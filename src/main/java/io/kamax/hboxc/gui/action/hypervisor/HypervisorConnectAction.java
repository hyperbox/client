/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.action.hypervisor;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.gui.hypervisor.HypervisorConnectView;
import io.kamax.hboxc.gui.server._SingleServerSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class HypervisorConnectAction extends AbstractAction {

    private static final long serialVersionUID = -5412030998497315452L;
    private _SingleServerSelector selector;

    public HypervisorConnectAction(_SingleServerSelector selector) {
        this(selector, "Connect");
    }

    public HypervisorConnectAction(_SingleServerSelector selector, String label) {
        super(label);
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        ServerOut srvOut = selector.getServer();
        if (srvOut != null) {
            HypervisorIn hypIn = HypervisorConnectView.getInput(srvOut);
            if (hypIn != null) {
                Logger.debug("Got user input to connect hypervisor");
                Request req = new Request(Command.HBOX, HyperboxTasks.HypervisorConnect);
                req.set(new ServerIn(srvOut.getId()));
                req.set(hypIn);
                MessageWorker.execute(req);
            }
        } else {
            Logger.debug("No server was selected");
        }
    }

}
