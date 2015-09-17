/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
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

package io.kamax.hboxc.gui.action.snapshot;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.SnapshotIn;
import io.kamax.hbox.comm.out.hypervisor.SnapshotOut;
import io.kamax.hboxc.gui.MainView;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.snapshot._SnapshotSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

public class SnapshotDeleteAction extends AbstractAction {

    private static final long serialVersionUID = -3490141907188974126L;
    private _SnapshotSelector selector;

    public SnapshotDeleteAction(String label, Icon icon, String toolTip, _SnapshotSelector selector) {
        super(label, icon);
        putValue(SHORT_DESCRIPTION, toolTip);
        this.selector = selector;
    }

    public SnapshotDeleteAction(_SnapshotSelector selector) {
        this(null, IconBuilder.getTask(HypervisorTasks.SnapshotDelete), "Delete the selected snapshot(s)", selector);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (selector.getSelection().size() > 0) {
            int info = JOptionPane
                    .showConfirmDialog(
                            MainView.getMainFrame(),
                            "This will delete the selected snapshot(s), merging the data with the previous state(s).\nThis cannot be canceled or rolled back!\nAre you sure?",
                            "Delete confirmation",
                            JOptionPane.WARNING_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
            if (info == JOptionPane.YES_OPTION) {
                for (SnapshotOut snapOut : selector.getSelection()) {
                    Request req = new Request(Command.VBOX, HypervisorTasks.SnapshotDelete);
                    req.set(new MachineIn(selector.getMachine()));
                    req.set(new SnapshotIn(snapOut.getUuid()));
                    MessageWorker.execute(req);
                }
            }
        }
    }

}
