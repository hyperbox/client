/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.core.connector;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.comm.io.factory.ConnectorIoFactory;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.connector.ConnectorConnectedEvent;
import io.kamax.hboxc.event.connector.ConnectorDisconnectedEvent;
import io.kamax.hboxc.event.connector.ConnectorStateChangedEvent;
import io.kamax.hboxc.event.server.ServerDisconnectedEvent;
import io.kamax.hboxc.factory.ServerFactory;
import io.kamax.hboxc.server._Server;
import io.kamax.hboxc.state.ConnectionState;
import io.kamax.tools.AxStrings;
import io.kamax.tools.logging.KxLog;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Connector implements _Connector {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private String id;
    private String label;
    private String address;
    private String username;
    private String backendId;
    private String serverId;
    private _Server server;
    private ConnectionState state;

    public Connector(String id, String label, String address, String username, String backendId) {
        this.id = id;
        setLabel(label);
        setAddress(address);
        setUsername(username);
        setBackendId(backendId);
    }

    private void setState(ConnectionState state) {
        if ((this.state == null) || !this.state.equals(state)) {
            this.state = state;
            if (state.equals(ConnectionState.Connected)) {
                EventManager.post(new ConnectorConnectedEvent(ConnectorIoFactory.get(this)));
            } else if (state.equals(ConnectionState.Disconnected)) {
                EventManager.post(new ConnectorDisconnectedEvent(ConnectorIoFactory.get(this)));
            } else {
                EventManager.post(new ConnectorStateChangedEvent(ConnectorIoFactory.get(this), state));
            }
        } else {
            log.debug("Ignoring setState() - " + getState() + " is same as current");
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setAddress(String address) {
        if (AxStrings.isEmpty(address)) {
            throw new HyperboxException("Address cannot be empty");
        }

        this.address = address;
    }

    @Override
    public String getBackendId() {
        return backendId;
    }

    @Override
    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    @Override
    public _Server connect(UserIn usrIn) {
        setState(ConnectionState.Connecting);

        try {
            EventManager.register(this);
            server = ServerFactory.get();
            server.connect(address, backendId, usrIn);
            serverId = server.getId();
            setState(ConnectionState.Connected);

            return server;
        } finally {
            if (ConnectionState.Connecting.equals(getState())) {
                disconnect();
            }
        }
    }

    @Override
    public void disconnect() {
        if (!getState().equals(ConnectionState.Connected) && !getState().equals(ConnectionState.Connecting)) {
            log.debug("Ignoring disconnect call, already in " + getState() + " state");
            return;
        }

        setState(ConnectionState.Disconnecting);

        if (server != null) {
            server.disconnect();
            server = null;
        }

        setState(ConnectionState.Disconnected);
        EventManager.unregister(this);
    }

    @Override
    public boolean isConnected() {
        return (server != null) && server.isConnected();
    }

    @Override
    public _Server getServer() {
        if (!isConnected()) {
            throw new HyperboxException("Server is not connected");
        }

        return server;
    }

    @Override
    public ConnectionState getState() {
        return state == null ? ConnectionState.Disconnected : state;
    }

    @Handler
    private void putServerDisconnectEvent(ServerDisconnectedEvent ev) {
        if (AxStrings.equals(ev.getServer().getId(), server.getId())) { // FIXME Possible NPE
            disconnect();
        }
    }

    @Override
    public String getServerId() {
        return serverId;
    }

}
