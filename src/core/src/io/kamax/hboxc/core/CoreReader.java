/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

import net.engio.mbassy.listener.Handler;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.io.factory.BackendIoFactory;
import io.kamax.hboxc.comm.io.factory.ConnectorIoFactory;
import io.kamax.hboxc.comm.io.factory.ConsoleViewerIoFactory;
import io.kamax.hboxc.comm.io.factory.ServerIoFactory;
import io.kamax.hboxc.comm.output.BackendOutput;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.core._Core;
import io.kamax.hboxc.core._CoreReader;
import io.kamax.hboxc.core.server.CachedServerReader;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.server.ServerDisconnectedEvent;
import io.kamax.hboxc.factory.BackendFactory;
import io.kamax.hboxc.server._ServerReader;
import io.kamax.hboxc.state.CoreState;
import io.kamax.hboxc.updater._Updater;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CoreReader implements _CoreReader {

   private _Core core;
   private Map<_ServerReader, _ServerReader> cachedServerReaders = new WeakHashMap<_ServerReader, _ServerReader>();

   public CoreReader(_Core core) {
      this.core = core;
      EventManager.get().register(this);
   }

   @Override
   public CoreState getCoreState() {
      return core.getCoreState();
   }

   @Override
   public List<ConnectorOutput> listConnectors() {
      return ConnectorIoFactory.getList(core.listConnector());
   }

   @Override
   public ConnectorOutput getConnector(ConnectorInput conIn) {
      return getConnector(conIn.getId());
   }

   @Override
   public _ServerReader getServerReader(String id) {
      _ServerReader cachedReader = cachedServerReaders.get(core.getServer(id));
      if (cachedReader == null) {
         cachedReader = new CachedServerReader(core.getServer(id));
         cachedServerReaders.put(core.getServer(id), cachedReader);
      }

      return cachedReader;
   }

   @Override
   public List<ConsoleViewerOutput> listConsoleViewers() {
      return ConsoleViewerIoFactory.getOutList(core.listConsoleViewer());
   }

   @Override
   public List<BackendOutput> listBackends() {
      return BackendIoFactory.getListId(BackendFactory.list());
   }

   @Override
   public ServerOut getServer(ConnectorInput conIn) {
      return ServerIoFactory.get(core.getConnector(conIn.getId()).getServer());
   }

   @Override
   public ConnectorOutput getConnector(String id) {
      return ConnectorIoFactory.get(core.getConnector(id));
   }

   @Override
   public ServerOut getServerInfo(String id) {
      return ServerIoFactory.get(core.getServer(id));
   }

   @Handler
   protected void putServerDisconnected(ServerDisconnectedEvent ev) {
      cachedServerReaders.remove(core.getServer(ev.getServer().getId()));
   }

   @Override
   public ConnectorOutput getConnectorForServer(String srvId) {
      return ConnectorIoFactory.get(core.getConnectorForServer(srvId));
   }

   @Override
   public _Updater getUpdater() {
      return core.getUpdater();
   }

}
