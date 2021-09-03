/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._HypervisorLoaderListReceiver;

import java.util.List;

public class HypervisorLoaderListWorker extends AxSwingWorker<_HypervisorLoaderListReceiver, Void, HypervisorLoaderOut> {

    private String serverId;

    public HypervisorLoaderListWorker(_HypervisorLoaderListReceiver recv, String serverId) {
        super(recv);
        this.serverId = serverId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (HypervisorLoaderOut hypLoadOut : Gui.getServer(serverId).listHypervisors()) {
            publish(hypLoadOut);
        }

        return null;
    }

    @Override
    protected void process(List<HypervisorLoaderOut> typeList) {
        getReceiver().add(typeList);
    }

    public static void execute(_HypervisorLoaderListReceiver recv, String srvId) {
        (new HypervisorLoaderListWorker(recv, srvId)).execute();
    }

}
