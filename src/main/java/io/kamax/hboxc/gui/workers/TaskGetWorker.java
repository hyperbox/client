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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.in.TaskIn;
import io.kamax.hbox.comm.out.TaskOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._TaskReceiver;

import java.util.concurrent.ExecutionException;

public class TaskGetWorker extends AxSwingWorker<_TaskReceiver, TaskOut, Void> {

    private TaskOut objOut;

    public TaskGetWorker(_TaskReceiver recv, TaskOut objOut) {
        super(recv);
        this.objOut = objOut;
    }

    @Override
    protected TaskOut innerDoInBackground() throws Exception {
        return Gui.getServer(objOut.getServerId()).getTask(new TaskIn(objOut));
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        TaskOut tOut = get();
        getReceiver().put(tOut);
    }

    public static void execute(_TaskReceiver recv, TaskOut objOut) {
        (new TaskGetWorker(recv, objOut)).execute();
    }

}
