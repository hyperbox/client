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
import io.kamax.hbox.comm.out.hypervisor.OsTypeOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._OsTypeListReceiver;
import java.util.List;

public class OsTypeListWorker extends AxSwingWorker<_OsTypeListReceiver, Void, OsTypeOut> {

    private String srvId;
    private String vmId;

    public OsTypeListWorker(_OsTypeListReceiver recv, String srvId, String vmId) {
        super(recv);
        this.srvId = srvId;
        this.vmId = vmId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        if (vmId != null) {
            for (OsTypeOut ostOut : Gui.getServer(srvId).listOsType(new MachineIn(vmId))) {
                publish(ostOut);
            }
        } else {
            for (OsTypeOut ostOut : Gui.getServer(srvId).listOsType()) {
                publish(ostOut);
            }
        }

        return null;
    }

    @Override
    protected void process(List<OsTypeOut> ostOutList) {
        getReceiver().add(ostOutList);
    }

    public static void execute(_WorkerTracker tracker, _OsTypeListReceiver recv, MachineOut mOut) {
        tracker.register(new OsTypeListWorker(recv, mOut.getServerId(), mOut.getUuid())).execute();
    }

}
