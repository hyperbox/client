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

import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.PopupMenuBuilder;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class StorageDeviceAttachmentMediumEditAction extends AbstractAction {

    private static final long serialVersionUID = -3684625339196299265L;
    private _AnswerWorkerReceiver recv;
    private String serverId;
    private StorageDeviceAttachmentOut sdaOut;

    public StorageDeviceAttachmentMediumEditAction(String serverId, StorageDeviceAttachmentOut sdaOut) {
        this(serverId, sdaOut, new AnswerWorkerReceiver());
    }

    public StorageDeviceAttachmentMediumEditAction(String serverId, StorageDeviceAttachmentOut sdaOut, _AnswerWorkerReceiver recv) {
        super("", IconBuilder.getTask(HypervisorTasks.MediumModify));
        this.serverId = serverId;
        this.sdaOut = sdaOut;
        this.recv = recv;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JPopupMenu menuActions = PopupMenuBuilder.get(serverId, sdaOut, recv);
        if (ae.getSource() instanceof JComponent) {
            JComponent component = (JComponent) ae.getSource();
            menuActions.show(component, 0, component.getHeight());
        }
    }

}
