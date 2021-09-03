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

package io.kamax.hboxc.gui.settings;

import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.event.consoleviewer.ConsoleViewerEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui.action.ConsoleViewerCreateAction;
import io.kamax.hboxc.gui.action.ConsoleViewerRemoveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.vm.console.viewer.ConsoleViewerEditor;
import io.kamax.hboxc.gui.vm.console.viewer.ConsoleViewerTableModel;
import io.kamax.hboxc.gui.vm.console.viewer._ConsoleViewerSelector;
import io.kamax.hboxc.gui.worker.receiver._ConsoleViewerListReceiver;
import io.kamax.hboxc.gui.workers.ConsolerViewerListWorker;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ConsoleViewersSettingsViewer implements _ConsoleViewerSelector, _ConsoleViewerListReceiver {

    private JPanel panel;

    private ConsoleViewerTableModel consViewerTableModel;
    private JTable consViewerTable;
    private JScrollPane consViewerTablePane;

    private JButton addButton;
    private JButton remButton;
    private JLabel loadingLabel;
    private JPanel buttonPanel;

    public ConsoleViewersSettingsViewer() {

        ViewEventManager.register(this);

        consViewerTableModel = new ConsoleViewerTableModel();
        consViewerTable = new JTable(consViewerTableModel);
        consViewerTable.addMouseListener(new BrowseMouseListener());
        consViewerTable.setShowGrid(true);
        consViewerTable.setFillsViewportHeight(true);
        consViewerTable.setAutoCreateRowSorter(true);
        consViewerTablePane = new JScrollPane(consViewerTable);

        addButton = new JButton(new ConsoleViewerCreateAction());
        remButton = new JButton(new ConsoleViewerRemoveAction(this));
        loadingLabel = new JLabel(IconBuilder.LoadingIcon);
        loadingLabel.setVisible(false);

        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(addButton);
        buttonPanel.add(remButton);
        buttonPanel.add(loadingLabel, "growx,pushx,wrap,hidemode 3");

        panel = new JPanel(new MigLayout());
        panel.add(consViewerTablePane, "grow,push,wrap");
        panel.add(buttonPanel, "growx,pushx,wrap");
    }

    public JComponent getComponet() {
        return panel;
    }

    private class BrowseMouseListener extends MouseAdapter {

        private ConsoleViewerOutput getSelection() {
            if (consViewerTable.getSelectedRow() == -1) {
                return null;
            } else {
                return consViewerTableModel.getObjectAtRowId(consViewerTable.convertRowIndexToModel(consViewerTable.getSelectedRow()));
            }
        }

        @Override
        public void mouseClicked(MouseEvent ev) {
            if (ev.getClickCount() == 2) {
                ConsoleViewerOutput cvOut = getSelection();
                if (cvOut != null) {
                    ConsoleViewerEditor.edit(cvOut);
                }
            }
        }
    }

    public void load() {
        ConsolerViewerListWorker.execute(this);
    }

    @Override
    public List<ConsoleViewerOutput> getConsoleViewers() {
        List<ConsoleViewerOutput> listOut = new ArrayList<ConsoleViewerOutput>();
        for (int rowId : consViewerTable.getSelectedRows()) {
            listOut.add(consViewerTableModel.getObjectAtRowId(consViewerTable.convertRowIndexToModel(rowId)));
        }
        return listOut;
    }

    @Handler
    public void putConsoleViewerEvent(ConsoleViewerEvent ev) {
        load();
    }

    @Override
    public void loadingStarted() {
        addButton.setEnabled(false);
        remButton.setEnabled(false);
        loadingLabel.setIcon(IconBuilder.LoadingIcon);
        consViewerTableModel.clear();
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        loadingLabel.setVisible(!isSuccessful);
        addButton.setEnabled(isSuccessful);
        remButton.setEnabled(isSuccessful);
        if (!isSuccessful) {
            loadingLabel.setIcon(null);
            loadingLabel.setText(t.getMessage());
        }
    }

    @Override
    public void add(List<ConsoleViewerOutput> objOutList) {
        consViewerTableModel.add(objOutList);
    }

}
