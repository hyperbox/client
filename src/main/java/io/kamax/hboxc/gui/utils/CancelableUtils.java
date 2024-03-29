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

package io.kamax.hboxc.gui.utils;

import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui.action.CancelAction;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class CancelableUtils {

    private CancelableUtils() {
        // static only
    }

    private static final String cancelAction = "cancel";
    private static KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    public static void set(_Cancelable c, JComponent comp) {
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, cancelAction);
        comp.getActionMap().put(cancelAction, new CancelAction(c));
    }

    public static void unset(JComponent comp) {
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(escapeKeyStroke);
        comp.getActionMap().remove(cancelAction);
    }

}
