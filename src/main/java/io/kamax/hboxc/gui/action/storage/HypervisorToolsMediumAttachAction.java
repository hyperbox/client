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
import io.kamax.hbox.comm.in.MediumIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.StorageDeviceAttachmentIn;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hboxc.controller.MessageInput;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HypervisorToolsMediumAttachAction extends AbstractAction {

    private static final long serialVersionUID = 9031406546855623059L;
    private _AnswerWorkerReceiver recv;
    private String serverId;
    private StorageDeviceAttachmentOut sdaOut;
    private MediumOut hypTools;

    public HypervisorToolsMediumAttachAction(String serverId, StorageDeviceAttachmentOut sdaOut, _AnswerWorkerReceiver recv) {
        this(serverId, sdaOut, "Attach Hypervisor Tools", IconBuilder.getTask(HypervisorTasks.MediumMount), true, recv);
    }

    public HypervisorToolsMediumAttachAction(final String serverId, StorageDeviceAttachmentOut sdaOut, String label, ImageIcon icon, boolean isEnabled,
                                             _AnswerWorkerReceiver recv) {
        super(label, icon);
        setEnabled(isEnabled);
        this.serverId = serverId;
        this.sdaOut = sdaOut;
        this.recv = recv;

        new SwingWorker<Void, Void>() {

            private Object oldIcon = getValue(Action.SMALL_ICON);

            {
                setEnabled(false);
                putValue(Action.SMALL_ICON, IconBuilder.LoadingIcon);
            }

            @Override
            protected Void doInBackground() throws Exception {
                hypTools = Gui.getServer(serverId).getHypervisor().getToolsMedium();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    setEnabled(true);
                } catch (Throwable t) {
                    Logger.warning("Error checking on Hypervisor tools: " + t.getMessage());
                } finally {
                    putValue(Action.SMALL_ICON, oldIcon);
                }
            }

        }.execute();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        new SwingWorker<Void, Void>() {

            {
                recv.loadingStarted();
            }

            @Override
            protected Void doInBackground() throws Exception {
                Request req = new Request(Command.VBOX, HypervisorTasks.MediumMount);
                req.set(new ServerIn(serverId));
                req.set(new MachineIn(sdaOut.getMachineUuid()));
                req.set(new StorageDeviceAttachmentIn(sdaOut.getControllerName(), sdaOut.getPortId(), sdaOut.getDeviceId(), sdaOut.getDeviceType()));
                req.set(new MediumIn(hypTools.getLocation(), hypTools.getDeviceType()));
                Gui.getReqRecv().post(new MessageInput(req, recv));

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    recv.loadingFinished(true, null);
                } catch (Throwable t) {
                    recv.loadingFinished(false, t);
                }
            }

        }.execute();
    }

}
