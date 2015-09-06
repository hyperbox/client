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

package io.kamax.hboxc.gui.server;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.event.server.ServerEvent;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.hypervisor.HypervisorViewer;
import io.kamax.hboxc.gui.worker.receiver._ServerReceiver;
import io.kamax.hboxc.gui.workers.ServerGetWorker;
import io.kamax.helper.swing.JTextFieldUtils;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

public class ServerViewer implements _Refreshable, _ServerReceiver {

    private String srvId;

    private HypervisorViewer hypViewer;

    private JLabel idLabel;
    private JTextField idValue;
    private JLabel nameLabel;
    private JTextField nameValue;
    private JLabel typeLabel;
    private JTextField typeValue;
    private JLabel versionLabel;
    private JTextField versionValue;
    private JLabel netProtocolLabel;
    private JTextField netProtocolValue;

    private JPanel srvPanel;

    private JPanel panel;

    public ServerViewer() {
        idLabel = new JLabel("ID");
        nameLabel = new JLabel("Name");
        typeLabel = new JLabel("Type");
        versionLabel = new JLabel("Version");
        netProtocolLabel = new JLabel("Protocol");

        idValue = JTextFieldUtils.createNonEditable();
        nameValue = JTextFieldUtils.createNonEditable();
        typeValue = JTextFieldUtils.createNonEditable();
        versionValue = JTextFieldUtils.createNonEditable();
        netProtocolValue = JTextFieldUtils.createNonEditable();

        hypViewer = new HypervisorViewer();

        srvPanel = new JPanel(new MigLayout());
        srvPanel.setBorder(BorderFactory.createTitledBorder("Server"));
        srvPanel.add(idLabel);
        srvPanel.add(idValue, "growx,pushx,wrap");
        srvPanel.add(nameLabel);
        srvPanel.add(nameValue, "growx,pushx,wrap");
        srvPanel.add(typeLabel);
        srvPanel.add(typeValue, "growx,pushx,wrap");
        srvPanel.add(versionLabel);
        srvPanel.add(versionValue, "growx,pushx,wrap");
        srvPanel.add(netProtocolLabel);
        srvPanel.add(netProtocolValue, "growx,pushx,wrap");

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(srvPanel, "growx, pushx,wrap");
        panel.add(hypViewer.getComponent(), "growx, pushx");

        ViewEventManager.register(this);
    }

    public JComponent getComponent() {
        return panel;
    }

    public void show(ServerOut srvOut) {
        update(srvOut);
    }

    private void clear() {
        // TODO implement
    }

    @Override
    public void refresh() {
        clear();
        ServerGetWorker.execute(this, srvId);
    }

    private void update(ServerOut srvOut) {
        this.srvId = srvOut.getId();
        hypViewer.setSrvId(srvId);
        idValue.setText(srvOut.getId());
        nameValue.setText(srvOut.getName());
        typeValue.setText(srvOut.getType());
        versionValue.setText(srvOut.getVersion());
        netProtocolValue.setText(srvOut.getNetworkProtocolVersion() != null ? srvOut.getNetworkProtocolVersion() : "Unknown");

        if (srvOut.isHypervisorConnected()) {
            hypViewer.show(Gui.getServer(srvOut).getHypervisor().getInfo());
        } else {
            hypViewer.setDisconnected();
        }
    }

    @Handler
    public void putServerEvent(ServerEvent ev) {

        if ((srvId != null) && srvId.equals(ev.getServer().getId())) {
            refresh();
        }
    }

    @Override
    public void loadingStarted() {
        // nothing to do yet
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable message) {
        // TODO implement in case of error
    }

    @Override
    public void put(ServerOut srvOut) {

        update(srvOut);
    }

}
