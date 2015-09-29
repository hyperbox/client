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

package io.kamax.hboxc.gui.security.perm;

import io.kamax.hbox.comm.in.PermissionIn;
import io.kamax.hboxc.gui.utils.AbstractListTableModel;


public class PermissionTableModel extends AbstractListTableModel<PermissionIn> {

    private static final long serialVersionUID = -6520950420074634455L;

    private final String itemTypeCol = "Item Type";
    private final String itemCol = "Item";
    private final String actionCol = "Action";
    private final String accessCol = "Access";

    @Override
    protected void addColumns() {
        addColumn(itemTypeCol);
        addColumn(itemCol);
        addColumn(actionCol);
        addColumn(accessCol);
    }

    @Override
    protected Object getValueAt(PermissionIn obj, String columnName) {
        if (columnName.contentEquals(itemTypeCol)) {
            return obj.getItemTypeId();
        } else if (columnName.contentEquals(itemCol)) {
            return obj.getItemId();
        } else if (columnName.contentEquals(actionCol)) {
            return obj.getActionId();
        } else if (columnName.contentEquals(accessCol)) {
            return obj.isAllowed() ? "Grant" : "Deny";
        } else {
            return "";
        }
    }

}
