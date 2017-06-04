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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.out.hypervisor.GuestNetworkInterfaceOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._GuestNetworkInterfaceReceiver;

import java.util.concurrent.ExecutionException;

public class GuestNetworkInterfaceWorker extends AxSwingWorker<_GuestNetworkInterfaceReceiver, GuestNetworkInterfaceOut, Void> {

    private String srvId;
    private String vmId;
    private NetworkInterfaceOut nicOut;

    public GuestNetworkInterfaceWorker(_GuestNetworkInterfaceReceiver recv, String srvId, String vmId, NetworkInterfaceOut nicOut) {
        super(recv);
        this.srvId = srvId;
        this.vmId = vmId;
        this.nicOut = nicOut;
    }

    @Override
    protected GuestNetworkInterfaceOut innerDoInBackground() throws Exception {
        return Gui.getServer(srvId).getGuest(vmId).findNetworkInterface(nicOut.getMacAddress());
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        GuestNetworkInterfaceOut gNicOut = get();
        getReceiver().put(gNicOut);
    }

    public static void execute(_GuestNetworkInterfaceReceiver recv, String srvId, String vmId, NetworkInterfaceOut nicOut) {
        (new GuestNetworkInterfaceWorker(recv, srvId, vmId, nicOut)).execute();
    }

}
