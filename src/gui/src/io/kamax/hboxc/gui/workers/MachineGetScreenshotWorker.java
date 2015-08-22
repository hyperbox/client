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

import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.hypervisor.ScreenshotOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._MachineScreenshotReceiver;
import java.util.concurrent.ExecutionException;

public class MachineGetScreenshotWorker extends AxSwingWorker<_MachineScreenshotReceiver, ScreenshotOut, Void> {

    private MachineOut mOut;

    public MachineGetScreenshotWorker(_MachineScreenshotReceiver recv, MachineOut mOut) {
        super(recv);
        this.mOut = mOut;
    }

    @Override
    protected ScreenshotOut doInBackground() throws Exception {
        return Gui.getServer(mOut.getServerId()).getScreenshot(new MachineIn(mOut));
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        ScreenshotOut scrOut = get();
        getReceiver().put(scrOut);
    }

    public static void execute(_MachineScreenshotReceiver recv, MachineOut mOut) {
        (new MachineGetScreenshotWorker(recv, mOut)).execute();
    }

}
