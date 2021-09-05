/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Max Dor
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

package io.kamax.hboxc.gui.module;

import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hboxc.event.module.ServerModuleEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.action.RefreshAction;
import io.kamax.hboxc.gui.action.module.ModuleRegisterAction;
import io.kamax.hboxc.gui.builder.PopupMenuBuilder;
import io.kamax.hboxc.gui.worker.receiver._ModuleListReceiver;
import io.kamax.hboxc.gui.workers.ModuleListWorker;
import io.kamax.tools.helper.swing.MouseWheelController;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleListView implements _ModuleSelector, _Refreshable, _ModuleListReceiver {

    private String srvId;

    private JLabel statusLabel;
    private JProgressBar refreshProgress;
    private ModuleListTableModel itemListModel;
    private JTable itemList;
    private JScrollPane scrollPane;

    private JButton refreshButton;
    private JButton registerButton;
    private JPanel buttonPanel;

    private JPanel panel;

    public ModuleListView() {
        statusLabel = new JLabel();
        statusLabel.setVisible(false);
        refreshProgress = new JProgressBar();
        refreshProgress.setVisible(false);

        itemListModel = new ModuleListTableModel();
        itemList = new JTable(itemListModel);
        itemList.setAutoCreateRowSorter(true);
        itemList.setFillsViewportHeight(true);
        itemList.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
        itemList.addMouseListener(new ItemListMouseListener());

        scrollPane = new JScrollPane(itemList);
        MouseWheelController.install(scrollPane);

        refreshButton = new JButton(new RefreshAction(this));
        registerButton = new JButton(new ModuleRegisterAction(this));
        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(refreshButton);
        buttonPanel.add(registerButton);

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(statusLabel, "hidemode 3, growx, pushx, wrap");
        panel.add(refreshProgress, "hidemode 3, growx, pushx, wrap");
        panel.add(buttonPanel, "hidemode 3, growx, pushx, wrap");
        panel.add(scrollPane, "hidemode 3, grow, push, wrap");

        ViewEventManager.register(this);
    }

    public void show(String srvId) {
        if (srvId == null) {
            itemListModel.clear();
        } else {
            this.srvId = srvId;
            refresh();
        }
    }

    @Override
    public void refresh() {
        if (srvId != null) {
            ModuleListWorker.execute(this, srvId);
        }
    }

    @Override
    public String getServerId() {
        return srvId;
    }

    @Override
    public List<ModuleOut> getModuleSelection() {
        List<ModuleOut> listSelectedItems = new ArrayList<ModuleOut>();
        for (int row : itemList.getSelectedRows()) {
            listSelectedItems.add(itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(row)));
        }
        return listSelectedItems;
    }

    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void loadingStarted() {
        statusLabel.setText("Loading...");
        statusLabel.setVisible(true);
        refreshProgress.setIndeterminate(true);
        refreshProgress.setVisible(true);

        itemListModel.clear();
        itemList.setEnabled(false);
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        statusLabel.setVisible(!isSuccessful);
        if (!isSuccessful) {
            statusLabel.setText(t.getMessage());
        }
        refreshProgress.setIndeterminate(false);
        refreshProgress.setVisible(false);

        itemList.setEnabled(true);
    }

    @Override
    public void add(List<ModuleOut> objOutList) {
        itemListModel.add(objOutList);
    }

    @Handler
    public void putModuleEvent(ServerModuleEvent ev) {
        if (srvId.equals(ev.getServer().getId())) {
            refresh();
        }
    }

    private class ItemListMouseListener extends MouseAdapter {

        private void showPopup(MouseEvent ev) {
            if (ev.isPopupTrigger() && (itemList.getSelectedRow() > -1)) {
                JPopupMenu actions = PopupMenuBuilder.get(ModuleListView.this,
                        itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(itemList.getSelectedRow())));
                if (actions != null) {
                    actions.show(ev.getComponent(), ev.getX(), ev.getY());
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent ev) {
            showPopup(ev);
        }

        @Override
        public void mousePressed(MouseEvent ev) {
            showPopup(ev);
        }

        @Override
        public void mouseClicked(MouseEvent ev) {
            if ((ev.getButton() == MouseEvent.BUTTON1) && (itemList.rowAtPoint(ev.getPoint()) == -1)) {
                itemList.clearSelection();
            } else if ((ev.getButton() == MouseEvent.BUTTON1) && (ev.getClickCount() == 2) && (itemList.rowAtPoint(ev.getPoint()) != -1)) {
                // TODO show details
            } else {
                showPopup(ev);
            }
        }

    }

}
