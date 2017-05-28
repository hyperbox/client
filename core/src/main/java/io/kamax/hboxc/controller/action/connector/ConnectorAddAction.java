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

package io.kamax.hboxc.controller.action.connector;

import io.kamax.hbox.comm.Answer;
import io.kamax.hbox.comm.AnswerType;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.io.factory.ConnectorIoFactory;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.controller.action.AbstractClientControllerSingleAction;
import io.kamax.hboxc.core._Core;
import io.kamax.hboxc.core.connector._Connector;
import io.kamax.hboxc.front._Front;

public class ConnectorAddAction extends AbstractClientControllerSingleAction {

    @Override
    public Enum<?> getRegistration() {
        return ClientTasks.ConnectorAdd;
    }

    @Override
    public void run(_Core core, _Front view, Request req, _AnswerReceiver recv) throws HyperboxException {
        ConnectorInput conIn = req.get(ConnectorInput.class);
        UserIn usrIn = req.get(UserIn.class);
        _Connector conn = core.addConnector(conIn, usrIn);
        recv.putAnswer(new Answer(req, AnswerType.DATA, ConnectorIoFactory.get(conn)));
    }

}
