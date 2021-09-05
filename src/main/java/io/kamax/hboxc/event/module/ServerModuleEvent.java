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

package io.kamax.hboxc.event.module;

import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.event.server.ServerEvent;

public class ServerModuleEvent extends ServerEvent {

    public ServerModuleEvent(Enum<?> eventId, ServerOut srvOut, ModuleOut objOut) {
        super(eventId, srvOut);
        set(ModuleOut.class, objOut);
    }

    public ModuleOut getModule() {
        return get(ModuleOut.class);
    }

    @Override
    public String toString() {
        return "Event ID " + getEventId() + " for Module " + getModule().getId() + " on Server " + getServer().getId() + " occured @ " + getTime();
    }

}
