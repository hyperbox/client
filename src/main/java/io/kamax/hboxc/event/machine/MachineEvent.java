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

package io.kamax.hboxc.event.machine;

import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.event.Event;

public abstract class MachineEvent extends Event {

    private String srvId;

    public MachineEvent(Enum<?> id, String srvId, MachineOut mOut) {
        super(id);
        this.srvId = srvId;
        set(MachineOut.class, mOut);
    }

    public MachineOut getMachine() {
        return get(MachineOut.class);
    }

    public String getUuid() {
        return getMachine().getUuid();
    }

    public String getServerId() {
        return srvId;
    }

    @Override
    public String toString() {
        return "Event ID " + getEventId() + " for Machine " + getUuid() + " on Server " + getServerId() + " occured @ " + getTime();
    }

}
