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

import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hbox.exception.HypervisorNotConnectedException;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._NetModeListReceiver;

import java.util.List;

public class NetModeListWorker extends AxSwingWorker<_NetModeListReceiver, Void, NetModeOut> {

    private String srvId;

    public NetModeListWorker(_NetModeListReceiver recv, String srvId) {
        super(recv);
        this.srvId = srvId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        if (!Gui.getServer(srvId).isHypervisorConnected()) {
            throw new HypervisorNotConnectedException();
        }

        for (NetModeOut mode : Gui.getServer(srvId).getHypervisor().listNetworkModes()) {
            publish(mode);
        }

        return null;
    }

    @Override
    protected void process(List<NetModeOut> modes) {
        getReceiver().add(modes);
    }

    public static void execute(_NetModeListReceiver recv, String srvId) {
        (new NetModeListWorker(recv, srvId)).execute();
    }

}
