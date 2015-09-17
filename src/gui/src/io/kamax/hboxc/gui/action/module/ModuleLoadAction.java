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

package io.kamax.hboxc.gui.action.module;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ModuleIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.module._ModuleSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ModuleLoadAction extends AbstractAction {

    private static final long serialVersionUID = -3141029261282534092L;
    private _ModuleSelector selector;

    public ModuleLoadAction(_ModuleSelector selector) {
        super("Load", IconBuilder.getTask(HyperboxTasks.ModuleLoad));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ModuleOut mod : selector.getModuleSelection()) {
            MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.ModuleLoad, new ServerIn(selector.getServerId()), new ModuleIn(mod.getId())));
        }
    }

}
