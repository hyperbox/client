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

package io.kamax.hboxc.gui.action.security;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.security.user.UserEditor;
import io.kamax.hboxc.gui.security.user._UserSelector;
import io.kamax.hboxc.gui.workers.MessageWorker;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UserModifyAction extends AbstractAction {

    private static final long serialVersionUID = 7869635770846824715L;
    private _UserSelector selector;

    public UserModifyAction(_UserSelector selector) {
        this(selector, "Edit");
    }

    public UserModifyAction(_UserSelector selector, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.UserModify));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        trigger(selector);
    }

    public static void trigger(_UserSelector selector) {
        List<String> selection = selector.getSelection();
        if (!selection.isEmpty()) {
            UserOut usrOut = Gui.getReader().getServerReader(selector.getServerId()).getUser(new UserIn(selector.getSelection().get(0)));
            UserIn usrIn = UserEditor.getInput(selector.getServerId(), usrOut);
            if (usrIn != null) {
                Logger.debug("Got user input");
                MessageWorker.execute(new Request(Command.HBOX, HyperboxTasks.UserModify, new ServerIn(selector.getServerId()), usrIn));
            }
        }
    }

}
