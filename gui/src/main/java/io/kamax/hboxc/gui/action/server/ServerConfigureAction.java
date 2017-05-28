/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.gui.action.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hboxc.gui.server.ServerEditorDialog;
import io.kamax.hboxc.gui.server._ServerSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ServerConfigureAction extends AbstractAction {

    private static final long serialVersionUID = 4982724588848783344L;
    private _ServerSelector selector;

    public ServerConfigureAction(_ServerSelector selector) {
        super("Configure");
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Logger.info("Action: Server Configure for Server #" + selector.getServer().getId());
        ServerIn srvIn = ServerEditorDialog.getInput(selector.getServer().getId());
        if (srvIn == null) {
            Logger.info("No server info was returned");
        } else {
            Logger.info("Server info was returned, sending data");
            MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.ServerConfigure, srvIn));
        }
    }

}
