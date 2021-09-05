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

package io.kamax.hboxc.gui.action.storage;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.StorageDeviceAttachmentIn;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MediumDettachAction extends AbstractAction {

    private static final long serialVersionUID = -3310041024462638526L;
    private _AnswerWorkerReceiver recv;
    private String serverId;
    private StorageDeviceAttachmentOut sdaOut;

    public MediumDettachAction(String serverId, StorageDeviceAttachmentOut sdaOut, boolean isEnabled, _AnswerWorkerReceiver recv) {
        this(serverId, sdaOut, "Detach Medium", IconBuilder.getTask(HypervisorTasks.MediumUnmount), isEnabled, recv);
    }

    public MediumDettachAction(String serverId, StorageDeviceAttachmentOut sdaOut, String label, ImageIcon icon, boolean isEnabled, _AnswerWorkerReceiver recv) {
        super(label, icon);
        setEnabled(isEnabled);
        this.serverId = serverId;
        this.sdaOut = sdaOut;
        this.recv = recv;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Request req = new Request(Command.VBOX, HypervisorTasks.MediumUnmount);
        req.set(new ServerIn(serverId));
        req.set(new MachineIn(sdaOut.getMachineUuid()));
        req.set(new StorageDeviceAttachmentIn(sdaOut.getControllerName(), sdaOut.getPortId(), sdaOut.getDeviceId()));
        MessageWorker.execute(req, recv);
    }

}
