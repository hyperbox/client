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
import io.kamax.hbox.comm.in.DeviceIn;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.io.SettingIO;
import io.kamax.hbox.comm.io.factory.SettingIoFactory;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.comm.utils.Transaction;
import io.kamax.hboxc.server._Console;
import io.kamax.hboxc.server._Machine;

public class Console extends Device implements _Console {

    public Console(_Machine machine) {
        super(machine, EntityType.Console.getId());
        refresh();
    }

    @Override
    public String getType() {
        return EntityType.Console.getId();
    }

    @Override
    public void refresh() {
        Request req = new Request(Command.VBOX, HypervisorTasks.DevicePropertyList);
        req.set(MachineIn.class, new MachineIn(getMachine().getId()));
        req.set(DeviceIn.class, new DeviceIn(getId()));
        Transaction t = getMachine().getServer().sendRequest(req);
        setSetting(SettingIoFactory.getListIo(t.extractItems(SettingIO.class)));
    }

}
