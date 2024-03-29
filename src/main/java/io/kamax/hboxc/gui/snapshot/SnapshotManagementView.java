/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 *
 * https://apps.kamax.io/hyperbox
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.hboxc.gui.snapshot;

import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.hypervisor.SnapshotOut;
import io.kamax.hboxc.event.machine.MachineSnapshotDataChangedEvent;
import io.kamax.hboxc.event.machine.MachineStateChangedEvent;
import io.kamax.hboxc.event.snapshot.SnapshotDeletedEvent;
import io.kamax.hboxc.event.snapshot.SnapshotModifiedEvent;
import io.kamax.hboxc.event.snapshot.SnapshotRestoredEvent;
import io.kamax.hboxc.event.snapshot.SnapshotTakenEvent;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.action.snapshot.SnapshotDeleteAction;
import io.kamax.hboxc.gui.action.snapshot.SnapshotModifyAction;
import io.kamax.hboxc.gui.action.snapshot.SnapshotRestoreAction;
import io.kamax.hboxc.gui.action.snapshot.SnapshotTakeAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.tools.logging.KxLog;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnapshotManagementView implements _SnapshotSelector, _Refreshable {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private JPanel mainPanel;

    private JProgressBar refreshProgress;

    private JButton takeSnapButton;
    private JButton restoreSnapButton;
    private JButton delSnapButton;
    private JButton infoSnapButton;
    private JPanel buttonPanel;

    private DefaultMutableTreeNode topNode;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private JScrollPane treeView;
    private Map<String, DefaultMutableTreeNode> snapNodes;
    private DefaultMutableTreeNode currentStateNode;

    private String srvId;
    private String vmUuid;
    private String currentState;
    private boolean hasSnap;
    private String currentSnapUuid;
    private String rootSnapUuid;
    private Map<String, SnapshotOut> snaps;

    public SnapshotManagementView() {
        currentStateNode = new DefaultMutableTreeNode(new CurrentState());

        refreshProgress = new JProgressBar();
        refreshProgress.setVisible(false);

        takeSnapButton = new JButton(new SnapshotTakeAction(this));
        takeSnapButton.setIcon(IconBuilder.getTask(HypervisorTasks.SnapshotTake));
        restoreSnapButton = new JButton(new SnapshotRestoreAction(this));
        restoreSnapButton.setIcon(IconBuilder.getTask(HypervisorTasks.SnapshotRestore));
        delSnapButton = new JButton(new SnapshotDeleteAction(this));
        infoSnapButton = new JButton(new SnapshotModifyAction(this));
        infoSnapButton.setIcon(IconBuilder.getTask(HypervisorTasks.SnapshotGet));

        snapNodes = new HashMap<String, DefaultMutableTreeNode>();
        topNode = new DefaultMutableTreeNode("Snapshots");
        treeModel = new DefaultTreeModel(topNode);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new TreeCellRenderer());
        tree.addTreeSelectionListener(new TreeListener());
        tree.addMouseListener(new TreeMouseListener());
        treeView = new JScrollPane(tree);

        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(takeSnapButton);
        buttonPanel.add(restoreSnapButton);
        buttonPanel.add(delSnapButton);
        buttonPanel.add(infoSnapButton);

        mainPanel = new JPanel(new MigLayout());
        mainPanel.add(refreshProgress, "hidemode 3, growx, pushx, wrap");
        mainPanel.add(buttonPanel, "hidemode 3, growx, pushx, wrap");
        mainPanel.add(treeView, "hidemode 3, grow, push, wrap");

        ViewEventManager.register(this);
    }

    private boolean isSame(MachineOut mOut) {
        return (srvId != null) && (vmUuid != null) && srvId.equals(mOut.getServerId()) && vmUuid.equals(mOut.getUuid());
    }

    private void add(SnapshotOut snapOut) {
        if (!snaps.containsKey(snapOut.getUuid())) {
            snaps.put(snapOut.getUuid(), snapOut);
        }
    }

    private void process(String snapUuid) {
        SnapshotOut snapOut = Gui.getServer(srvId).getSnapshot(vmUuid, snapUuid);
        if (snapOut != null) {
            add(snapOut);
            for (String childUuid : snapOut.getChildrenUuid()) {
                process(childUuid);
            }
        }
    }

    private void setCurrentState() {
        if (currentStateNode.getParent() != null) {
            treeModel.removeNodeFromParent(currentStateNode);
        }

        DefaultMutableTreeNode parentNode = topNode;
        if ((currentSnapUuid != null) && snapNodes.containsKey(currentSnapUuid)) {
            parentNode = snapNodes.get(currentSnapUuid);
        }

        treeModel.insertNodeInto(currentStateNode, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(currentStateNode.getPath()));
    }

    public void show(MachineOut newVmOut) {
        if ((newVmOut != null) && ((vmUuid == null) || !vmUuid.equals(newVmOut.getUuid()))) {
            srvId = newVmOut.getServerId();
            vmUuid = newVmOut.getUuid();
            refresh();
        } else {
            // TODO refactor
        }
    }

    private void refreshCurrentState() {
        treeModel.reload(currentStateNode);
    }

    private void clearData() {
        currentState = null;
        hasSnap = false;
        currentSnapUuid = null;
        rootSnapUuid = null;
        snaps = new HashMap<String, SnapshotOut>();
    }

    private void refreshData() {
        clearData();

        MachineOut mOut = Gui.getServer(srvId).getMachine(vmUuid);
        currentState = mOut.getState();
        hasSnap = mOut.hasSnapshots();
        if (hasSnap) {
            currentSnapUuid = mOut.getCurrentSnapshot();
            rootSnapUuid = Gui.getServer(srvId).getRootSnapshot(vmUuid).getUuid();
            process(rootSnapUuid);
        }
    }

    private void addDisplay(MutableTreeNode parentNode, String snapUuid) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(snaps.get(snapUuid));
        snapNodes.put(snapUuid, node);
        treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
        tree.scrollPathToVisible(new TreePath(node.getPath()));
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        for (String childUuid : snaps.get(snapUuid).getChildrenUuid()) {
            addDisplay(node, childUuid);
        }
    }

    private void refreshDisplay() {
        clear();
        if (hasSnap) {
            addDisplay(topNode, rootSnapUuid);
        }
        setCurrentState();
    }

    @Override
    public void refresh() {
        log.debug("Refreshing Snapshot Data for " + vmUuid);

        refreshProgress.setIndeterminate(true);
        refreshProgress.setVisible(true);
        tree.setEnabled(false);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                refreshData();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    refreshDisplay();
                } catch (Throwable t) {
                    log.error("Tracing Exception", t);
                } finally {
                    refreshProgress.setIndeterminate(false);
                    refreshProgress.setVisible(false);
                    tree.setEnabled(true);
                }
            }
        }.execute();
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    private void clear() {
        takeSnapButton.setEnabled(false);
        restoreSnapButton.setEnabled(false);
        delSnapButton.setEnabled(false);
        infoSnapButton.setEnabled(false);
        currentStateNode.removeFromParent();
        snapNodes.clear();
        topNode.removeAllChildren();
        treeModel.reload();
    }

    private class TreeListener implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent ev) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            takeSnapButton.setEnabled(false);
            restoreSnapButton.setEnabled(false);
            delSnapButton.setEnabled(false);
            infoSnapButton.setEnabled(false);

            if (node != null) {
                if (node.getUserObject() instanceof CurrentState) {
                    takeSnapButton.setEnabled(true);
                    restoreSnapButton.setEnabled(false);
                    delSnapButton.setEnabled(false);
                    infoSnapButton.setEnabled(false);
                }
                if (node.getUserObject() instanceof SnapshotOut) {
                    takeSnapButton.setEnabled(false);
                    restoreSnapButton.setEnabled(true);
                    delSnapButton.setEnabled(true);
                    infoSnapButton.setEnabled(true);
                }
            }
        }

    }


    private class TreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = -4333779603957385968L;

        @Override
        public Component getTreeCellRendererComponent(JTree rawTree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            super.getTreeCellRendererComponent(rawTree, value, isSelected, isExpanded, isLeaf, row, hasFocus);

            if (node.getUserObject() instanceof SnapshotOut) {
                SnapshotOut snapOut = (SnapshotOut) node.getUserObject();
                setText(snapOut.getName());
                setIcon(IconBuilder.getSnapshot(snapOut));
            }
            if (node.getUserObject() instanceof CurrentState) {
                setIcon(IconBuilder.getMachineState(currentState));
            }

            return this;
        }
    }

    private class TreeMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
            if (node != null) {
                if ((e.getClickCount() == 2) && (node.getUserObject() instanceof SnapshotOut)) {
                    // TODO SnapshotModifyDialog.show(mOut, ((SnapshotOutput) node.getUserObject()));
                }
            } else {
                // TODO clear selection
            }
        }

    }

    @Override
    public List<SnapshotOut> getSelection() {
        List<SnapshotOut> snapOutList = new ArrayList<SnapshotOut>();
        for (TreePath path : tree.getSelectionPaths()) {
            snapOutList.add(((SnapshotOut) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()));
        }
        return snapOutList;
    }

    @Handler
    private void putMachineStateChanged(MachineStateChangedEvent ev) {
        if (isSame(ev.getMachine())) {
            currentState = ev.getMachine().getState();
            refreshCurrentState();
        }
    }

    @Handler
    private void putMachineSnapshotDataChangeEvent(MachineSnapshotDataChangedEvent ev) {
        if (isSame(ev.getMachine())) {
            refresh();
        }
    }

    @Handler
    private void putSnapshotTakenEvent(SnapshotTakenEvent ev) {
        if (isSame(ev.getMachine())) {
            refresh();
        }
    }

    @Handler
    private void putSnapshotDeletedEvent(SnapshotDeletedEvent ev) {
        if (isSame(ev.getMachine())) {
            refresh();
        }
    }

    @Handler
    private void putSnapshotModifiedEvent(SnapshotModifiedEvent ev) {
        if (isSame(ev.getMachine())) {
            refresh();
        }
    }

    @Handler
    private void putSnapshotRestoredEvent(SnapshotRestoredEvent ev) {
        if (isSame(ev.getMachine())) {
            refresh();
        }
    }

    @Override
    public MachineOut getMachine() {
        return new MachineOut(srvId, vmUuid);
    }

}
