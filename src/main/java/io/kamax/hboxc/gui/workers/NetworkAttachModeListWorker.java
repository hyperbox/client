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

import io.kamax.hbox.comm.out.network.NetworkAttachModeOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachModeReceiver;

import java.util.List;

public class NetworkAttachModeListWorker extends AxSwingWorker<_NetworkAttachModeReceiver, Void, NetworkAttachModeOut> {

    private String srvId;

    public NetworkAttachModeListWorker(_NetworkAttachModeReceiver recv, String serverId) {
        super(recv);
        this.srvId = serverId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (NetworkAttachModeOut ostOut : Gui.getServer(srvId).listNetworkAttachModes()) {
            publish(ostOut);
        }

        return null;
    }

    @Override
    protected void process(List<NetworkAttachModeOut> ostOutList) {
        getReceiver().add(ostOutList);
    }

    public static void execute(_WorkerTracker tracker, _NetworkAttachModeReceiver recv, String serverId) {
        tracker.register(new NetworkAttachModeListWorker(recv, serverId)).execute();
    }

}
