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

package io.kamax.hboxc.core.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.SnapshotIn;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.core.Entity;
import io.kamax.hboxc.server._Console;
import io.kamax.hboxc.server._Device;
import io.kamax.hboxc.server._Machine;
import io.kamax.hboxc.server._Server;

import java.util.HashMap;
import java.util.Map;

public class Machine extends Entity implements _Machine {

    private _Server srv;
    //private String id;
    //private MachineOut mOut;
    private Map<String, _Device> devices = new HashMap<String, _Device>();

    public Machine(_Server srv, String id) {
        super(id);
        this.srv = srv;
        refresh();
    }

    public void refresh() {
        /*
        Request req = new Request(Command.VBOX, HypervisorTasks.MachineGet);
        req.set(new MachineIn(id));
        Transaction t = srv.sendRequest(req);
        mOut = t.extractItem(MachineOut.class);
         */
    }

    @Override
    public _Console getConsole() {
        if (!devices.containsKey(EntityType.Console.getId())) {
            devices.put(EntityType.Console.getId(), new Console(this));
        }

        return (_Console) devices.get(EntityType.Console.getId());
    }

    @Override
    public _Server getServer() {
        return srv;
    }

    @Override
    public void takeSnapshot(SnapshotIn snapshotIn) {
        Request req = new Request(Command.VBOX, HypervisorTasks.SnapshotTake);
        req.set(MachineIn.class, new MachineIn(getId()));
        req.set(SnapshotIn.class, snapshotIn);
        srv.sendRequest(req);
    }

}
