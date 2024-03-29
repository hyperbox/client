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

package io.kamax.hboxc.gui.net;

import io.kamax.hbox.comm.out.hypervisor.GuestNetworkInterfaceOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hboxc.gui.worker.receiver._GuestNetworkInterfaceReceiver;
import io.kamax.hboxc.gui.workers.GuestNetworkInterfaceWorker;
import io.kamax.tools.AxStrings;
import io.kamax.tools.helper.swing.JTextFieldUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class NetworkInterfaceSummary extends JPanel implements _GuestNetworkInterfaceReceiver {

    private static final long serialVersionUID = -9083225380488179517L;

    private String srvId;
    private String vmId;
    private NetworkInterfaceOut nicOut;
    private JTextField nicValue;
    private JTextField ipv4Value;

    public NetworkInterfaceSummary(String srvId, String vmId, NetworkInterfaceOut nicOut) {
        super(new MigLayout("ins 0"));
        this.srvId = srvId;
        this.vmId = vmId;
        this.nicOut = nicOut;

        nicValue = JTextFieldUtils.createAsLabel(nicOut.getAdapterType() + " using " + nicOut.getAttachMode()
                + (AxStrings.isEmpty(nicOut.getAttachName()) ? "" : " on " + nicOut.getAttachName()));
        ipv4Value = JTextFieldUtils.createAsLabel(nicOut.getMacAddress());
        add(new JLabel("Adapter " + (nicOut.getNicId() + 1)));
        add(new JLabel(""), "growx,pushx,wrap");
        add(new JLabel(""));
        add(nicValue, "right, growx, pushx, wrap");
        add(new JLabel(""));
        add(ipv4Value, "right, growx, pushx, wrap");
        refresh();
    }

    private void refresh() {
        GuestNetworkInterfaceWorker.execute(this, srvId, vmId, nicOut);
    }

    @Override
    public void put(GuestNetworkInterfaceOut gNicOut) {
        if (gNicOut != null) {
            ipv4Value.setText(nicOut.getMacAddress() + " | " + gNicOut.getIp4Address() + "/" + gNicOut.getIp4Subnet());
        } else {
            ipv4Value.setText(nicOut.getMacAddress() + " | IP information is not available");
        }
    }

    @Override
    public void loadingStarted() {
        ipv4Value.setText(nicOut.getMacAddress() + " | Loading...");
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        if (!isSuccessful) {
            ipv4Value.setText(nicOut.getMacAddress() + " | Error: " + t.getMessage());
        }
    }

}
