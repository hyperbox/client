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

package io.kamax.hboxc.gui.tasks;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.utils.CancelableUtils;
import io.kamax.hboxc.gui.worker.receiver._ServerReceiver;
import io.kamax.hboxc.gui.worker.receiver._TaskReceiver;
import io.kamax.hboxc.gui.workers.ServerGetWorker;
import io.kamax.hboxc.gui.workers.TaskGetWorker;
import io.kamax.tools.helper.swing.JTextFieldUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class TaskView implements _Cancelable {

    private TaskOut tskOut;
    private String srvName;

    private JDialog dialog;
    private JLabel idLabel;
    private JTextField idField;
    private JLabel srvLabel;
    private JTextField srvField;
    private JLabel actionLabel;
    private JTextField actionField;
    private JLabel reqIdLabel;
    private JTextField reqIdField;
    private JLabel stateLabel;
    private JTextField stateField;
    private JLabel userLabel;
    private JTextField userField;
    private JLabel createLabel;
    private JTextField createField;
    private JLabel queueLabel;
    private JTextField queueField;
    private JLabel startLabel;
    private JTextField startField;
    private JLabel endLabel;
    private JTextField endField;
    private JLabel errorLabel;
    private JTextField errorField;

    private TaskView() {
        idLabel = new JLabel("ID");
        idField = JTextFieldUtils.createNonEditable();

        srvLabel = new JLabel("Server");
        srvField = JTextFieldUtils.createNonEditable();

        actionLabel = new JLabel("Action");
        actionField = JTextFieldUtils.createNonEditable();

        reqIdLabel = new JLabel("Request ID");
        reqIdField = JTextFieldUtils.createNonEditable();

        stateLabel = new JLabel("State");
        stateField = JTextFieldUtils.createNonEditable();

        userLabel = new JLabel("User");
        userField = JTextFieldUtils.createNonEditable();

        createLabel = new JLabel("Created at");
        createField = JTextFieldUtils.createNonEditable();

        queueLabel = new JLabel("Queued at");
        queueField = JTextFieldUtils.createNonEditable();

        startLabel = new JLabel("Started at");
        startField = JTextFieldUtils.createNonEditable();

        endLabel = new JLabel("Ended at");
        endField = JTextFieldUtils.createNonEditable();

        errorLabel = new JLabel("Error");
        errorField = JTextFieldUtils.createNonEditable();

        dialog = JDialogBuilder.get(IconBuilder.getEntityType(EntityType.Task).getImage());
        dialog.getContentPane().setLayout(new MigLayout());

        dialog.add(idLabel);
        dialog.add(idField, "growx, pushx, wrap");
        dialog.add(srvLabel);
        dialog.add(srvField, "growx, pushx, wrap");
        dialog.add(actionLabel);
        dialog.add(actionField, "growx, pushx, wrap");
        dialog.add(reqIdLabel);
        dialog.add(reqIdField, "growx, pushx, wrap");
        dialog.add(stateLabel);
        dialog.add(stateField, "growx, pushx, wrap");
        dialog.add(userLabel);
        dialog.add(userField, "growx, pushx, wrap");
        dialog.add(createLabel);
        dialog.add(createField, "growx, pushx, wrap");
        dialog.add(queueLabel);
        dialog.add(queueField, "growx, pushx, wrap");
        dialog.add(startLabel);
        dialog.add(startField, "growx, pushx, wrap");
        dialog.add(endLabel);
        dialog.add(endField, "growx, pushx, wrap");
        dialog.add(errorLabel, "hidemode 3");
        dialog.add(errorField, "growx,pushx, wrap, hidemode 3");

        CancelableUtils.set(this, dialog.getRootPane());

        dialog.pack();
        dialog.setSize(453, dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getParent());
    }

    private void display(TaskOut tskOut) {
        this.tskOut = tskOut;
        dialog.setTitle("Task Details");
        TaskGetWorker.execute(new TaskReceiver(), tskOut);
        dialog.setVisible(true);
    }

    public static void show(TaskOut tOut) {
        new TaskView().display(tOut);
    }

    @Override
    public void cancel() {
        dialog.setVisible(false);
    }

    private class ServerReceiver implements _ServerReceiver {

        @Override
        public void loadingStarted() {
            // TODO Auto-generated method stub

        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable t) {
            // TODO Auto-generated method stub

        }

        @Override
        public void put(ServerOut srvOut) {
            srvField.setText(srvOut.getName());
        }

    }

    private class TaskReceiver implements _TaskReceiver {

        @Override
        public void loadingStarted() {
            dialog.setTitle("Task Details - Loading");
        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable message) {
            if (isSuccessful) {
                ServerGetWorker.execute(new ServerReceiver(), tskOut.getServerId());

                dialog.setTitle("Task Details - #" + tskOut.getId());
                actionField.setText(tskOut.getActionId());
                if (tskOut.getCreateTime() != null) {
                    createField.setText(tskOut.getCreateTime().toString());
                } else {
                    createField.setText("N/A");
                }
                if (tskOut.getQueueTime() != null) {
                    queueField.setText(tskOut.getQueueTime().toString());
                } else {
                    queueField.setText("N/A");
                }
                if (tskOut.getStartTime() != null) {
                    startField.setText(tskOut.getStartTime().toString());
                } else {
                    startField.setText("N/A");
                }
                if (tskOut.getStopTime() != null) {
                    endField.setText(tskOut.getStopTime().toString());
                } else {
                    endField.setText("N/A");
                }
                if (tskOut.getError() != null) {
                    errorField.setText(tskOut.getError().getError());
                } else {
                    errorField.setText("N/A");
                }
                idField.setText(tskOut.getId());

                reqIdField.setText(tskOut.getRequestId());
                srvField.setText(srvName);

                stateField.setText(tskOut.getState().getId());
                userField.setText(tskOut.getUser().getDomainLogonName());
            } else {
                dialog.setTitle("Task Details - Loading failed");
                errorField.setText(message.getMessage());
            }
        }

        @Override
        public void put(TaskOut tskOut) {
            TaskView.this.tskOut = tskOut;
        }

    }

}
