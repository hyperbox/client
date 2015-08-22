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

package io.kamax.hboxc.gui.action.hypervisor;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.controller.MessageInput;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.server._SingleServerSelector;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class HypervisorDisconnectAction extends AbstractAction {

    private static final long serialVersionUID = 6849363439923397210L;
    private _SingleServerSelector selector;

    public HypervisorDisconnectAction(_SingleServerSelector selector) {
        this(selector, "Disconnect");
    }

    public HypervisorDisconnectAction(_SingleServerSelector selector, String label) {
        super(label);
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ServerOut srvOut = selector.getServer();
        if (srvOut != null) {
            Request req = new Request(Command.HBOX, HyperboxTasks.HypervisorDisconnect, new ServerIn(srvOut.getId()));
            Gui.post(new MessageInput(req));
        } else {
            Logger.debug("No server was selected");
        }

    }

}
