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

package io.kamax.hboxc.gui.security.user;

import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hboxc.gui.utils.AbstractOutputListTableModel;

@SuppressWarnings("serial")
public class UserTableModel extends AbstractOutputListTableModel<UserOut> {

   private final String ID = "ID";
   private final String USERNAME = "Username";

   @Override
   protected void addColumns() {
      addColumn(ID);
      addColumn(USERNAME);
   }

   @Override
   protected Object getValueAt(UserOut obj, String columnName) {
      if (ID.equalsIgnoreCase(columnName)) {
         return obj.getId();
      }
      if (USERNAME.equalsIgnoreCase(columnName)) {
         return obj.getDomainLogonName();
      }
      return null;
   }

}
