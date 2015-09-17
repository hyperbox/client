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

package io.kamax.hboxc.gui.action.connector;

import io.kamax.hbox.comm.Request;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.gui.connector._ConnectorSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class ConnectorConnectAction extends AbstractAction {

    private static final long serialVersionUID = -1316657520388449508L;
    private _ConnectorSelector select;

    public ConnectorConnectAction(_ConnectorSelector select) {
        this(select, "Connect");
    }

    public ConnectorConnectAction(_ConnectorSelector select, String label) {
        super(label);
        this.select = select;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        for (ConnectorOutput conOut : select.listConnectors()) {
            MessageWorker.execute(new Request(ClientTasks.ConnectorConnect, new ConnectorInput(conOut.getId())));
        }
    }

}
