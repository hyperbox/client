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

import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.gui.MainView;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class SettingsManager implements _Saveable, _Cancelable {

    private final String GENERAL = "General";
    private final String VIEWERS = "Viewers";

    private JDialog mainDialog;

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

    private GeneralSettingsViewer generalEdit;
    private ConsoleViewersSettingsViewer viewerEdit;

    public static SettingsManager show() {
        return new SettingsManager();
    }

    public SettingsManager() {
        generalEdit = new GeneralSettingsViewer();
        generalEdit.load();
        viewerEdit = new ConsoleViewersSettingsViewer();
        viewerEdit.load();

        layout = new CardLayout();
        sectionPanels = new JPanel(layout);

        sectionPanels.add(generalEdit.getComponet(), GENERAL);
        sectionPanels.add(viewerEdit.getComponet(), VIEWERS);

        listModel = new DefaultListModel<>();
        //listModel.addElement(GENERAL);
        listModel.addElement(VIEWERS);

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
        cancelButton = new JButton(new CancelAction(this));

        buttonsPanel = new JPanel(new MigLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainDialog = new JDialog(MainView.getMainFrame());
        mainDialog = JDialogBuilder.get("Configuration", IconBuilder.getTask(HypervisorTasks.MachineModify).getImage(), saveButton);
        mainDialog.setSize(1000, 600);
        mainDialog.getContentPane().setLayout(new MigLayout());
        mainDialog.getContentPane().add(split, "grow,push,wrap");
        mainDialog.getContentPane().add(buttonsPanel, "grow x");
        mainDialog.setLocationRelativeTo(mainDialog.getParent());
        mainDialog.setVisible(true);
    }

    @Override
    public void cancel() {
        mainDialog.setVisible(false);
    }

    @Override
    public void save() {
        mainDialog.setVisible(false);
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

        private static final long serialVersionUID = -3013644638595005392L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (label.getText() == GENERAL) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Machine));
            } else if (label.getText() == VIEWERS) {
                label.setIcon(IconBuilder.getEntityType(EntityType.Display));
            } else {
                label.setIcon(null);
            }

            return label;
        }

    }

}
