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

package io.kamax.hboxc.gui.vm.edit;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._MachineReceiver;
import io.kamax.hboxc.gui.workers.MachineGetWorker;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import io.kamax.tools.logging.KxLog;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class VmEditDialog implements _Saveable, _Cancelable, _WorkerTracker {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private final String GENERAL = "General";
    private final String SYSTEM = "System";
    private final String OUTPUT = "Output";
    private final String STORAGE = "Storage";
    private final String AUDIO = "Audio";
    private final String NETWORK = "Network";

    private JDialog mainDialog;
    private JProgressBar refreshProgress;
    private boolean loadingFailed = false;
    private List<SwingWorker<?, ?>> remainingWorkers = new ArrayList<SwingWorker<?, ?>>();
    private List<SwingWorker<?, ?>> finishedWorkers = new ArrayList<SwingWorker<?, ?>>();

    private JSplitPane split;

    private JPanel buttonsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private JScrollPane leftPane;
    private JScrollPane rightPane;

    private DefaultListModel<String> listModel;
    private JList<String> itemList;

    private JPanel sectionPanels;
    private CardLayout layout;

    private GeneralVmEdit generalEdit;
    private SystemVmEdit systemEdit;
    private OutputVmEdit outputEdit;
    private StorageVmEdit storageEdit;
    private AudioVmEdit audioEdit;
    private NetworkVmEdit networkEdit;

    private MachineIn mIn;

    public VmEditDialog() {
        generalEdit = new GeneralVmEdit(this);
        systemEdit = new SystemVmEdit(this);
        outputEdit = new OutputVmEdit(this);
        storageEdit = new StorageVmEdit(this);
        audioEdit = new AudioVmEdit(this);
        networkEdit = new NetworkVmEdit(this);

        layout = new CardLayout();
        sectionPanels = new JPanel(layout);

        sectionPanels.add(generalEdit.getComp(), GENERAL);
        sectionPanels.add(systemEdit.getComp(), SYSTEM);
        sectionPanels.add(outputEdit.getComp(), OUTPUT);
        sectionPanels.add(storageEdit.getComp(), STORAGE);
        sectionPanels.add(audioEdit.getComp(), AUDIO);
        sectionPanels.add(networkEdit.getComp(), NETWORK);

        listModel = new DefaultListModel<>();
        listModel.addElement(GENERAL);
        listModel.addElement(SYSTEM);
        listModel.addElement(OUTPUT);
        listModel.addElement(STORAGE);
        listModel.addElement(AUDIO);
        listModel.addElement(NETWORK);

        itemList = new JList<>(listModel);
        itemList.setCellRenderer(new LabelCellRenderer());
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(new ListSelect());
        if (!listModel.isEmpty()) {
            itemList.setSelectedValue(listModel.getElementAt(0), true);
        }

        leftPane = new JScrollPane(itemList);
        leftPane.setMinimumSize(new Dimension(100, 50));
        rightPane = new JScrollPane(sectionPanels);
        rightPane.setMinimumSize(new Dimension(300, 100));

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        split.setBorder(null);

        saveButton = new JButton(new SaveAction(this));
        saveButton.setEnabled(false);
        cancelButton = new JButton(new CancelAction(this));
        refreshProgress = new JProgressBar();
        refreshProgress.setVisible(false);
        refreshProgress.setStringPainted(true);

        buttonsPanel = new JPanel(new MigLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(refreshProgress, "growx,pushx");

        mainDialog = JDialogBuilder.get(saveButton);
        mainDialog.setIconImage(IconBuilder.getTask(HypervisorTasks.MachineModify).getImage());
        mainDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
        mainDialog.setSize(1000, 600);
        mainDialog.getContentPane().setLayout(new MigLayout());
        mainDialog.getContentPane().add(split, "grow,push,wrap");
        mainDialog.getContentPane().add(buttonsPanel, "grow x");

    }

    private void show(MachineOut mOut) {
        mIn = new MachineIn(mOut);
        MachineGetWorker.execute(this, new MachineReceiver(), mOut);
        mainDialog.setTitle(mOut.getName() + " - Settings");
        mainDialog.setLocationRelativeTo(mainDialog.getParent());
        mainDialog.setVisible(true);
    }

    public static void edit(MachineOut mOut) {
        new VmEditDialog().show(mOut);
    }

    private void hide() {
        itemList.clearSelection();
        mainDialog.setVisible(false);
        mainDialog.dispose();
        mIn = null;
    }

    @Override
    public void save() {
        generalEdit.save();
        systemEdit.save();
        outputEdit.save();
        storageEdit.save();
        audioEdit.save();
        networkEdit.save();

        MessageWorker.execute(new Request(Command.VBOX, HypervisorTasks.MachineModify, mIn), new AnswerWorkerReceiver() {

            private Action initialAction;

            @Override
            public void start() {
                initialAction = saveButton.getAction();
                saveButton.setAction(LoadingAction.get());
                mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            @Override
            public void loadingFinished(boolean isSuccessful, Throwable t) {
                mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                if (!isSuccessful) {
                    saveButton.setAction(initialAction);
                    fail(t);
                } else {
                    hide();
                }
            }

        });

    }

    @Override
    public void cancel() {
        hide();
    }

    private class ListSelect implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent lsEv) {
            if (itemList.getSelectedValue() != null) {
                layout.show(sectionPanels, itemList.getSelectedValue().toString());
            }
        }

    }


    private class LabelCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 3884145295010503208L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (label.getText() == GENERAL) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Machine));
            } else if (label.getText() == SYSTEM) {
                label.setIcon(IconBuilder.getEntityType(EntityType.CPU));
            } else if (label.getText() == OUTPUT) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Display));
            } else if (label.getText() == STORAGE) {
                label.setIcon(IconBuilder.getEntityType(EntityType.HardDisk));
            } else if (label.getText() == AUDIO) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Audio));
            } else if (label.getText() == NETWORK) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Network));
            } else {
                label.setIcon(null);
            }

            return label;
        }

    }

    private class MachineReceiver implements _MachineReceiver {

        @Override
        public void loadingStarted() {
            saveButton.setEnabled(false);
            mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            refreshProgress.setIndeterminate(true);
            refreshProgress.setVisible(true);
        }

        @Override
        public void loadingFinished(boolean isFinished, Throwable message) {
            refreshProgress.setMinimum(0);
            refreshProgress.setMaximum(100);
            refreshProgress.setValue(0);
            refreshProgress.setIndeterminate(false);
        }

        @Override
        public void put(final MachineOut mOut) {
            generalEdit.update(mOut, mIn);
            systemEdit.update(mOut, mIn);
            outputEdit.update(mOut, mIn);
            storageEdit.update(mOut, mIn);
            audioEdit.update(mOut, mIn);
            networkEdit.update(mOut, mIn);
        }

    }

    private void refreshLoadingStatus() {
        int remaining = remainingWorkers.size();
        int finished = finishedWorkers.size();
        int total = remaining + finished;

        refreshProgress.setMinimum(0);
        refreshProgress.setMaximum(total);
        refreshProgress.setValue(finished);
        saveButton.setEnabled(remainingWorkers.isEmpty() && !loadingFailed);
        refreshProgress.setVisible(!remainingWorkers.isEmpty());

        if (remainingWorkers.isEmpty()) {
            mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            saveButton.setEnabled(!loadingFailed);
            finishedWorkers.clear();
        } else {
            mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            refreshProgress.setIndeterminate(finishedWorkers.isEmpty());
        }
    }

    @Override
    public AxSwingWorker<?, ?, ?> register(AxSwingWorker<?, ?, ?> worker) {
        if (worker.isDone()) {
            log.debug("Skipping registration of already finished worker: " + worker);
        }

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent ev) {
                AxSwingWorker<?, ?, ?> worker = AxSwingWorker.class.cast(ev.getSource());

                if (worker.hasFailed()) {
                    loadingFailed = true;
                }
                if ("state".equals(ev.getPropertyName()) && (SwingWorker.StateValue.DONE == ev.getNewValue())) {
                    remainingWorkers.remove(ev.getSource());
                    finishedWorkers.add((SwingWorker<?, ?>) ev.getSource());
                    refreshLoadingStatus();
                }
            }
        });

        remainingWorkers.add(worker);
        refreshLoadingStatus();
        return worker;
    }
}
