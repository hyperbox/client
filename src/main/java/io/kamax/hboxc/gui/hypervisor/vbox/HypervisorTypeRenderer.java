/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

package io.kamax.hboxc.gui.hypervisor.vbox;

import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import io.kamax.hbox.hypervisor.vbox.VirtualBox;

import javax.swing.*;
import java.awt.*;


public class HypervisorTypeRenderer implements ListCellRenderer {

    private ListCellRenderer parent;

    public HypervisorTypeRenderer(ListCellRenderer parent) {
        this.parent = parent;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof HypervisorLoaderOut) {
            value = ((HypervisorLoaderOut) value).getTypeId();
        }

        if (VirtualBox.Type.WEB_SERVICES.equals(value)) {
            value = "Web Servcices";
        }

        if (VirtualBox.Type.XPCOM.equals(value)) {
            value = "Native (XPCOM)";
        }

        if (VirtualBox.Type.MSCOM.equals(value)) {
            value = "Native (MSCOM)";
        }

        return parent.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

}
