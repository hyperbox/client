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

package io.kamax.hboxc.gui.action.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hboxc.exception.ServerDisconnectedException;
import io.kamax.hboxc.gui.MainView;
import io.kamax.hboxc.gui.server._ServerSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class ServerShutdownAction extends AbstractAction {

    private static final long serialVersionUID = -8124327272371691374L;
    private _ServerSelector selector;

    public ServerShutdownAction(_ServerSelector selector) {
        super("Shutdown");
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Logger.verbose("Will send shutdown signal to " + selector.getServers().size() + " servers");
        for (ServerOut srvOut : selector.getServers()) {
            Logger.info("Prompting user for shutdown of " + srvOut);
            int info = JOptionPane.showConfirmDialog(
                    MainView.getMainFrame(),
                    "This will shutdown the Hyperbox Server, canceling all running and pending tasks and disconnect all users.\n"
                            + "The server can only be restarted from the host itself.\n"
                            + "Are you sure?",
                            "Shutdown confirmation",
                            JOptionPane.WARNING_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
            if (info == JOptionPane.YES_OPTION) {
                Logger.info("User accepted, sending shutdown signal to " + srvOut);
                try {
                    MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.ServerShutdown, new ServerIn(srvOut.getId())));
                } catch (ServerDisconnectedException e) {
                    // we ignore this exception as it can happen if the connection is terminated before the "ServerShutdown" packet reaches us.
                    // TODO make sure the server sends finishing signals to everything so the disconnect is clean
                }
            } else {
                Logger.info("User didn't proceed with shutdown of " + srvOut);
            }
        }
    }

}
