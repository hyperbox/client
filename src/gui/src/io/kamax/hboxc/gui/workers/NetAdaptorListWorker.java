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

import io.kamax.hbox.comm.out.network.NetAdaptorOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._NetAdaptorListReceiver;
import java.util.List;

public class NetAdaptorListWorker extends AxSwingWorker<_NetAdaptorListReceiver, Void, NetAdaptorOut> {

    private String srvId;
    private String modeId;

    public NetAdaptorListWorker(_NetAdaptorListReceiver recv, String srvId, String modeId) {
        super(recv);
        this.srvId = srvId;
        this.modeId = modeId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (NetAdaptorOut adaptOut : Gui.getServer(srvId).getHypervisor().listAdaptors(modeId)) {
            publish(adaptOut);
        }

        return null;
    }

    @Override
    protected void process(List<NetAdaptorOut> adaptOutList) {
        getReceiver().add(adaptOutList);
    }

    public static void execute(_NetAdaptorListReceiver recv, String srvId, String modeId) {
        (new NetAdaptorListWorker(recv, srvId, modeId)).execute();
    }

}
