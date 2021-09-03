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

package io.kamax.hboxc.gui.vm.console.viewer;

import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.constant.ConsoleViewerAttributes;
import io.kamax.hboxc.gui.utils.ObjectOutputTableModel;


public class ConsoleViewerTableModel extends ObjectOutputTableModel<ConsoleViewerOutput> {

    private static final long serialVersionUID = -4776712872703853672L;

    @Override
    protected void addColumns() {
        addColumn("Hypervisor", ConsoleViewerAttributes.HypervisorTypeId);
        addColumn("Module", ConsoleViewerAttributes.ModuleId);
        addColumn("Viewer", ConsoleViewerAttributes.ViewerPath);
    }

}
