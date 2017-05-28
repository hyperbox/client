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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.out.host.HostOut;
import io.kamax.hbox.exception.HypervisorNotConnectedException;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._HostReceiver;

import java.util.concurrent.ExecutionException;

public class HostGetWorker extends AxSwingWorker<_HostReceiver, HostOut, Void> {

    private String srvId;

    public HostGetWorker(_HostReceiver recv, String srvId) {
        super(recv);
        this.srvId = srvId;
    }

    @Override
    protected HostOut innerDoInBackground() throws Exception {
        if (!Gui.getServer(srvId).isHypervisorConnected()) {
            throw new HypervisorNotConnectedException("Host information is not available while the hypervisor is not connected");
        }

        return Gui.getServer(srvId).getHost();
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        HostOut hostOut = get();
        getReceiver().put(hostOut);
    }

    public static void execute(_HostReceiver recv, String srvId) {
        (new HostGetWorker(recv, srvId)).execute();
    }

}
