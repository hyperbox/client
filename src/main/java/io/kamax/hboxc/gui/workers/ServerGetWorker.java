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

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._ServerReceiver;

import java.util.concurrent.ExecutionException;

public class ServerGetWorker extends AxSwingWorker<_ServerReceiver, ServerOut, Void> {

    private String srvId;

    public ServerGetWorker(_ServerReceiver recv, String srvId) {
        super(recv);
        this.srvId = srvId;
    }

    @Override
    protected ServerOut innerDoInBackground() throws Exception {
        ServerOut newSrvOut = Gui.getServerInfo(srvId);
        return newSrvOut;
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        ServerOut srvOut = get();
        getReceiver().put(srvOut);
    }

    public static void execute(_ServerReceiver recv, String srvId) {
        new ServerGetWorker(recv, srvId).execute();
    }

}
