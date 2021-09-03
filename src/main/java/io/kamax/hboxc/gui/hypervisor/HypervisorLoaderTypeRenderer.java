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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import io.kamax.hboxc.gui.Gui;

import javax.swing.*;
import java.awt.*;

public class HypervisorLoaderTypeRenderer implements ListCellRenderer<HypervisorLoaderOut> {

    private ListCellRenderer<HypervisorLoaderOut> parent;

    public HypervisorLoaderTypeRenderer(ListCellRenderer<HypervisorLoaderOut> parent) {
        this.parent = parent;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HypervisorLoaderOut> list, HypervisorLoaderOut value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value != null) {
            return Gui.getHypervisorModel(value.getHypervisorId()).getTypeRenderer(parent)
                    .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else {
            return parent.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

}
