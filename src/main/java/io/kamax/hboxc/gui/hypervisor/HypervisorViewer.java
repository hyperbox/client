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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.out.event.hypervisor.HypervisorConnectedEventOut;
import io.kamax.hbox.comm.out.event.hypervisor.HypervisorDisconnectedEventOut;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.worker.receiver._HypervisorReceiver;
import io.kamax.hboxc.gui.workers.HypervisorGetWorker;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class HypervisorViewer implements _Refreshable, _HypervisorReceiver {

    private String srvId;

    private JLabel stateLabel;
    private JTextField stateData;

    private JLabel typeLabel;
    private JTextField typeData;

    private JLabel vendorLabel;
    private JTextField vendorData;

    private JLabel productLabel;
    private JTextField productData;

    private JLabel versionLabel;
    private JTextField versionData;

    private JLabel revisionLabel;
    private JTextField revisionData;

    private JPanel panel;

    public HypervisorViewer() {
        stateLabel = new JLabel("State");
        typeLabel = new JLabel("Type");
        vendorLabel = new JLabel("Vendor");
        productLabel = new JLabel("Product");
        versionLabel = new JLabel("Version");
        revisionLabel = new JLabel("Revision");

        stateData = new JTextField();
        stateData.setEditable(false);
        typeData = new JTextField();
        typeData.setEditable(false);
        vendorData = new JTextField();
        vendorData.setEditable(false);
        productData = new JTextField();
        productData.setEditable(false);
        versionData = new JTextField();
        versionData.setEditable(false);
        revisionData = new JTextField();
        revisionData.setEditable(false);

        panel = new JPanel(new MigLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Hypervisor"));
        panel.add(stateLabel, "hidemode 3");
        panel.add(stateData, "growx,pushx,wrap,hidemode 3");
        panel.add(typeLabel, "hidemode 3");
        panel.add(typeData, "growx,pushx,wrap,hidemode 3");
        panel.add(vendorLabel, "hidemode 3");
        panel.add(vendorData, "growx,pushx,wrap,hidemode 3");
        panel.add(productLabel, "hidemode 3");
        panel.add(productData, "growx,pushx,wrap,hidemode 3");
        panel.add(versionLabel, "hidemode 3");
        panel.add(versionData, "growx,pushx,wrap,hidemode 3");
        panel.add(revisionLabel, "hidemode 3");
        panel.add(revisionData, "growx,pushx,wrap,hidemode 3");

        ViewEventManager.register(this);
    }

    private void toogleConnected(boolean isConnected) {
        stateData.setText(isConnected ? "Connected" : "Disconnected");
        typeLabel.setVisible(isConnected);
        typeData.setVisible(isConnected);
        vendorLabel.setVisible(isConnected);
        vendorData.setVisible(isConnected);
        productLabel.setVisible(isConnected);
        productData.setVisible(isConnected);
        versionLabel.setVisible(isConnected);
        versionData.setVisible(isConnected);
        revisionLabel.setVisible(isConnected);
        revisionData.setVisible(isConnected);
    }

    public void show(String srvId) {
        setSrvId(srvId);
        refresh();
    }

    public void setDisconnected() {
        typeData.setText(null);
        vendorData.setText(null);
        productData.setText(null);
        versionData.setText(null);
        revisionData.setText(null);
        toogleConnected(false);
    }

    public JComponent getComponent() {
        return panel;
    }

    public void setSrvId(String srvId) {
        this.srvId = srvId;
    }

    @Handler
    private void putHypervisorConnectEvent(HypervisorConnectedEventOut ev) {
        if ((srvId != null) && ev.getServerId().equals(srvId)) {
            toogleConnected(true);
        }

    }

    @Handler
    private void putHypervisorConnectEvent(HypervisorDisconnectedEventOut ev) {
        if ((srvId != null) && ev.getServerId().equals(srvId)) {
            toogleConnected(false);
        }
    }

    @Override
    public void refresh() {
        HypervisorGetWorker.execute(this, srvId);
    }

    @Override
    public void loadingStarted() {
        toogleConnected(false);
        stateData.setText("Loading...");
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        toogleConnected(isSuccessful);
        if (!isSuccessful) {
            stateData.setText(t.getMessage());
        }
    }

    @Override
    public void put(HypervisorOut hypOut) {
        typeData.setText(hypOut.getType());
        vendorData.setText(hypOut.getVendor());
        productData.setText(hypOut.getProduct());
        versionData.setText(hypOut.getVersion());
        revisionData.setText(hypOut.getRevision());
    }

}
