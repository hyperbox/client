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
import io.kamax.hboxc.gui.store._StoreSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StoreDeleteAction extends AbstractAction {

    private static final long serialVersionUID = 45779716838996171L;
    private _StoreSelector selector;

    public StoreDeleteAction(_StoreSelector selector) {
        this(selector, "Delete");
    }

    public StoreDeleteAction(_StoreSelector selector, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.StoreDelete));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int info = JOptionPane.showConfirmDialog(
                null,
                "This will delete the store and all its content.\nThis cannot be canceled or rolled back!\nAre you sure?",
                "Delete confirmation",
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        if (info == JOptionPane.YES_OPTION) {
            for (String storeId : selector.getSelection()) {
                MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.StoreDelete, new ServerIn(selector.getServer()), new StoreIn(storeId)));
            }
        }
    }

}
