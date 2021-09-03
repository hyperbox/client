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

package io.kamax.hboxc.controller.action.backend;

import io.kamax.hbox.comm.Answer;
import io.kamax.hbox.comm.AnswerType;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.comm.input.BackendInput;
import io.kamax.hboxc.comm.io.factory.BackendIoFactory;
import io.kamax.hboxc.comm.output.BackendOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.controller.action.AbstractClientControllerSingleAction;
import io.kamax.hboxc.core._Core;
import io.kamax.hboxc.front._Front;

public class BackendGetAction extends AbstractClientControllerSingleAction {

    @Override
    public Enum<?> getRegistration() {
        return ClientTasks.BackendGet;
    }

    @Override
    public void run(_Core core, _Front view, Request req, _AnswerReceiver recv) throws HyperboxException {
        BackendInput bckIn = req.get(BackendInput.class);
        _Backend bck = core.getBackend(bckIn.getId());
        BackendOutput bckOut = BackendIoFactory.get(bck);
        recv.putAnswer(new Answer(req, AnswerType.DATA, bckOut));
    }

}
