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

package io.kamax.hboxc.gui.connector;

import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.output.BackendOutput;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._ConnectorBackendListReceiver;
import io.kamax.hboxc.gui.workers.ConnectorBackendListWorker;
import io.kamax.hboxc.gui.workers.MessageWorker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public final class ConnectorEditorDialog implements _Saveable, _Cancelable, _ConnectorBackendListReceiver {

    private JDialog dialog;

    private JPanel inputPanel;
    private JLabel hostnameLabel;
    private JTextField hostnameField;
    private JLabel labelLabel;
    private JTextField labelField;
    private JLabel userLabel;
    private JTextField userField;
    private JLabel passLabel;
    private JPasswordField passField;
    private JLabel connectorLabel;
    private JComboBox<BackendOutput> connectorValue;

    private JPanel buttonPanel;
    private JButton loginButton;
    private JButton cancelButton;
    private JProgressBar refreshProgress;

    private String conId = "-1";
    private ClientTasks task;

    private ConnectorEditorDialog() {
        hostnameLabel = new JLabel("Hostname");
        hostnameField = new JTextField(15);
        hostnameField.setText("127.0.0.1");

        labelLabel = new JLabel("Label");
        labelField = new JTextField(15);

        userLabel = new JLabel("User");
        userField = new JTextField(15);

        passLabel = new JLabel("Password");
        passField = new JPasswordField(15);

        connectorLabel = new JLabel("Connector");
        connectorValue = new JComboBox<>();

        loginButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));
        refreshProgress = new JProgressBar();
        refreshProgress.setVisible(false);
        refreshProgress.setStringPainted(true);

        inputPanel = new JPanel(new MigLayout());
        inputPanel.add(labelLabel);
        inputPanel.add(labelField, "growx,pushx,wrap");
        inputPanel.add(hostnameLabel);
        inputPanel.add(hostnameField, "growx, pushx, wrap");
        inputPanel.add(userLabel);
        inputPanel.add(userField, "growx, pushx, wrap");
        inputPanel.add(passLabel);
        inputPanel.add(passField, "growx, pushx, wrap");
        inputPanel.add(connectorLabel);
        inputPanel.add(connectorValue, "growx,pushx,wrap");

        buttonPanel = new JPanel(new MigLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshProgress, "growx,pushx");

        dialog = JDialogBuilder.get(loginButton);
        dialog.add(inputPanel, "growx, pushx, wrap");
        dialog.add(buttonPanel, "growx, pushx");
    }

    public void show() {
        ConnectorBackendListWorker.execute(this);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }

    public void create() {
        task = ClientTasks.ConnectorAdd;
        dialog.setTitle("Add Server Connection");

        show();
    }

    public void modify(ConnectorOutput conOut) {
        conId = conOut.getId();
        task = ClientTasks.ConnectorModify;
        dialog.setTitle("Edit Server Connection");

        labelField.setText(conOut.getLabel());
        hostnameField.setText(conOut.getAddress());
        userField.setText(conOut.getUsername());

        show();
    }

    @Override
    public void save() {
        ConnectorInput conIn = new ConnectorInput(conId);
        conIn.setAddress(hostnameField.getText());
        conIn.setLabel(labelField.getText());
        conIn.setBackendId(((BackendOutput) connectorValue.getSelectedItem()).getId());

        if (task.equals(ClientTasks.ConnectorAdd) || (!userField.getText().isEmpty() && passField.getPassword().length > 0)) {
            UserIn uIn = new UserIn(userField.getText(), passField.getPassword());
            MessageWorker.execute(new Request(task, uIn, conIn), new SaveAnswerReceiver());
        } else {
            MessageWorker.execute(new Request(task, conIn), new SaveAnswerReceiver());
        }
    }

    @Override
    public void cancel() {
        hide();
    }

    public static void add() {
        new ConnectorEditorDialog().create();
    }

    public static void edit(ConnectorOutput srvOut) {
        new ConnectorEditorDialog().modify(srvOut);
    }

    @Override
    public void loadingStarted() {
        loginButton.setEnabled(false);
        connectorValue.setEnabled(false);
        connectorValue.setSelectedIndex(-1);
        refreshProgress.setVisible(true);
        refreshProgress.setIndeterminate(true);
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        refreshProgress.setVisible(false);
        refreshProgress.setIndeterminate(false);
        loginButton.setEnabled(isSuccessful);
        connectorValue.setEnabled(isSuccessful);
        if (!isSuccessful) {
            connectorValue.removeAllItems();
            Gui.showError("Error fetching connector backends: " + t.getMessage());
        }
    }

    @Override
    public void add(List<BackendOutput> objOutList) {
        for (BackendOutput objOut : objOutList) {
            connectorValue.addItem(objOut);
        }
    }

    private class SaveAnswerReceiver extends AnswerWorkerReceiver {

        private Action initialAction;

        @Override
        public void start() {
            initialAction = loginButton.getAction();
            loginButton.setAction(new LoadingAction());
            loginButton.setEnabled(false);
        }

        @Override
        public void success() {
            loginButton.setAction(initialAction);
            passField.setText(null);
            hide();
        }

        @Override
        public void fail(Throwable t) {
            loginButton.setAction(initialAction);
            Gui.showError(t);
        }

    }

}
