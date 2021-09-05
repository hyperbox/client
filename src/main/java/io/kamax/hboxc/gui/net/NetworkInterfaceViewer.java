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

package io.kamax.hboxc.gui.net;

import io.kamax.hbox.comm.in.NetworkInterfaceIn;
import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hbox.comm.out.network.NetworkAttachNameOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachNameReceiver;
import io.kamax.hboxc.gui.workers.NetworkAttachNameListWorker;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import io.kamax.tools.AxStrings;
import io.kamax.tools.helper.swing.JCheckBoxUtils;
import io.kamax.tools.logging.Logger;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NetworkInterfaceViewer {

    private _WorkerTracker tracker;

    private String srvId;
    public NetworkInterfaceOut nicOut;
    private NetworkInterfaceIn nicIn;

    private JLabel enableNicLabel = new JLabel();
    public JCheckBox enableNicValue = new JCheckBox();
    private JLabel connectedLabel = new JLabel();
    public JCheckBox connectedValue = new JCheckBox();
    private JLabel attachToLabel = new JLabel();
    public JComboBox<String> attachModeValue = new JComboBox<>();
    private JLabel attachNameLabel = new JLabel();
    public JComboBox<String> attachNameValue = new JComboBox<>();
    private JLabel adapterTypeLabel = new JLabel();
    public JComboBox<String> adapterTypeValue = new JComboBox<>();
    private JLabel macAddrLabel = new JLabel();
    public JTextField macAddrValue = new JTextField(30);

    private JPanel mainPanel = new JPanel(new MigLayout());

    public static NetworkInterfaceViewer show(_WorkerTracker tracker, String srvId, NetworkInterfaceOut nicOut) {
        NetworkInterfaceViewer viewer = new NetworkInterfaceViewer(tracker, srvId, nicOut);
        return viewer;
    }

    public static NetworkInterfaceViewer show(_WorkerTracker tracker, String srvId, NetworkInterfaceOut nicOut, NetworkInterfaceIn nicIn) {
        NetworkInterfaceViewer viewer = new NetworkInterfaceViewer(tracker, srvId, nicOut, nicIn);
        return viewer;
    }

    public NetworkInterfaceViewer(_WorkerTracker tracker, String srvId, NetworkInterfaceOut nicOut) {
        this.tracker = tracker;

        this.srvId = srvId;
        this.nicOut = nicOut;

        setLabels();

        enableNicValue.setSelected(false);
        connectedValue.setSelected(false);
        attachModeValue.addActionListener(new AttachTypeListener());
        attachModeValue.removeAllItems();
        attachNameValue.setEditable(true);
        attachNameValue.removeAllItems();
        adapterTypeValue.removeAllItems();
        macAddrValue.setText(null);

        mainPanel.add(enableNicLabel);
        mainPanel.add(enableNicValue, "growx, pushx, wrap");
        mainPanel.add(connectedLabel);
        mainPanel.add(connectedValue, "growx, pushx, wrap");
        mainPanel.add(attachToLabel);
        mainPanel.add(attachModeValue, "growx, pushx, wrap");
        mainPanel.add(attachNameLabel);
        mainPanel.add(attachNameValue, "growx, pushx, wrap");
        mainPanel.add(adapterTypeLabel);
        mainPanel.add(adapterTypeValue, "growx, pushx, wrap");
        mainPanel.add(macAddrLabel);
        mainPanel.add(macAddrValue, "growx, pushx, wrap");

        enableNicValue.setSelected(nicOut.isEnabled());
        connectedValue.setSelected(nicOut.isCableConnected());
        macAddrValue.setText(nicOut.getMacAddress());

        JCheckBoxUtils.link(enableNicValue, connectedValue, attachModeValue, attachNameValue, adapterTypeValue, macAddrValue);
    }

    public NetworkInterfaceViewer(_WorkerTracker tracker, String srvId, NetworkInterfaceOut nicOut, NetworkInterfaceIn nicIn) {
        this(tracker, srvId, nicOut);
        this.nicIn = nicIn;
    }

    private void setLabels() {
        enableNicLabel.setText("Enabled");
        connectedLabel.setText("Cable Connected");
        attachToLabel.setText("Attach Mode");
        attachNameLabel.setText("Attach To");
        adapterTypeLabel.setText("Adapter Type");
        macAddrLabel.setText("MAC Address");
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComboBox<String> getAadapterTypeList() {
        return adapterTypeValue;
    }

    public JComboBox<String> getAttachModeList() {
        return attachModeValue;
    }

    public NetworkInterfaceIn save() {
        if (nicIn != null) {
            nicIn.setEnabled(enableNicValue.isSelected());
            if (enableNicValue.isEnabled()) {
                nicIn.setCableConnected(connectedValue.isSelected());
                nicIn.setAttachMode(attachModeValue.getSelectedItem().toString());
                nicIn.setAttachName(AxStrings.get(attachNameValue.getSelectedItem()));
                nicIn.setAdapterType(adapterTypeValue.getSelectedItem().toString());
                nicIn.setMacAddress(macAddrValue.getText().replaceAll(":", ""));
            }
        }

        if (nicIn.hasNewData()) {
            return nicIn;
        } else {
            return null;
        }
    }

    private class AttachTypeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String attachTypeId = attachModeValue.getSelectedItem().toString();
            if (AxStrings.isEmpty(attachTypeId)) {
                Logger.info("Selected attach mode value is empty, skipping listing of attach type");
                return;
            }

            Logger.info(attachTypeId + " was selected as attachment type, fetching list of attachment names");
            NetworkAttachNameListWorker.execute(tracker, new NetworkAttachNameReceiver(), srvId, attachTypeId);
        }
    }

    private class NetworkAttachNameReceiver implements _NetworkAttachNameReceiver {

        @Override
        public void loadingStarted() {
            attachModeValue.setEnabled(false);
            attachNameValue.setEnabled(false);
            attachNameValue.removeAllItems();
        }

        @Override
        public void loadingFinished(final boolean isSuccessful, final Throwable t) {
            attachModeValue.setEnabled(isSuccessful);

            if (!isSuccessful) {
                attachNameValue.removeAllItems();
                attachNameValue.addItem("Error loading attach names: " + t.getMessage());
                return;
            }

            if (attachModeValue.getSelectedItem().equals(nicOut.getAttachMode())) {
                attachNameValue.setSelectedItem(nicOut.getAttachName());
            } else {
                attachNameValue.setSelectedIndex(-1);
            }

            new SwingWorker<NetModeOut, Void>() {

                @Override
                protected NetModeOut doInBackground() throws Exception {
                    return Gui.getServer(srvId).getHypervisor().getNetworkMode(attachModeValue.getSelectedItem().toString());
                }

                @Override
                protected void done() {
                    try {
                        NetModeOut mode = get();
                        attachNameValue.setEditable(mode.canLinkNetworkName());
                        attachNameValue.setEnabled(mode.canLinkAdaptor() || mode.canLinkNetworkName());
                    } catch (Throwable t1) {
                        if ((t1 instanceof ExecutionException) && (t1.getCause() != null)) {
                            t1 = t1.getCause();
                        }

                        attachNameValue.removeAllItems();
                        attachNameValue.addItem("Error loading mode info: " + t1.getMessage());
                    }
                }

            }.execute();
        }

        @Override
        public void add(List<NetworkAttachNameOut> nanOut) {
            for (NetworkAttachNameOut attachName : nanOut) {
                Logger.debug("Adding attachment name: " + attachName.getId());
                attachNameValue.addItem(attachName.getId());
            }
        }

    }

}
