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

package io.kamax.hboxc.core.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.TaskIn;
import io.kamax.hboxc.server._Server;
import io.kamax.hboxc.server.task._Task;

public class Task implements _Task {

    private _Server srv;
    private String taskId;

    public Task(_Server srv, String taskId) {
        this.srv = srv;
        this.taskId = taskId;
    }

    @Override
    public void cancel() {
        srv.sendRequest(new Request(Command.HBOX, HyperboxTasks.TaskCancel, new TaskIn(taskId)));
    }

}
