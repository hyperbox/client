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

package io.kamax.hboxc.gui.action.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hboxc.gui.server.ServerEditorDialog;
import io.kamax.hboxc.gui.server._ServerSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;

public class ServerConfigureAction extends AbstractAction {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private _ServerSelector selector;

    public ServerConfigureAction(_ServerSelector selector) {
        super("Configure");
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        log.info("Action: Server Configure for Server #" + selector.getServer().getId());
        ServerIn srvIn = ServerEditorDialog.getInput(selector.getServer().getId());
        if (srvIn == null) {
            log.info("No server info was returned");
        } else {
            log.info("Server info was returned, sending data");
            MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.ServerConfigure, srvIn));
        }
    }

}
