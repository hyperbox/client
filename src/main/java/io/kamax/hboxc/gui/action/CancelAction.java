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

package io.kamax.hboxc.gui.action;

import io.kamax.hboxc.gui._Cancelable;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class CancelAction extends AbstractAction {

    private static final long serialVersionUID = -5394602704151824335L;
    private _Cancelable obj;

    public CancelAction(_Cancelable obj) {
        super("Cancel");
        this.obj = obj;
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        obj.cancel();
    }

}
