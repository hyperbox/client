/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
 *
 * https://apps.kamax.io/hyperbox
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

package io.kamax.hboxc.gui.store.utils;

import io.kamax.hbox.comm.out.StoreItemOut;
import io.kamax.hbox.constant.StoreItemAttribute;
import io.kamax.hboxc.gui.utils.ObjectOutputTableModel;


public class StoreItemBrowserTableModel extends ObjectOutputTableModel<StoreItemOut> {

    private static final long serialVersionUID = 6842450280493799463L;

    @Override
    protected void addColumns() {
        addColumn("Name", StoreItemAttribute.Name);
        addColumn("Size", StoreItemAttribute.Size);
    }

}
