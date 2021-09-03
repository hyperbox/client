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

package io.kamax.hboxc.gui.snapshot;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.SnapshotIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.hypervisor.SnapshotOut;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._SnapshotGetReceiver;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.hboxc.gui.workers.SnapshotGetWorker;
import io.kamax.tools.AxStrings;

import javax.swing.*;

public class SnapshotModifyDialog implements _Saveable, _Cancelable, _SnapshotGetReceiver {

    private Action saveAction = new SaveAction(this);
    private SnapshotEditorDialog editor;
    private MachineOut mOut;
    private SnapshotIn snapIn;
    private SnapshotOut snapOut;

    public SnapshotModifyDialog(MachineOut mOut, SnapshotOut snapOut) {
        this.mOut = mOut;
        this.snapOut = snapOut;
        saveAction = new SaveAction(this);

        editor = new SnapshotEditorDialog(saveAction, new CancelAction(this));
    }

    public static void show(MachineOut mOut, SnapshotOut snapOut) {
        SnapshotModifyDialog instance = new SnapshotModifyDialog(mOut, snapOut);
        SnapshotGetWorker.execute(instance, mOut.getServerId(), mOut.getUuid(), snapOut.getUuid());
        instance.editor.setDialogTitle("Edit Snapshot");
        instance.editor.getDialog().pack();
        instance.editor.getDialog().setLocationRelativeTo(instance.editor.getDialog().getParent());
        instance.editor.getDialog().setVisible(true);
    }

    private void hide() {
        editor.getDialog().setVisible(false);
        editor.getDialog().dispose();
    }

    @Override
    public void save() {
        snapIn = new SnapshotIn(snapOut.getUuid());
        if (!AxStrings.contentEquals(editor.getName(), snapOut.getName())) {
            snapIn.setName(editor.getName());
        }
        if (!AxStrings.contentEquals(editor.getDescription(), snapOut.getDescription())) {
            snapIn.setDescription(editor.getDescription());
        }

        if (snapIn.hasNewData()) {
            MessageWorker.execute(new Request(Command.VBOX, HypervisorTasks.SnapshotModify, new MachineIn(mOut), snapIn), new AnswerWorkerReceiver() {

                @Override
                public void start() {
                    editor.getSaveButton().setAction(LoadingAction.get());
                }

                @Override
                public void success() {
                    hide();
                }

                @Override
                public void fail(Throwable t) {
                    editor.getSaveButton().setAction(saveAction);
                    super.fail(t);
                }
            });
        }


    }

    @Override
    public void cancel() {
        hide();
    }

    @Override
    public void loadingStarted() {
        editor.getSaveButton().setAction(LoadingAction.get());
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        editor.getSaveButton().setAction(saveAction);
        editor.getSaveButton().setEnabled(isSuccessful);
    }

    @Override
    public void put(String srvId, String vmId, SnapshotOut snapOut) {
        this.snapOut = snapOut;
        editor.setName(snapOut.getName());
        editor.setDescription(snapOut.getDescription());
        editor.setDialogTitle("Snapshot Edit - " + snapOut.getName());
    }

}
