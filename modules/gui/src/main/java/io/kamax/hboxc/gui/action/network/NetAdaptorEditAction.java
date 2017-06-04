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

package io.kamax.hboxc.gui.action.network;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.NetAdaptorIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.net.NetAdaptorDialog;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NetAdaptorEditAction extends AbstractAction {

    private static final long serialVersionUID = 1227441578681565462L;
    private String srvId;
    private String hypId;
    private String modeId;
    private String adaptId;

    public NetAdaptorEditAction(String srvId, String hypId, String modeId, String adaptId) {
        super("Edit", IconBuilder.getTask(HypervisorTasks.NetAdaptorModify));
        this.srvId = srvId;
        this.hypId = hypId;
        this.modeId = modeId;
        this.adaptId = adaptId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NetAdaptorIn adaptIn = NetAdaptorDialog.getInput(srvId, hypId, modeId, adaptId);
        if (adaptIn != null) {
            Request req = new Request(Command.VBOX, HypervisorTasks.NetAdaptorModify, adaptIn);
            req.set(ServerIn.class, new ServerIn(srvId));
            MessageWorker.execute(req);
        } else {
            Logger.debug("Net Adaptor creation: null input returned");
        }
    }

}
