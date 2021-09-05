/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Max Dor
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

package io.kamax.hboxc.gui.action.module;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ModuleIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.StoreItemOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.module._ModuleSelector;
import io.kamax.hboxc.gui.store.utils.StoreItemChooser;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ModuleRegisterAction extends AbstractAction {

    private static final long serialVersionUID = 7801761177660441210L;
    private _ModuleSelector selector;

    public ModuleRegisterAction(_ModuleSelector selector) {
        super("Register", IconBuilder.getTask(HyperboxTasks.ModuleRegister));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StoreItemOut stoOut = StoreItemChooser.getExisitingFile(selector.getServerId());
        if (stoOut != null) {
            ModuleIn modIn = new ModuleIn();
            modIn.setDescriptorFile(stoOut.getPath());
            MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.ModuleRegister, new ServerIn(selector.getServerId()), modIn));
        }
    }

}
