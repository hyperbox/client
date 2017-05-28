/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Maxime Dor
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

package io.kamax.hboxc.gui.utils;

import io.kamax.hboxc.gui.action.LoadingAction;

import javax.swing.*;


public class JButtonLoadable extends JButton {

    private static final long serialVersionUID = 3159387338470832277L;
    private Action initialAction;

    public JButtonLoadable(Action action) {
        super(action);
    }

    public void startLoad() {
        initialAction = getAction();
        setAction(LoadingAction.get());
    }

    public void stopLoad(boolean enable) {
        initialAction.setEnabled(enable);
        setAction(initialAction);
    }

    public void stopLoad() {
        stopLoad(true);
    }

}
