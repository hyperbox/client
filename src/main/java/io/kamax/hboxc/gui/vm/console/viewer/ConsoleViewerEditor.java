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

package io.kamax.hboxc.gui.vm.console.viewer;

import io.kamax.hbox.comm.Request;
import io.kamax.hboxc.comm.input.ConsoleViewerInput;
import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.utils.JDialogUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ConsoleViewerEditor implements _Saveable, _Cancelable {

    private ClientTasks saveTask;
    private ConsoleViewerInput cvIn;

    private JLabel hypervisorLabel;
    private JLabel moduleLabel;
    private JLabel pathLabel;
    private JLabel argsLabel;

    private JTextField hypervisorData;
    private JTextField moduleData;
    private JTextField pathData;
    private JTextField argsData;

    private JPanel buttonPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private JDialog dialog;

    private ConsoleViewerEditor() {

        hypervisorLabel = new JLabel("Hypervisor Type Pattern");
        moduleLabel = new JLabel("Module Pattern");
        pathLabel = new JLabel("Viewer Path");
        argsLabel = new JLabel("Arguments");

        hypervisorData = new JTextField();
        moduleData = new JTextField();
        pathData = new JTextField();
        argsData = new JTextField();

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));
        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Console Viewer - Edition", saveButton);
        dialog.add(hypervisorLabel);
        dialog.add(hypervisorData, "growx, pushx, wrap");
        dialog.add(moduleLabel);
        dialog.add(moduleData, "growx, pushx, wrap");
        dialog.add(pathLabel);
        dialog.add(pathData, "growx, pushx, wrap");
        dialog.add(argsLabel);
        dialog.add(argsData, "growx, pushx, wrap");
        dialog.add(buttonPanel, "center, bottom, span 2");
    }

    public static void create() {

        new ConsoleViewerEditor().add();
    }

    public static void edit(ConsoleViewerOutput conViewOut) {

        new ConsoleViewerEditor().modify(conViewOut);
    }

    private void add() {

        saveTask = ClientTasks.ConsoleViewerAdd;
        cvIn = new ConsoleViewerInput();

        show();
    }

    private void modify(ConsoleViewerOutput conViewOut) {

        saveTask = ClientTasks.ConsoleViewerModify;
        cvIn = new ConsoleViewerInput(conViewOut.getId());

        hypervisorData.setText(conViewOut.getHypervisorId());
        hypervisorData.setEditable(false);
        moduleData.setText(conViewOut.getModuleId());
        moduleData.setEditable(false);
        pathData.setText(conViewOut.getViewerPath());
        argsData.setText(conViewOut.getArgs().toString());

        show();
    }

    private void show() {
        JDialogUtils.setSizeAtLeast(dialog, 410, 170);
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    private void hide() {

        dialog.setVisible(false);
    }

    @Override
    public void cancel() {

        hide();
    }

    @Override
    public void save() {

        cvIn.setHypervisorId(hypervisorData.getText());
        cvIn.setModuleId(moduleData.getText());
        cvIn.setViewer(pathData.getText());
        cvIn.setArgs(argsData.getText());

        Gui.post(new Request(saveTask, cvIn));

        hide();
    }

}
