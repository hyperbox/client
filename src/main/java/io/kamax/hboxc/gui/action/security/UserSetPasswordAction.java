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

import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.exception.FeatureNotImplementedException;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.security.user._UserSelector;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class UserSetPasswordAction extends AbstractAction {

    private static final long serialVersionUID = 6518020230621674797L;
    private _UserSelector selector;

    public UserSetPasswordAction(_UserSelector selector) {
        this(selector, "Reset Password");
    }

    public UserSetPasswordAction(_UserSelector selector, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.UserModify));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        selector.getServerId();
        throw new FeatureNotImplementedException("Feature is currently not implemented");
    }

}
