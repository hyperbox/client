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

package io.kamax.hboxc.gui.action.machine;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.vm._MachineSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class MachineAcpiPowerAction extends AbstractAction {

    private static final long serialVersionUID = 6523811937797754797L;
    private _MachineSelector selector;

    public MachineAcpiPowerAction(_MachineSelector selector) {
        super("Power Button", IconBuilder.getTask(HypervisorTasks.MachineAcpiPowerButton));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (MachineOut mOut : selector.getMachines()) {
            MessageWorker.execute(new Request(Command.VBOX, HypervisorTasks.MachineAcpiPowerButton, new MachineIn(mOut)));
        }
    }

}
