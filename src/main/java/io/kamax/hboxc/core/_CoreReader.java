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

package io.kamax.hboxc.core;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.output.BackendOutput;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.server._ServerReader;
import io.kamax.hboxc.state.CoreState;
import io.kamax.hboxc.updater._Updater;

import java.util.List;

public interface _CoreReader {

    public static final String CFGKEY_SERVER_CACHE_USE = "client.core.server.cache.use";
    public static final String CFGVAL_SERVER_CACHE_USE = "1";

    public CoreState getCoreState();

    public List<ConnectorOutput> listConnectors();

    public ConnectorOutput getConnector(ConnectorInput srvIn);

    public ConnectorOutput getConnector(String id);

    public ConnectorOutput getConnectorForServer(String srvId);

    public ServerOut getServer(ConnectorInput conIn);

    public ServerOut getServerInfo(String id);

    public _ServerReader getServerReader(String id);

    public List<ConsoleViewerOutput> listConsoleViewers();

    public List<BackendOutput> listBackends();

    public _Updater getUpdater();

}
