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

package io.kamax.hboxc.gui.action.security;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.security.user._UserSelector;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class UserRemoveAction extends AbstractAction {

   private _UserSelector selector;

   public UserRemoveAction(_UserSelector selector) {
      this(selector, "Delete");
   }

   public UserRemoveAction(_UserSelector selector, String label) {
      super(label, IconBuilder.getTask(HyperboxTasks.UserDelete));
      this.selector = selector;
   }

   @Override
   public void actionPerformed(ActionEvent ae) {
      for (String userId : selector.getSelection()) {
         Gui.post(new Request(Command.HBOX, HyperboxTasks.UserDelete, new ServerIn(selector.getServerId()), new UserIn(userId)));
      }
   }

}
