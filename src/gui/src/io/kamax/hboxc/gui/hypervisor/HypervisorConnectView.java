/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.utils.HypervisorLoaderOutComparator;
import io.kamax.tool.KxCollections;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class HypervisorConnectView implements _Saveable, _Cancelable {

    private ServerOut srvOut;

    private JDialog dialog;

    private JLabel loaderLabel;
    private JComboBox loaderData;

    private JLabel optionsLabel;
    private JTextField optionsData;

    private JButton connectButton;
    private JButton cancelButton;

    private HypervisorIn hypIn;

    public HypervisorConnectView(ServerOut srvOut) {
        this.srvOut = srvOut;

        loaderLabel = new JLabel("Connector ID");
        loaderData = new JComboBox();

        optionsLabel = new JLabel("Connector Options");
        optionsData = new JTextField();

        connectButton = new JButton(new SaveAction(this, "Connect"));
        cancelButton = new JButton(new CancelAction(this));

        JPanel centerPanel = new JPanel(new MigLayout("ins 0"));
        centerPanel.add(loaderLabel);
        centerPanel.add(loaderData, "growx,pushx,wrap");
        centerPanel.add(optionsLabel);
        centerPanel.add(optionsData, "growx,pushx,wrap");

        JPanel buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Connect to Hypervisor", connectButton);
        dialog.add(centerPanel, "growx,pushx,wrap");
        dialog.add(buttonPanel, "growx,pushx,wrap");
    }

    public void show() {
        for (HypervisorLoaderOut hypOut : KxCollections.sort(Gui.getServer(srvOut).listHypervisors(), new HypervisorLoaderOutComparator())) {
            loaderData.addItem(hypOut);
        }
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }

    @Override
    public void cancel() {
        hide();
    }

    @Override
    public void save() {
        hypIn = new HypervisorIn(((HypervisorLoaderOut) loaderData.getSelectedItem()).getHypervisorId());
        hypIn.setConnectionOptions(optionsData.getText());
        hypIn.setAutoConnect(true);
        hide();
    }

    public HypervisorIn getUserInput() {
        show();
        return hypIn;
    }

    public static HypervisorIn getInput(ServerOut srvOut) {
        return new HypervisorConnectView(srvOut).getUserInput();
    }

}
