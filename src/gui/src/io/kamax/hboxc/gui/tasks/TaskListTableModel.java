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

package io.kamax.hboxc.gui.tasks;

import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AbstractOutputListTableModel;
import io.kamax.tool.TimeFormater;

@SuppressWarnings("serial")
public final class TaskListTableModel extends AbstractOutputListTableModel<TaskOut> {

   private final String SERVER = "Server";
   private final String ID = "ID";
   private final String TASK = "Task";
   private final String USER = "User";
   private final String STATUS = "Status";
   private final String Q_TIME = "Queue Time";
   private final String S_TIME = "Start Time";
   private final String F_TIME = "Finish Time";

   @Override
   protected void addColumns() {
      addColumn(SERVER);
      addColumn(ID);
      addColumn(TASK);
      addColumn(USER);
      // addColumn("Progress");
      addColumn(STATUS);
      addColumn(Q_TIME);
      addColumn(S_TIME);
      addColumn(F_TIME);
   }

   @Override
   protected Object getValueAt(TaskOut tOut, String columnLabel) {
      if (columnLabel == SERVER) {
         try {
            return Gui.getServerInfo(tOut.getServerId()).getName();
         } catch (HyperboxException e) {
            // TODO catch proper exception
            // server disconnected meanwhile, we'll return the ID instead
            return tOut.getServerId();
         }
      }

      if (columnLabel == ID) {
         return tOut.getId();
      }

      if (columnLabel == TASK) {
         return tOut.getActionId();
      }

      if (columnLabel == USER) {
         return tOut.getUser().getDomainLogonName();
      }

      if (columnLabel == STATUS) {
         return tOut.getState().getId();
      }

      if (columnLabel == Q_TIME) {
         return tOut.getQueueTime() != null ? TimeFormater.get(tOut.getQueueTime()) : "N/A";
      }

      if (columnLabel == S_TIME) {
         return tOut.getStartTime() != null ? TimeFormater.get(tOut.getStartTime()) : "N/A";
      }

      if (columnLabel == F_TIME) {
         return tOut.getStopTime() != null ? TimeFormater.get(tOut.getStopTime()) : "N/A";
      }

      return null;
   }

   public void removeServer(String serverId) {
      for (TaskOut tskOut : list()) {
         if (tskOut.getServerId().equals(serverId)) {
            remove(tskOut);
         }
      }
   }

}
