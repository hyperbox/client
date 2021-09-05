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

package io.kamax.hboxc.gui.vm;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.hypervisor.OsTypeOut;
import io.kamax.hbox.constant.MachineAttribute;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.MainView;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.utils.JDialogUtils;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.workers.MessageWorker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VmCreateDialog implements _Saveable, _Cancelable {

    private static VmCreateDialog instance;
    private ServerOut srvOut;
    private JDialog mainDialog;

    private JPanel mainPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel osLabel;
    private JComboBox<String> osBox;

    private JPanel buttonsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private MachineIn mIn;

    public VmCreateDialog(ServerOut srvOut) {
        this.srvOut = srvOut;
    }

    private void init() {
        mainDialog = new JDialog(MainView.getMainFrame());
        mainDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        mainDialog.setTitle("Create Machine");
        JDialogUtils.setCloseOnEscapeKey(mainDialog, true);

        nameLabel = new JLabel("Name");
        nameField = new JTextField(40);
        osLabel = new JLabel("Settings Template");
        osBox = new JComboBox<>();

        mainPanel = new JPanel(new MigLayout());
        mainPanel.add(nameLabel);
        mainPanel.add(nameField, "growx,pushx,wrap");
        mainPanel.add(osLabel);
        mainPanel.add(osBox, "growx,pushx,wrap");

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));

        buttonsPanel = new JPanel(new MigLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        mainDialog.getContentPane().setLayout(new MigLayout());
        mainDialog.getContentPane().add(mainPanel, "grow,push,wrap");
        mainDialog.getContentPane().add(buttonsPanel, "center, growx");
        mainDialog.getRootPane().setDefaultButton(saveButton);
    }

    public static void show(final ServerOut srvOut) {
        if (instance == null) {
            instance = new VmCreateDialog(srvOut);
            instance.init();
        }

        new SwingWorker<List<OsTypeOut>, Void>() {

            {
                instance.osBox.removeAllItems();
                instance.osBox.setEnabled(false);
                instance.saveButton.setEnabled(false);
                instance.osBox.addItem("Loading...");
            }

            @Override
            protected List<OsTypeOut> doInBackground() throws Exception {
                return Gui.getReader().getServerReader(srvOut.getId()).listOsType();
            }

            @Override
            protected void done() {
                List<OsTypeOut> osTypes;
                try {
                    osTypes = get();
                    for (OsTypeOut osOut : osTypes) {
                        instance.osBox.addItem(osOut.getId());
                    }
                    instance.osBox.removeItem("Loading...");
                    instance.osBox.setEnabled(true);
                    instance.saveButton.setEnabled(true);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }.execute();

        instance.mainDialog.pack();
        instance.mainDialog.setSize(475, instance.mainDialog.getHeight());
        instance.mainDialog.setLocationRelativeTo(instance.mainDialog.getParent());
        instance.mainDialog.setVisible(true);
    }

    private void hide() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
        mIn = null;
        instance = null;
    }

    @Override
    public void save() {
        mIn = new MachineIn();
        mIn.setName(nameField.getText());
        mIn.setSetting(new StringSettingIO(MachineAttribute.OsType, osBox.getSelectedItem().toString()));

        MessageWorker.execute(new Request(Command.VBOX, HypervisorTasks.MachineCreate, new ServerIn(srvOut.getId()), mIn), new AnswerWorkerReceiver() {

            private Action initialAction;

            @Override
            public void start() {
                initialAction = saveButton.getAction();
                saveButton.setAction(LoadingAction.get());
                mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            @Override
            public void loadingFinished(boolean isSuccessful, Throwable t) {
                mainDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                if (!isSuccessful) {
                    saveButton.setAction(initialAction);
                    fail(t);
                } else {
                    hide();
                }
            }
        });
    }

    @Override
    public void cancel() {
        hide();
    }

}
