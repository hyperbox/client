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

package io.kamax.hboxc.gui.action.storage;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.MediumIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.StorageDeviceAttachmentIn;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;
import io.kamax.hboxc.gui.workers.MessageWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class HypervisorToolsMediumAttachAction extends AbstractAction {

    private static final long serialVersionUID = 9031406546855623059L;
    private _AnswerWorkerReceiver recv;
    private String serverId;
    private StorageDeviceAttachmentOut sdaOut;
    private MediumOut hypTools;

    public HypervisorToolsMediumAttachAction(String serverId, StorageDeviceAttachmentOut sdaOut, MediumOut hypTools, _AnswerWorkerReceiver recv) {
        this(serverId, sdaOut, hypTools, "Attach Hypervisor Tools", IconBuilder.getTask(HypervisorTasks.MediumMount), true, recv);
    }

    public HypervisorToolsMediumAttachAction(String serverId, StorageDeviceAttachmentOut sdaOut, MediumOut hypTools, String label, ImageIcon icon, boolean isEnabled,
            _AnswerWorkerReceiver recv) {
        super(label, icon);
        setEnabled(isEnabled && hypTools != null);
        this.serverId = serverId;
        this.sdaOut = sdaOut;
        this.hypTools = hypTools;
        this.recv = recv;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Request req = new Request(Command.VBOX, HypervisorTasks.MediumMount);
        req.set(new ServerIn(serverId));
        req.set(new MachineIn(sdaOut.getMachineUuid()));
        req.set(new StorageDeviceAttachmentIn(sdaOut.getControllerName(), sdaOut.getPortId(), sdaOut.getDeviceId(), sdaOut.getDeviceType()));
        req.set(new MediumIn(hypTools.getLocation(), hypTools.getDeviceType()));
        MessageWorker.execute(req, recv);
    }

}
