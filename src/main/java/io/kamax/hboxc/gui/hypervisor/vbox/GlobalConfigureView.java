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

package io.kamax.hboxc.gui.hypervisor.vbox;

import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hboxc.gui.hypervisor._GlobalConfigureView;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class GlobalConfigureView implements _GlobalConfigureView {

    private JLabel machineFolderLabel;
    private JTextField machineFolderValue;
    private JLabel consoleModuleLabel;
    private JTextField consoleModuleValue;
    private JPanel panel;

    public GlobalConfigureView() {
        machineFolderLabel = new JLabel("Default Machine Folder");
        consoleModuleLabel = new JLabel("Default Extention Pack");

        machineFolderValue = new JTextField();
        consoleModuleValue = new JTextField();

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(machineFolderLabel);
        panel.add(machineFolderValue, "growx, pushx, wrap");
        panel.add(consoleModuleLabel);
        panel.add(consoleModuleValue, "growx, pushx, wrap");
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void update(HypervisorOut hypOut) {
        machineFolderValue.setText(hypOut.getSetting("vbox.global.machineFolder").getString());
        consoleModuleValue.setText(hypOut.getSetting("vbox.global.consoleModule").getString());
    }

    @Override
    public HypervisorIn getUserInput() {
        HypervisorIn hypIn = new HypervisorIn();
        hypIn.setSetting(new StringSettingIO("vbox.global.machineFolder", machineFolderValue.getText()));
        hypIn.setSetting(new StringSettingIO("vbox.global.consoleModule", consoleModuleValue.getText()));
        return hypIn;
    }

}
