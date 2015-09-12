/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.builder.IconBuilder;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class RefreshAction extends AbstractAction {

    private static final long serialVersionUID = 4790936188063112797L;
    private _Refreshable refreshable;

    public RefreshAction(_Refreshable r) {
        super("Refresh", IconBuilder.RefreshIcon);
        refreshable = r;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        refreshable.refresh();
    }

}
