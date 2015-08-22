/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.HyperboxClient;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class HypervisorConfigureDialog implements _Saveable, _Cancelable {

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
        try {
            // TODO Use Swing worker
            configView = Gui.getHypervisorModel(Gui.getServer(srvId).getHypervisor().getInfo().getId()).getConfigureView();
            configView.update(Gui.getServer(srvId).getHypervisor().getInfo());
            dialog.add(configView.getComponent(), "grow,push,wrap");
            dialog.add(buttonPanel, "growx,pushx,wrap");
            dialog.pack();
            dialog.setSize(650, dialog.getHeight());
            dialog.setLocationRelativeTo(dialog.getParent());
            dialog.setVisible(true);
        } catch (HyperboxException t) {
            HyperboxClient.getView().postError(t.getMessage());
        }
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

}
