/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 *
 * http://kamax.io/hbox/
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

import io.kamax.hbox.comm.in.SnapshotIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class SnapshotCreateDialog implements _Saveable, _Cancelable {

    private static SnapshotCreateDialog instance;
    private JDialog mainDialog;

    private JPanel mainPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel descLabel;
    private JTextArea descArea;

    private JPanel buttonsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private MachineOut mOut;
    private SnapshotIn snapIn;

    private void init(MachineOut mOut) {
        this.mOut = mOut;

        nameLabel = new JLabel("Name");
        nameField = new JTextField(40);
        descLabel = new JLabel("Description");
        descArea = new JTextArea();
        descArea.setBorder(nameField.getBorder());
        descArea.setLineWrap(true);
        descArea.setRows(10);

        mainPanel = new JPanel(new MigLayout());
        mainPanel.add(nameLabel);
        mainPanel.add(nameField, "growx,pushx,wrap");
        mainPanel.add(descLabel);
        mainPanel.add(descArea, "growx,pushx,wrap");

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));

        buttonsPanel = new JPanel(new MigLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainDialog = JDialogBuilder.get("Take new Snapshot", saveButton);
        mainDialog.getContentPane().setLayout(new MigLayout());
        mainDialog.getContentPane().add(mainPanel, "grow,push,wrap");
        mainDialog.getContentPane().add(buttonsPanel, "center, growx");
        mainDialog.getRootPane().setDefaultButton(saveButton);
    }

    public static void show(MachineOut mOut) {
        instance = new SnapshotCreateDialog();
        instance.init(mOut);

        instance.mainDialog.pack();
        instance.mainDialog.setLocationRelativeTo(instance.mainDialog.getParent());
        instance.mainDialog.setVisible(true);
    }

    private void hide() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
        instance = null;
    }

    @Override
    public void save() {
        snapIn = new SnapshotIn();
        snapIn.setName(nameField.getText());
        snapIn.setDescription(descArea.getText());

        Gui.getServer(mOut.getServerId()).getMachineReader(mOut.getUuid()).takeSnapshot(snapIn);

        hide();
    }

    @Override
    public void cancel() {
        hide();
    }

}
