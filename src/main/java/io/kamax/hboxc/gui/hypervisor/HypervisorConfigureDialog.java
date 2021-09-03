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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.worker.receiver._HypervisorReceiver;
import io.kamax.hboxc.gui.workers.HypervisorGetWorker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class HypervisorConfigureDialog implements _Saveable, _Cancelable, _HypervisorReceiver {

    private _GlobalConfigureView configView;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel buttonPanel;
    private JDialog dialog;

    private String srvId;
    private HypervisorIn hypIn;

    private HypervisorConfigureDialog(String srvId) {
        this.srvId = srvId;

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));
        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Hypervisor Configuration Editor", IconBuilder.getEntityType(EntityType.Server).getImage(), saveButton);
    }

    private HypervisorIn getInternalInput() {
        HypervisorGetWorker.execute(this, srvId);
        dialog.setVisible(true);

        return hypIn;
    }

    @Override
    public void cancel() {
        dialog.setVisible(false);
    }

    @Override
    public void save() {
        hypIn = configView.getUserInput();
        dialog.setVisible(false);
    }

    public static HypervisorIn getInput(String srvId) {
        return (new HypervisorConfigureDialog(srvId)).getInternalInput();
    }

    @Override
    public void loadingStarted() {
        saveButton.setAction(new LoadingAction());
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        saveButton.setAction(new SaveAction(this));
        saveButton.setEnabled(isSuccessful);
        if (!isSuccessful) {
            Gui.showError("Unable to fetch hypervisor info: " + t.getMessage());
        }
    }

    @Override
    public void put(HypervisorOut hypOut) {
        configView = Gui.getHypervisorModel(hypOut.getId()).getConfigureView();
        configView.update(hypOut);
        dialog.add(configView.getComponent(), "grow,push,wrap");
        dialog.add(buttonPanel, "growx,pushx,wrap");
        dialog.pack();
        dialog.setSize(650, dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getParent());
    }

}
