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

package io.kamax.hboxc.core.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.GuestNetworkInterfaceIn;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.out.hypervisor.GuestNetworkInterfaceOut;
import io.kamax.hboxc.comm.utils.Transaction;
import io.kamax.hboxc.server._GuestReader;
import io.kamax.hboxc.server._Server;

public class GuestReader implements _GuestReader {

    private _Server srv;
    private String machineUuid;

    public GuestReader(_Server srv, String machineUuid) {
        this.srv = srv;
        this.machineUuid = machineUuid;
    }

    @Override
    public GuestNetworkInterfaceOut findNetworkInterface(String macAddress) {
        Request req = new Request(Command.VBOX, HypervisorTasks.GuestNetworkInterfaceFind);
        req.set(new MachineIn(machineUuid));
        req.set(new GuestNetworkInterfaceIn().setMacAddress(macAddress));
        Transaction t = srv.sendRequest(req);
        return t.extractItem(GuestNetworkInterfaceOut.class);
    }

}
