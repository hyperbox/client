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

import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._ModuleListReceiver;
import java.util.List;

public class ModuleListWorker extends AxSwingWorker<_ModuleListReceiver, Void, ModuleOut> {

   private String srvId;

   public ModuleListWorker(_ModuleListReceiver recv, String srvId) {
      super(recv);
      this.srvId = srvId;
   }

   @Override
   protected Void doInBackground() throws Exception {
      for (ModuleOut modOut : Gui.getServer(srvId).listModules()) {
         publish(modOut);
      }

      return null;
   }

   @Override
   protected void process(List<ModuleOut> objOutList) {
      getReceiver().add(objOutList);
   }

   public static void execute(_ModuleListReceiver recv, String srvId) {
      (new ModuleListWorker(recv, srvId)).execute();
   }

}
