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

package io.kamax.hboxc.gui.module;

import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hboxc.gui.utils.AbstractOutputListTableModel;

@SuppressWarnings("serial")
public class ModuleListTableModel extends AbstractOutputListTableModel<ModuleOut> {

   private final String ID = "ID";
   private final String NAME = "Name";
   private final String IS_LOADED = "Loaded";
   private final String DESCRIPTOR = "Descriptor";

   @Override
   protected void addColumns() {
      addColumn(ID);
      addColumn(NAME);
      addColumn(IS_LOADED);
      addColumn(DESCRIPTOR);
   }

   @Override
   protected Object getValueAt(ModuleOut obj, String columnName) {
      if (ID.equalsIgnoreCase(columnName)) {
         return obj.getId();
      }
      if (NAME.equalsIgnoreCase(columnName)) {
         return obj.getName();
      }
      if (IS_LOADED.equalsIgnoreCase(columnName)) {
         return Boolean.toString(obj.isLoaded());
      }
      if (DESCRIPTOR.equalsIgnoreCase(columnName)) {
         return obj.getDescriptorFile();
      }
      return null;
   }

}
