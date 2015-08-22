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
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.net.NetAdaptorDialog;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class NetAdaptorAddAction extends AbstractAction {

    private static final long serialVersionUID = -1475838049060205072L;
    private String srvId;
    private String modeId;

    public NetAdaptorAddAction(String srvId, String modeId) {
        super("Add", IconBuilder.getTask(HypervisorTasks.NetAdaptorAdd));
        this.srvId = srvId;
        this.modeId = modeId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NetAdaptorIn adaptIn = NetAdaptorDialog.getInput(srvId, modeId, null);
        if (adaptIn != null) {
            Request req = new Request(Command.VBOX, HypervisorTasks.NetAdaptorAdd, adaptIn);
            req.set(ServerIn.class, new ServerIn(srvId));
            Gui.post(req);
        } else {
            Logger.debug("Net Adaptor creation: null input returned");
        }
    }

}
