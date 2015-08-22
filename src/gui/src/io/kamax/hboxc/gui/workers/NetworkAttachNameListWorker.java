/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.in.NetworkAttachModeIn;
import io.kamax.hbox.comm.out.network.NetworkAttachNameOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachNameReceiver;
import java.util.List;

public class NetworkAttachNameListWorker extends AxSwingWorker<_NetworkAttachNameReceiver, Void, NetworkAttachNameOut> {

   private String srvId;
   private String netAttachModeId;

   public NetworkAttachNameListWorker(_NetworkAttachNameReceiver recv, String srvId, String netAttachModeId) {
      super(recv);
      this.srvId = srvId;
      this.netAttachModeId = netAttachModeId;
   }

   @Override
   protected Void doInBackground() throws Exception {
      for (NetworkAttachNameOut nanOut : Gui.getServer(srvId).listNetworkAttachNames(new NetworkAttachModeIn(netAttachModeId))) {
         publish(nanOut);
      }

      return null;
   }

   @Override
   protected void process(List<NetworkAttachNameOut> nanOut) {
      getReceiver().add(nanOut);
   }

   public static void execute(_WorkerTracker tracker, _NetworkAttachNameReceiver recv, String srvId, String netAttachModeId) {
      tracker.register(new NetworkAttachNameListWorker(recv, srvId, netAttachModeId)).execute();
   }

}
