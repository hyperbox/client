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

package io.kamax.hboxc.gui.store;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.StoreOut;
import io.kamax.hbox.comm.out.event.store.StoreEventOut;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.action.store.StoreCreateAction;
import io.kamax.hboxc.gui.action.store.StoreRegisterAction;
import io.kamax.hboxc.gui.builder.PopupMenuBuilder;
import io.kamax.hboxc.gui.server._SingleServerSelector;
import io.kamax.hboxc.gui.store.utils.StoreItemChooser;
import io.kamax.hboxc.gui.worker.receiver._StoreListReceiver;
import io.kamax.hboxc.gui.workers.StoreListWorker;
import io.kamax.tools.helper.swing.MouseWheelController;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StoreListView implements _StoreSelector, _Refreshable, _SingleServerSelector, _StoreListReceiver {

    private ServerOut srvOut;

    private JLabel statusLabel;

    private StoreListTableModel itemListModel;
    private JTable itemList;
    private JScrollPane scrollPane;

    private JButton addButton;
    private JButton registerButton;
    private JPanel buttonPanel;

    private JPanel panel;

    public StoreListView() {

        statusLabel = new JLabel();
        statusLabel.setVisible(false);

        itemListModel = new StoreListTableModel();
        itemList = new JTable(itemListModel);
        itemList.setAutoCreateRowSorter(true);
        itemList.setFillsViewportHeight(true);
        itemList.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));
        itemList.addMouseListener(new ItemListMouseListener());
        scrollPane = new JScrollPane(itemList);
        MouseWheelController.install(scrollPane);

        addButton = new JButton(new StoreCreateAction(this));
        registerButton = new JButton(new StoreRegisterAction(this));
        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(addButton);
        buttonPanel.add(registerButton);

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(statusLabel, "hidemode 3, growx, pushx, wrap");
        panel.add(buttonPanel, "hidemode 3, growx, pushx, wrap");
        panel.add(scrollPane, "hidemode 3, grow, push, wrap");

        ViewEventManager.register(this);
    }

    public JComponent getComponent() {
        return panel;
    }

    @Override
    public List<String> getSelection() {
        List<String> listSelectedItems = new ArrayList<String>();
        for (int row : itemList.getSelectedRows()) {
            listSelectedItems.add(itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(row)).getId());
        }
        return listSelectedItems;
    }

    @Handler
    public void postStoreState(StoreEventOut event) {
        refresh();
    }

    private class ItemListMouseListener extends MouseAdapter {

        private void showPopup(MouseEvent ev) {
            if (ev.isPopupTrigger() && (itemList.getSelectedRow() > -1)) {
                JPopupMenu actions = PopupMenuBuilder.get(StoreListView.this,
                        itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(itemList.getSelectedRow())));
                actions.show(ev.getComponent(), ev.getX(), ev.getY());
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
                // StoreOutput stoOut = itemListModel.getObjectAtRowId(itemList.convertRowIndexToModel(itemList.rowAtPoint(ev.getPoint())));
                StoreItemChooser.browse(srvOut.getId());
            } else {
                showPopup(ev);
            }
        }

    }

    public void show(ServerOut srvOut) {
        if (srvOut == null) {
            itemListModel.clear();
        } else {
            this.srvOut = srvOut;
            refresh();
        }
    }

    @Override
    public ServerOut getServer() {
        return srvOut;
    }

    @Override
    public void refresh() {
        StoreListWorker.execute(this, srvOut.getId());
    }

    @Override
    public void loadingStarted() {
        itemListModel.clear();
        statusLabel.setText("Loading...");
        statusLabel.setVisible(true);
        buttonPanel.setEnabled(false);
        scrollPane.setEnabled(false);
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable message) {
        statusLabel.setVisible(!isSuccessful);
        if (!isSuccessful) {
            statusLabel.setText(message.getMessage());
        }
        buttonPanel.setEnabled(isSuccessful);
        scrollPane.setEnabled(isSuccessful);
    }

    @Override
    public void add(List<StoreOut> stoOutList) {
        itemListModel.merge(stoOutList);
    }

}
