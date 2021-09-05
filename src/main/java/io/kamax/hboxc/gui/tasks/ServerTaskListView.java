/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.tasks;

import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hboxc.event.connector.ConnectorStateChangedEvent;
import io.kamax.hboxc.event.task.TaskAddedEvent;
import io.kamax.hboxc.event.task.TaskRemovedEvent;
import io.kamax.hboxc.event.task.TaskStateChangedEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.action.task.TaskCancelAction;
import io.kamax.hboxc.gui.worker.receiver._TaskListReceiver;
import io.kamax.hboxc.gui.workers.TaskListWorker;
import io.kamax.tools.AxStrings;
import io.kamax.tools.helper.swing.MouseWheelController;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerTaskListView implements _TaskSelector, _Refreshable {

    private String srvId;

    private JLabel loadingLabel = new JLabel("Loading...");
    private ServerTaskListTableModel itemListModel;
    private JTable itemList;
    private JScrollPane scrollPane;
    private JPanel panel;
    private JScrollPane pane;

    private JPopupMenu actions;

    public ServerTaskListView() {
        itemListModel = new ServerTaskListTableModel();
        itemList = new JTable(itemListModel);
        itemList.setFillsViewportHeight(true);
        itemList.setAutoCreateRowSorter(true);
        // Sort by ID descending, so the newest queued task is always on top
        itemList.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(4, SortOrder.DESCENDING)));
        itemList.addMouseListener(new ItemListMouseListener());

        loadingLabel.setVisible(false);

        scrollPane = new JScrollPane(itemList);

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(loadingLabel, "hidemode 3, growx, pushx, wrap");
        panel.add(scrollPane, "hidemode 3, grow, push, wrap");

        pane = new JScrollPane(panel);
        pane.setBorder(BorderFactory.createEmptyBorder());
        MouseWheelController.install(pane);

        actions = new JPopupMenu();
        actions.add(new JMenuItem(new TaskCancelAction(this)));

        ViewEventManager.register(this);
    }

    public JComponent getComponent() {
        return pane;
    }

    private class ItemListMouseListener extends MouseAdapter {

        private void popupHandle(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                // TODO enable when cancel task is possible
                //actions.show(ev.getComponent(), ev.getX(), ev.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent ev) {
            popupHandle(ev);
        }

        @Override
        public void mousePressed(MouseEvent ev) {
            popupHandle(ev);
        }

        @Override
        public void mouseClicked(MouseEvent ev) {
            if (ev.getButton() == MouseEvent.BUTTON1) {
                if (itemList.rowAtPoint(ev.getPoint()) == -1) {
                    itemList.clearSelection();
                }
                if ((ev.getClickCount() == 2) && (itemList.getSelectedRow() > -1)) {
                    TaskOut tOut = itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(itemList.getSelectedRow()));
                    TaskView.show(tOut);
                }
            } else {
                popupHandle(ev);
            }
        }

    }

    @Override
    public List<TaskOut> getSelection() {
        List<TaskOut> listSelectedItems = new ArrayList<TaskOut>();
        for (int row : itemList.getSelectedRows()) {
            listSelectedItems.add(itemListModel.getObjectAtRow(itemList.convertRowIndexToModel(row)));
        }
        return listSelectedItems;
    }

    public void refresh(String srvId) {
        this.srvId = srvId;
        refresh();
    }

    @Override
    public void refresh() {
        if (srvId != null) {
            TaskListWorker.execute(new TaskListReceiver(), srvId);
        }
    }

    private class TaskListReceiver implements _TaskListReceiver {

        @Override
        public void loadingStarted() {
            loadingLabel.setVisible(true);
            panel.setEnabled(false);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            itemListModel.clear();
        }

        private void finish() {
            panel.setEnabled(false);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable message) {

            finish();
            if (isSuccessful) {
                loadingLabel.setVisible(false);
            } else {
                loadingLabel.setText("Error when loading tasks: " + message.getMessage());
            }
        }

        @Override
        public void add(List<TaskOut> tOutList) {

            itemListModel.merge(tOutList);
        }
    }

    @Handler
    private void putConnectorStateEvent(ConnectorStateChangedEvent ev) {
        if (AxStrings.equals(srvId, ev.getConnector().getServerId())) {
            refresh();
        }
    }

    @Handler
    private void putTaskAddedEvent(TaskAddedEvent ev) {
        if (ev.getServer().getId().equals(srvId)) {
            add(ev.getTask());
        }
    }

    @Handler
    private void putTaskRemovedEvent(TaskRemovedEvent ev) {
        if (ev.getServer().getId().equals(srvId)) {
            remove(ev.getTask());
        }
    }

    @Handler
    private void putTaskStateEvent(TaskStateChangedEvent ev) {
        if (ev.getServer().getId().equals(srvId)) {
            update(ev.getTask());
        }
    }

    private void add(TaskOut tOut) {
        itemListModel.add(tOut);
    }

    private void update(TaskOut tOut) {
        itemListModel.update(tOut);
    }

    private void remove(TaskOut tOut) {
        itemListModel.remove(tOut);
    }

}
