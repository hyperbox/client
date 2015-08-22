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

package io.kamax.hboxc.gui.action;

import io.kamax.hboxc.HyperboxClient;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class SaveAction extends AbstractAction {

    private static final long serialVersionUID = -5885429669111924805L;
    private _Saveable obj;

    public SaveAction(_Saveable obj) {
        this(obj, "Save");
    }

    public SaveAction(_Saveable obj, String label) {
        super(label);
        this.obj = obj;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            obj.save();
        } catch (Throwable t) {
            Logger.exception(t);
            HyperboxClient.getView().postError(t, "Cannot save: " + t.getMessage());
        }
    }

}
