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

package io.kamax.hboxc.gui.store;

import io.kamax.hbox.comm.out.StoreOut;
import io.kamax.hboxc.gui.utils.AbstractOutputListTableModel;


public final class StoreListTableModel extends AbstractOutputListTableModel<StoreOut> {

    private static final long serialVersionUID = 7772589603382903388L;
    private final String ID = "ID";
    private final String IS_VALID = "Valid";
    private final String LABEL = "Label";
    private final String LOC = "Location";

    @Override
    protected void addColumns() {
        addColumn(ID);
        addColumn(IS_VALID);
        addColumn(LABEL);
        addColumn(LOC);
    }

    @Override
    protected Object getValueAt(StoreOut obj, String columnName) {
        if (ID.equalsIgnoreCase(columnName)) {
            return obj.getId();
        }
        if (IS_VALID.equalsIgnoreCase(columnName)) {
            return obj.isValid();
        }
        if (LABEL.equalsIgnoreCase(columnName)) {
            return obj.getLabel();
        }
        if (LOC.equalsIgnoreCase(columnName)) {
            return obj.getLocation();
        }
        return null;
    }

}
