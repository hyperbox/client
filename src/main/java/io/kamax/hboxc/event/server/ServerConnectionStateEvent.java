/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

package io.kamax.hboxc.event.server;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.states.ServerConnectionState;
import io.kamax.hboxc.event.ClientEvents;

public class ServerConnectionStateEvent extends ServerEvent {

    private ServerConnectionState state;

    public ServerConnectionStateEvent(ServerOut srv, ServerConnectionState state) {
        super(ClientEvents.ServerConnectionState, srv);
        this.state = state;
    }

    public ServerConnectionState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Server ID " + getServer().getId() + " change connection state to " + getState() + " @ " + getTime();
    }

}
