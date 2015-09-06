/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 - Maxime Dor
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.Request;
import io.kamax.hboxc.controller.MessageInput;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;

public class MessageWorker extends AxSwingWorker<_AnswerWorkerReceiver, Void, Void> {

    private Request req;

    public MessageWorker(Request req, _AnswerWorkerReceiver recv) {
        super(recv);
        this.req = req;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Gui.getReqRecv().post(new MessageInput(req, getReceiver()));

        return null;
    }

    public static void execute(Request req, _AnswerWorkerReceiver recv) {
        (new MessageWorker(req, recv)).execute();
    }

    public static void execute(Request req) {
        (new MessageWorker(req, new AnswerWorkerReceiver())).execute();
    }

}
