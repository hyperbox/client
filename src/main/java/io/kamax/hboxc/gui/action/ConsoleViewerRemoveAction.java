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

package io.kamax.hboxc.gui.action;

import io.kamax.hbox.comm.Request;
import io.kamax.hboxc.comm.input.ConsoleViewerInput;
import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.vm.console.viewer._ConsoleViewerSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class ConsoleViewerRemoveAction extends AbstractAction {

    private static final long serialVersionUID = 121514003218490085L;
    private _ConsoleViewerSelector selector;

    public ConsoleViewerRemoveAction(_ConsoleViewerSelector selector) {
        super(null, IconBuilder.DelIcon);
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        for (ConsoleViewerOutput cvOut : selector.getConsoleViewers()) {
            MessageWorker.execute(new Request(ClientTasks.ConsoleViewerRemove, new ConsoleViewerInput(cvOut.getId())));
        }
    }

}
