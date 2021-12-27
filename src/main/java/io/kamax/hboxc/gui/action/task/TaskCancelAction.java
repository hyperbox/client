/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
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

package io.kamax.hboxc.gui.action.task;

import io.kamax.hbox.comm.in.TaskIn;
import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.tasks._TaskSelector;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver.WorkerDataReceiver;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;

public final class TaskCancelAction extends AbstractAction {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private _TaskSelector selector;

    public TaskCancelAction(_TaskSelector selector) {
        super("Cancel");
        setEnabled(true);
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        for (final TaskOut tOut : selector.getSelection()) {
            final TaskIn tIn = new TaskIn(tOut.getId());
            new AxSwingWorker<WorkerDataReceiver, Void, Void>(new WorkerDataReceiver()) {

                @Override
                protected Void innerDoInBackground() throws Exception {
                    log.debug("Canceling Task #" + tIn.getId());
                    Gui.getServer(tOut.getServerId()).getTask(tOut.getId()).cancel();
                    return null;
                }

            };

        }
    }

}
