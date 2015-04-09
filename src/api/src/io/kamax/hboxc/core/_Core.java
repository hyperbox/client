/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
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

package io.kamax.hboxc.core;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.core.connector._Connector;
import io.kamax.hboxc.server._Machine;
import io.kamax.hboxc.server._Server;
import io.kamax.hboxc.state.CoreState;
import io.kamax.hboxc.updater._Updater;
import java.util.List;

public interface _Core {

   public void init() throws HyperboxException;

   public void start() throws HyperboxException;

   public void stop();

   public void destroy();

   public CoreState getCoreState();

   public _Backend getBackend(String id);

   public List<String> listBackends();

   public List<_Connector> listConnector();

   public _Connector getConnector(String id);

   public _Connector addConnector(ConnectorInput conIn, UserIn usrIn);

   public _Connector modifyConnector(ConnectorInput conIn, UserIn usrIn);

   public _Connector connect(String id);

   public void disconnect(String id);

   public void removeConnector(String id);

   public _ConsoleViewer addConsoleViewer(String hypervisorId, String moduleId, String viewerPath, String args);

   public void removeConsoleViewer(String id);

   public _ConsoleViewer getConsoleViewer(String id);

   public _ConsoleViewer findConsoleViewer(String hypervisorId, String moduleId);

   public List<_ConsoleViewer> listConsoleViewer();

   public List<_ConsoleViewer> listConsoleViewer(String hypervisorTypeId);

   public void launchConsoleViewer(String serverId, String machineId);

   public void launchConsoleViewer(_Machine machine);

   public _Server getServer(String serverId);

   public _Connector getConnectorForServer(String serverId);

   public _Updater getUpdater();

}
