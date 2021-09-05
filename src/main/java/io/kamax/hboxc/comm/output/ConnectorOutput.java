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

package io.kamax.hboxc.comm.output;

import io.kamax.hbox.comm.io.SettingIO;
import io.kamax.hbox.comm.out.ObjectOut;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.constant.ClientEntity;
import io.kamax.hboxc.constant.ConnectorAttributes;
import io.kamax.hboxc.state.ConnectionState;
import io.kamax.tools.AxStrings;

import java.util.Collection;

public class ConnectorOutput extends ObjectOut {

    protected ConnectorOutput() {
        // do not use
    }

    public ConnectorOutput(String id) {
        super(ClientEntity.Connector, id);
    }

    public ConnectorOutput(String id, Collection<SettingIO> settings) {
        super(ClientEntity.Connector, id, settings);
    }

    public String getLabel() {
        return getSetting(ConnectorAttributes.Label).getString();
    }

    public ConnectionState getState() {
        return (ConnectionState) getSetting(ConnectorAttributes.ConnectionState).getRawValue();
    }

    public boolean isConnected() {
        return getSetting(ConnectorAttributes.isConnected).getBoolean();
    }

    public String getBackendId() {
        return getSetting(ConnectorAttributes.BackendId).getString();
    }

    public String getAddress() {
        return getSetting(ConnectorAttributes.Address).getString();
    }

    public String getUsername() {
        return getSetting(ConnectorAttributes.Username).getString();
    }

    public ServerOut getServer() {
        return (ServerOut) getSetting(ConnectorAttributes.Server).getRawValue();
    }

    public String getServerId() {
        return getSetting(ConnectorAttributes.ServerId).getString();
    }

    @Override
    public String toString() {
        return AxStrings.isEmpty(getLabel()) ? getId() : getLabel();
    }

}
