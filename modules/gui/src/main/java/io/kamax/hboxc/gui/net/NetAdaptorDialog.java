/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Maxime Dor
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

package io.kamax.hboxc.gui.net;

import io.kamax.hbox.comm.in.NetAdaptorIn;
import io.kamax.hbox.comm.out.network.NetAdaptorOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.hypervisor._NetAdaptorConfigureView;
import io.kamax.tools.AxStrings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class NetAdaptorDialog implements _Saveable, _Cancelable {

    private NetAdaptorIn adaptIn;
    private _NetAdaptorConfigureView configView;

    private JPanel buttonsPanel;
    private JButton saveButton;
    private JButton cancelButton;

    private JDialog dialog;

    private NetAdaptorDialog(final String srvId, final String hypId, final String modeId, final String adaptId) {
        configView = Gui.getHypervisorModel(hypId).getNetAdaptorConfig(srvId, modeId, adaptId);
        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));

        buttonsPanel = new JPanel(new MigLayout("ins 0"));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Add Network Adaptor", saveButton);
        dialog.getContentPane().add(configView.getComponent(), "grow, push, wrap");
        dialog.getContentPane().add(buttonsPanel, "center");

        if (!AxStrings.isEmpty(adaptId)) {
            new SwingWorker<NetAdaptorOut, Void>() {

                {
                    dialog.setTitle("Modifying Network Adator - Loading...");
                }

                @Override
                protected NetAdaptorOut doInBackground() throws Exception {
                    return Gui.getServer(srvId).getHypervisor().getNetAdaptor(modeId, adaptId);
                }

                @Override
                protected void done() {
                    try {
                        NetAdaptorOut naOut = get();
                        configView.update(naOut);
                        dialog.setTitle("Modifying Network Adaptor - " + naOut.getLabel());
                    } catch (InterruptedException e) {
                        Gui.showError(e);
                    } catch (ExecutionException e) {
                        Gui.showError(e.getCause());
                    }
                }
            }.execute();
        }
    }

    private NetAdaptorIn getInput() {
        show();
        return adaptIn;
    }

    public static NetAdaptorIn getInput(String srvId, String hypId, String modeId, String adaptId) {
        try {
            return new NetAdaptorDialog(srvId, hypId, modeId, adaptId).getInput();
        } catch (HyperboxException e) {
            Gui.showError(e.getMessage());
            return null;
        }
    }

    private void show() {
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    private void hide() {
        dialog.setVisible(false);
    }

    @Override
    public void cancel() {
        adaptIn = null;
        hide();
    }

    @Override
    public void save() {
        adaptIn = configView.getInput();
        hide();
    }

}
