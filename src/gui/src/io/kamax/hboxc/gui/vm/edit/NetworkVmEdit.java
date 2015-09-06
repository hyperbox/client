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

package io.kamax.hboxc.gui.vm.edit;

import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.NetworkInterfaceIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.network.NetworkAttachModeOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceTypeOut;
import io.kamax.hboxc.gui.net.NetworkInterfaceViewer;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachModeReceiver;
import io.kamax.hboxc.gui.worker.receiver._NetworkInterfaceTypeReceiver;
import io.kamax.hboxc.gui.workers.NetworkAttachModeListWorker;
import io.kamax.hboxc.gui.workers.NetworkInterfaceTypeListWorker;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

public final class NetworkVmEdit {

    private _WorkerTracker tracker;

    private String srvId;
    private MachineIn mIn;
    private JTabbedPane nicTabs;
    private List<NetworkInterfaceViewer> viewers;
    private JPanel panel;

    public NetworkVmEdit(_WorkerTracker tracker) {
        this.tracker = tracker;

        viewers = new ArrayList<NetworkInterfaceViewer>();
        nicTabs = new JTabbedPane();
        panel = new JPanel(new MigLayout());
        panel.add(nicTabs, "grow,push");
    }

    public Component getComp() {
        return panel;
    }

    public void update(MachineOut mOut, MachineIn mIn) {
        this.srvId = mOut.getServerId();
        this.mIn = mIn;

        nicTabs.removeAll();
        viewers.clear();

        for (NetworkInterfaceOut nicOut : mOut.listNetworkInterface()) {
            NetworkInterfaceViewer viewer = NetworkInterfaceViewer.show(tracker, srvId, nicOut, new NetworkInterfaceIn(mOut.getUuid(), nicOut.getNicId()));
            viewers.add(viewer);
            nicTabs.addTab("NIC " + (nicOut.getNicId() + 1), viewer.getPanel());
        }

        NetworkAttachModeListWorker.execute(tracker, new NetworkModeReceiver(), srvId);
        NetworkInterfaceTypeListWorker.execute(tracker, new NetworkInterfaceTypeReceiver(), srvId);
    }

    public void save() {
        if (mIn != null) {
            for (NetworkInterfaceViewer viewer : viewers) {
                NetworkInterfaceIn nicIn = viewer.save();
                if (nicIn != null) {
                    mIn.addNetworkInterface(nicIn);
                }
            }
        }
    }

    private class NetworkModeReceiver implements _NetworkAttachModeReceiver {

        @Override
        public void loadingStarted() {
            for (NetworkInterfaceViewer viewer : viewers) {
                viewer.getAttachModeList().setEnabled(false);
                viewer.getAttachModeList().removeAllItems();
            }
        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable message) {
            for (NetworkInterfaceViewer viewer : viewers) {
                if (isSuccessful) {
                    viewer.getAttachModeList().setSelectedItem(viewer.nicOut.getAttachMode());
                } else {
                    viewer.getAttachModeList().removeAllItems();
                    viewer.getAttachModeList().addItem("Error loading attach modes: " + message.getMessage());
                }
                viewer.getAttachModeList().setEnabled(true);
            }
        }

        @Override
        public void add(List<NetworkAttachModeOut> attachModes) {
            for (NetworkInterfaceViewer viewer : viewers) {
                for (NetworkAttachModeOut attachMode : attachModes) {
                    viewer.getAttachModeList().addItem(attachMode.getId());
                }
            }

        }

    }

    private class NetworkInterfaceTypeReceiver implements _NetworkInterfaceTypeReceiver {

        @Override
        public void loadingStarted() {
            for (NetworkInterfaceViewer viewer : viewers) {
                viewer.getAadapterTypeList().setEnabled(false);
                viewer.getAadapterTypeList().removeAllItems();
            }
        }

        @Override
        public void loadingFinished(boolean isSuccessful, Throwable message) {
            for (NetworkInterfaceViewer viewer : viewers) {
                if (isSuccessful) {
                    viewer.getAadapterTypeList().setSelectedItem(viewer.nicOut.getAdapterType());
                } else {
                    viewer.getAadapterTypeList().removeAllItems();
                    viewer.getAadapterTypeList().addItem("Error loading network interface types: " + message.getMessage());
                }
                viewer.getAadapterTypeList().setEnabled(true);
            }
        }

        @Override
        public void add(List<NetworkInterfaceTypeOut> objOutList) {
            for (NetworkInterfaceViewer viewer : viewers) {
                for (NetworkInterfaceTypeOut adapterType : objOutList) {
                    viewer.getAadapterTypeList().addItem(adapterType.getId());
                }
            }
        }

    }

}
