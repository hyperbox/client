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

import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.hypervisor.OsTypeOut;
import io.kamax.hbox.constant.MachineAttribute;
import io.kamax.hboxc.HyperboxClient;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.worker.receiver._KeyboardTypeListReceiver;
import io.kamax.hboxc.gui.worker.receiver._OsTypeListReceiver;
import io.kamax.hboxc.gui.workers.KeyboardTypeListWorker;
import io.kamax.hboxc.gui.workers.OsTypeListWorker;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GeneralVmEdit {

    private _WorkerTracker tracker;

    private JPanel panel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel osTypeLabel;
    private JComboBox<String> osTypeField;
    private JLabel snapshotFolderLabel;
    private JTextField snapshotFolderField;
    private JLabel keyboardTypeLabel;
    private JComboBox<String> keyboardTypeBox;
    private JLabel mouseTypeLabel;
    private JComboBox<String> mouseTypeBox;
    private JLabel descLabel;
    private JTextArea descArea;

    private MachineOut mOut;
    private MachineIn mIn;

    public GeneralVmEdit(_WorkerTracker tracker) {
        this.tracker = tracker;

        nameLabel = new JLabel("Name");
        nameField = new JTextField();

        osTypeLabel = new JLabel("OS Type");
        osTypeField = new JComboBox<>();

        snapshotFolderLabel = new JLabel("Snapshot Folder");
        snapshotFolderField = new JTextField();

        keyboardTypeLabel = new JLabel("Keyboard Type");
        keyboardTypeBox = new JComboBox<>();

        mouseTypeLabel = new JLabel("Mouse Type");
        mouseTypeBox = new JComboBox<>();

        descLabel = new JLabel("Description");
        descArea = new JTextArea();

        panel = new JPanel(new MigLayout());
        panel.add(nameLabel);
        panel.add(nameField, "growx,pushx,wrap");
        panel.add(osTypeLabel);
        panel.add(osTypeField, "growx,pushx,wrap");
        panel.add(snapshotFolderLabel);
        panel.add(snapshotFolderField, "growx,pushx,wrap");
        panel.add(keyboardTypeLabel);
        panel.add(keyboardTypeBox, "growx,pushx,wrap");
        panel.add(mouseTypeLabel);
        panel.add(mouseTypeBox, "growx,pushx,wrap");
        panel.add(descLabel);
        panel.add(descArea, "growx,pushx,wrap");
    }

    public Component getComp() {
        return panel;
    }

    public void update(MachineOut mOut, MachineIn mIn) {
        this.mIn = mIn;
        this.mOut = mOut;

        nameField.setText(mOut.getName());
        descArea.setText(mOut.getSetting(MachineAttribute.Description).getString());

        KeyboardTypeListWorker.execute(tracker, new KeyboardListReceiver(), mOut.getServerId(), mOut.getUuid());

        try {
            mouseTypeBox.removeAllItems();
            for (String mouse : Gui.getServer(mOut.getServerId()).listMouseMode(new MachineIn(mOut))) {
                mouseTypeBox.addItem(mouse);
            }
            mouseTypeBox.setSelectedItem(mOut.getSetting(MachineAttribute.MouseMode).getRawValue());
        } catch (Throwable t) {
            HyperboxClient.getView().postError("Unable to retrieve list of Mouse modes", t);
        }
        OsTypeListWorker.execute(tracker, new OsTypeLoader(), mOut);
    }

    public void save() {
        if (!nameField.getText().contentEquals(mOut.getName())) {
            mIn.setName(nameField.getText());
        }
        if (!osTypeField.getSelectedItem().toString().contentEquals(mOut.getSetting(MachineAttribute.OsType).getString())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.OsType, osTypeField.getSelectedItem().toString()));
        }
        if (!keyboardTypeBox.getSelectedItem().toString().contentEquals(mOut.getSetting(MachineAttribute.KeyboardMode).getString())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.KeyboardMode, keyboardTypeBox.getSelectedItem().toString()));
        }
        if (!mouseTypeBox.getSelectedItem().toString().contentEquals(mOut.getSetting(MachineAttribute.MouseMode).getString())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.MouseMode, mouseTypeBox.getSelectedItem().toString()));
        }
        if (!descArea.getText().contentEquals(mOut.getSetting(MachineAttribute.Description).getString())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.Description, descArea.getText()));
        }
    }

    private class OsTypeLoader implements _OsTypeListReceiver {

        @Override
        public void loadingStarted() {
            osTypeField.removeAllItems();
            osTypeField.addItem("Loading...");
            osTypeField.setSelectedItem("Loading...");
            osTypeField.setEnabled(false);
        }

        @Override
        public void loadingFinished(boolean isSuccess, Throwable message) {
            osTypeField.setEnabled(true);
            if (isSuccess) {
                osTypeField.setSelectedItem(mOut.getSetting(MachineAttribute.OsType).getRawValue());
                osTypeField.removeItem("Loading...");
            } else {
                osTypeField.removeAllItems();
                osTypeField.addItem("Failed to load: " + message.getMessage());
            }
        }

        @Override
        public void add(List<OsTypeOut> ostOuttList) {
            for (OsTypeOut osOut : ostOuttList) {
                osTypeField.addItem(osOut.getId());
            }
        }

    }

    private class KeyboardListReceiver implements _KeyboardTypeListReceiver {

        @Override
        public void loadingStarted() {
            keyboardTypeBox.setEnabled(false);
            keyboardTypeBox.removeAllItems();
            keyboardTypeBox.addItem("Loading...");
            keyboardTypeBox.setSelectedItem("Loading...");
        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable message) {
            keyboardTypeBox.removeItem("Loading...");
            keyboardTypeBox.setEnabled(isSuccessful);
            if (isSuccessful) {
                keyboardTypeBox.setSelectedItem(mOut.getSetting(MachineAttribute.KeyboardMode).getRawValue());
            } else {
                keyboardTypeBox.removeAllItems();
                keyboardTypeBox.addItem("Failed to load Keyboard Types list: " + message.getMessage());
            }

        }

        @Override
        public void add(List<String> keyboardList) {
            for (String keyboard : keyboardList) {
                keyboardTypeBox.addItem(keyboard);
            }

        }

    }

}
