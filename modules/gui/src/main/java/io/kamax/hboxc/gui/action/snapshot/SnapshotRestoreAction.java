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

package io.kamax.hboxc.gui.action.snapshot;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.SnapshotIn;
import io.kamax.hboxc.gui.snapshot._SnapshotSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SnapshotRestoreAction extends AbstractAction {

    private static final long serialVersionUID = 3466077525649598001L;
    private _SnapshotSelector selector;

    public SnapshotRestoreAction(_SnapshotSelector selector) {
        this.selector = selector;
        putValue(SHORT_DESCRIPTION, "Restore the machine state to the selected snapshot");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Request req = new Request(Command.VBOX, HypervisorTasks.SnapshotRestore);
        req.set(new MachineIn(selector.getMachine()));
        req.set(new SnapshotIn(selector.getSelection().get(0).getUuid()));
        MessageWorker.execute(req);
    }

}
