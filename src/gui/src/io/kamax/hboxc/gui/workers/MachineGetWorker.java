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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._MachineReceiver;
import java.util.concurrent.ExecutionException;

public class MachineGetWorker extends AxSwingWorker<_MachineReceiver, MachineOut, Void> {

    private MachineOut mOut;

    public MachineGetWorker(_MachineReceiver recv, MachineOut mOut) {
        super(recv);
        this.mOut = mOut;
    }

    @Override
    protected MachineOut doInBackground() throws Exception {
        MachineOut newMachineOut = Gui.getServer(mOut.getServerId()).getMachine(mOut.getUuid());
        return newMachineOut;
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        MachineOut mOut = get();
        getReceiver().put(mOut);
    }

    public static void execute(_WorkerTracker tracker, _MachineReceiver recv, MachineOut mOut) {
        tracker.register(new MachineGetWorker(recv, mOut)).execute();
    }

}
