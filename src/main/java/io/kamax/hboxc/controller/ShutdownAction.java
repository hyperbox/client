/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
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

package io.kamax.hboxc.controller;

import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hboxc.controller.action.AbstractClientControllerSingleAction;
import io.kamax.hboxc.core._Core;
import io.kamax.hboxc.front._Front;

public final class ShutdownAction extends AbstractClientControllerSingleAction {

    static Controller c;

    @Override
    public Enum<?> getRegistration() {
        return ClientTasks.Exit;
    }

    @Override
    public void run(_Core core, _Front view, Request req, _AnswerReceiver recv) {
        c.stop();
    }

}
