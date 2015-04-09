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

package io.kamax.hboxc.gui.action.task;

import io.kamax.hbox.comm.in.TaskIn;
import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.tasks._TaskSelector;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public final class TaskCancelAction extends AbstractAction {

   private _TaskSelector selector;

   public TaskCancelAction(_TaskSelector selector) {
      super("Cancel");
      setEnabled(true);
      this.selector = selector;
   }

   @Override
   public void actionPerformed(ActionEvent ae) {
      for (TaskOut tOut : selector.getSelection()) {
         TaskIn tIn = new TaskIn(tOut.getId());
         Logger.debug("Canceling Task #" + tIn.getId());
         Gui.getServer(tOut.getServerId()).getTask(tOut.getId()).cancel();
      }
   }

}
