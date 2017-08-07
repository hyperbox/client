/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

import io.kamax.hboxc.gui.action.CloseAction;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class JDialogUtils {

    private static KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    public static void setCloseOnEscapeKey(JDialog comp, boolean close) {
        if (close) {
            comp.getRootPane().registerKeyboardAction(new CloseAction(comp), escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        } else {
            comp.getRootPane().getInputMap().remove(escapeKeyStroke);
        }
    }

    public static void setSizeAtLeast(JDialog dialog, int desiredWidth, int desiredHeight) {
        dialog.pack();
        int width = dialog.getWidth();
        int height = dialog.getHeight();
        if (desiredWidth > width) {
            width = desiredWidth;
        }
        if (desiredHeight > height) {
            height = desiredHeight;
        }
        dialog.setSize(width, height);
    }

}
