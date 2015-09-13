/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Maxime Dor
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

import io.kamax.hbox.comm.out.hypervisor.SnapshotOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._SnapshotGetReceiver;
import java.util.concurrent.ExecutionException;

public class SnapshotGetWorker extends AxSwingWorker<_SnapshotGetReceiver, SnapshotOut, Void> {

    private String srvId;
    private String vmId;
    private String snapId;

    public SnapshotGetWorker(_SnapshotGetReceiver recv, String srvId, String vmId, String snapId) {
        super(recv);
        this.srvId = srvId;
        this.vmId = vmId;
        this.snapId = snapId;
    }

    @Override
    protected SnapshotOut innerDoInBackground() throws Exception {
        return Gui.getServer(srvId).getSnapshot(vmId, snapId);
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        getReceiver().put(srvId, vmId, get());
    }

    public static void execute(_SnapshotGetReceiver recv, String srvId, String vmId, String snapId) {
        (new SnapshotGetWorker(recv, srvId, vmId, snapId)).execute();
    }

}
