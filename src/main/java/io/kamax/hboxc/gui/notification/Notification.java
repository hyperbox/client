/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Max Dor
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

package io.kamax.hboxc.gui.notification;

import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.gui.builder.IconBuilder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Notification extends JPanel {

    private static final long serialVersionUID = 7333557872687445752L;
    private JLabel textLabel = new JLabel();
    private JLabel closeIcon = new JLabel(IconBuilder.getTask(ClientTasks.NotificationClose));

    protected JLabel getLabel() {
        return textLabel;
    }

    protected void setText(String text) {
        textLabel.setText(text);
    }

    public Notification() {
        super(new MigLayout("ins 0"));

        Border insets = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border line = BorderFactory.createLineBorder(new Color(0x00529B), 1);
        Border border = BorderFactory.createCompoundBorder(line, insets);
        setBorder(border);

        setOpaque(true);
        setBackground(new Color(0xBDE5F8));

        add(textLabel, "growx, pushx");
        add(closeIcon);

        closeIcon.addMouseListener(new MouseListener());
    }

    private class MouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent ev) {
            if ((ev.getButton() == MouseEvent.BUTTON1) && (ev.getClickCount() == 1)) {
                setVisible(false);
            }
        }

    }

}
