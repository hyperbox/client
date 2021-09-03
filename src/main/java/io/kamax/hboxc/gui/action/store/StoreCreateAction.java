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

package io.kamax.hboxc.gui.action.store;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.StoreIn;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.server._SingleServerSelector;
import io.kamax.hboxc.gui.store.StoreEditor;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StoreCreateAction extends AbstractAction {

    private static final long serialVersionUID = 4135617259287266131L;
    private _SingleServerSelector select;

    public StoreCreateAction(_SingleServerSelector select) {
        this(select, "Create");
    }

    public StoreCreateAction(_SingleServerSelector select, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.StoreCreate));
        putValue(SHORT_DESCRIPTION, "Create and register the target as a new Store.\nThe target must NOT exist.");
        this.select = select;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StoreIn stoIn = StoreEditor.getInputCreate(select.getServer().getId());
        if (stoIn != null) {
            MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.StoreCreate, new ServerIn(select.getServer()), stoIn));
        }
    }

}
