/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 - Maxime Dor
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

package io.kamax.hboxc.gui.vm.edit;

import io.kamax.hbox.comm.in.ConsoleIn;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.io.BooleanSettingIO;
import io.kamax.hbox.comm.io.PositiveNumberSettingIO;
import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.constant.MachineAttribute;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class OutputVmEdit {

    private MachineIn mIn;
    private MachineOut mOut;

    private JPanel consolePanel;
    private JPanel displayPanel;
    private JPanel panel;

    private JLabel enableLabel;
    private JLabel portLabel;
    private JLabel addressLabel;
    private JLabel authTypeLabel;
    private JLabel authTimeoutLabel;
    private JLabel allowMultiConnLabel;
    private JLabel vncPassLabel;
    private JLabel vramLabel;
    private JLabel monitorCountLabel;
    private JLabel accel2dLabel;
    private JLabel accel3dLabel;

    private JCheckBox enableValue;
    private JTextField portValue;
    private JTextField addressValue;
    private JTextField authTypeValue;
    private JTextField authTimeoutValue;
    private JCheckBox allowMultiConnValue;
    private JTextField vncPassField;
    private JTextField vramField;
    private JTextField monitorCountField;
    private JCheckBox accel2dBox;
    private JCheckBox accel3dBox;

    public OutputVmEdit(_WorkerTracker tracker) {
        enableLabel = new JLabel("Enabled");
        portLabel = new JLabel("Port");
        addressLabel = new JLabel("Address");
        authTypeLabel = new JLabel("Authentication Type");
        authTimeoutLabel = new JLabel("Authentication Timeout");
        allowMultiConnLabel = new JLabel("Allow Multi Connections");
        vncPassLabel = new JLabel("VNC Password");

        enableValue = new JCheckBox();
        portValue = new JTextField();
        addressValue = new JTextField();
        authTypeValue = new JTextField();
        authTimeoutValue = new JTextField();
        allowMultiConnValue = new JCheckBox();
        vncPassField = new JTextField();

        vramLabel = new JLabel("VRAM");
        vramField = new JTextField();
        monitorCountLabel = new JLabel("Monitors");
        monitorCountField = new JTextField();
        accel2dLabel = new JLabel("2D Acceleration");
        accel2dBox = new JCheckBox();
        accel3dLabel = new JLabel("3D Acceleration");
        accel3dBox = new JCheckBox();

        displayPanel = new JPanel(new MigLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Graphics"));
        displayPanel.add(vramLabel);
        displayPanel.add(vramField, "growx,pushx,wrap");
        displayPanel.add(monitorCountLabel);
        displayPanel.add(monitorCountField, "growx,pushx,wrap");
        displayPanel.add(accel2dLabel);
        displayPanel.add(accel2dBox, "growx,pushx,wrap");
        displayPanel.add(accel3dLabel);
        displayPanel.add(accel3dBox, "growx,pushx,wrap");

        consolePanel = new JPanel(new MigLayout());
        consolePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Console"));
        consolePanel.add(enableLabel);
        consolePanel.add(enableValue, "growx, pushx, wrap");
        consolePanel.add(portLabel);
        consolePanel.add(portValue, "growx, pushx, wrap");
        consolePanel.add(addressLabel);
        consolePanel.add(addressValue, "growx, pushx, wrap");
        consolePanel.add(authTypeLabel);
        consolePanel.add(authTypeValue, "growx, pushx, wrap");
        consolePanel.add(authTimeoutLabel);
        consolePanel.add(authTimeoutValue, "growx, pushx, wrap");
        consolePanel.add(allowMultiConnLabel);
        consolePanel.add(allowMultiConnValue, "growx, pushx, wrap");

        panel = new JPanel(new MigLayout());
        panel.add(displayPanel, "growx,pushx,wrap");
        panel.add(consolePanel, "growx,pushx,wrap");
    }

    public Component getComp() {
        return panel;
    }

    public void update(MachineOut mOut, MachineIn mIn) {
        this.mIn = mIn;
        this.mOut = mOut;

        enableValue.setSelected(mOut.getSetting(MachineAttribute.VrdeEnabled).getBoolean());
        portValue.setText(mOut.getSetting(MachineAttribute.VrdePort).getString());
        addressValue.setText(mOut.getSetting(MachineAttribute.VrdeAddress).getString());
        authTypeValue.setText(mOut.getSetting(MachineAttribute.VrdeAuthType).getString());
        authTimeoutValue.setText(mOut.getSetting(MachineAttribute.VrdeAuthTimeout).getString());
        allowMultiConnValue.setSelected(mOut.getSetting(MachineAttribute.VrdeMultiConnection).getBoolean());

        if ("VNC".equals(mOut.getSetting(MachineAttribute.VrdeModule).getString())) {
            panel.add(vncPassLabel);
            panel.add(vncPassField, "growx, pushx, wrap");
            if (Gui.getServer(mOut.getServerId()).getMachineReader(mOut.getUuid()).getConsole().hasSetting("VNCPassword")) {
                vncPassField.setText(Gui.getServer(mOut.getServerId()).getMachineReader(mOut.getUuid()).getConsole().getSetting("VNCPassword")
                        .getString());
            }
        }

        vramField.setText(mOut.getSetting(MachineAttribute.VRAM).getString());
        monitorCountField.setText(mOut.getSetting(MachineAttribute.MonitorCount).getString());
        accel2dBox.setSelected(mOut.getSetting(MachineAttribute.Accelerate2dVideo).getBoolean());
        accel3dBox.setSelected(mOut.getSetting(MachineAttribute.Accelerate3d).getBoolean());
    }

    public void save() {
        if (!mOut.getSetting(MachineAttribute.VrdeEnabled).getBoolean().equals(enableValue.isSelected())) {
            mIn.setSetting(new BooleanSettingIO(MachineAttribute.VrdeEnabled, enableValue.isSelected()));
        }
        if (!mOut.getSetting(MachineAttribute.VrdePort).getString().contentEquals(portValue.getText())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.VrdePort, portValue.getText()));
        }
        if (!mOut.getSetting(MachineAttribute.VrdeAddress).getString().contentEquals(addressValue.getText())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.VrdeAddress, addressValue.getText()));
        }
        if (!mOut.getSetting(MachineAttribute.VrdeAuthType).getString().contentEquals(authTypeValue.getText())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.VrdeAuthType, authTypeValue.getText()));
        }
        if (!mOut.getSetting(MachineAttribute.VrdeAuthTimeout).getString().contentEquals(authTimeoutValue.getText())) {
            mIn.setSetting(new StringSettingIO(MachineAttribute.VrdeAuthTimeout, authTimeoutValue.getText()));
        }
        if (!mOut.getSetting(MachineAttribute.VrdeMultiConnection).getBoolean().equals(allowMultiConnValue.isSelected())) {
            mIn.setSetting(new BooleanSettingIO(MachineAttribute.VrdeMultiConnection, allowMultiConnValue.isSelected()));
        }
        if ("VNC".equals(mOut.getSetting(MachineAttribute.VrdeModule).getString()) && !vncPassField.getText().isEmpty()) {
            ConsoleIn conIn = new ConsoleIn();
            conIn.setSetting(new StringSettingIO("VNCPassword", vncPassField.getText()));
            mIn.addDevice(conIn);
        }

        if (!mOut.getSetting(MachineAttribute.VRAM).getString().contentEquals(vramField.getText())) {
            mIn.setSetting(new PositiveNumberSettingIO(MachineAttribute.VRAM, Long.parseLong(vramField.getText())));
        }
        if (!mOut.getSetting(MachineAttribute.MonitorCount).getString().contentEquals(monitorCountField.getText())) {
            mIn.setSetting(new PositiveNumberSettingIO(MachineAttribute.MonitorCount, Long.parseLong(monitorCountField.getText())));
        }
        if (!mOut.getSetting(MachineAttribute.Accelerate2dVideo).getBoolean().equals(accel2dBox.isSelected())) {
            mIn.setSetting(new BooleanSettingIO(MachineAttribute.Accelerate2dVideo, accel2dBox.isSelected()));
        }
        if (!mOut.getSetting(MachineAttribute.Accelerate3d).getBoolean().equals(accel3dBox.isSelected())) {
            mIn.setSetting(new BooleanSettingIO(MachineAttribute.Accelerate3d, accel3dBox.isSelected()));
        }
    }

}
