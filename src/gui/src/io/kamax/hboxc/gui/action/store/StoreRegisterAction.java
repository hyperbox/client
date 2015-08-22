/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.action.store;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.StoreIn;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.server._SingleServerSelector;
import io.kamax.hboxc.gui.store.StoreEditor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class StoreRegisterAction extends AbstractAction {

    private static final long serialVersionUID = 2570216069504356093L;
    private _SingleServerSelector select;

    public StoreRegisterAction(_SingleServerSelector select) {
        this(select, "Register");
    }

    public StoreRegisterAction(_SingleServerSelector select, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.StoreRegister));
        putValue(SHORT_DESCRIPTION, "Register the target as a new Store\nThe target MUST exist.");
        this.select = select;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StoreIn stoIn = StoreEditor.getInputRegister(select.getServer().getId());
        if (stoIn != null) {
            Gui.post(new Request(Command.HBOX, HyperboxTasks.StoreRegister, new ServerIn(select.getServer().getId()), stoIn));
        }
    }

}
