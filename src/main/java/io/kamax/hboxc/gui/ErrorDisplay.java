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

package io.kamax.hboxc.gui;

import io.kamax.tools.logging.Logger;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDisplay implements IPublicationErrorHandler {

    public static void display(String description, Throwable t) {
        Logger.exception(t);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        textArea.setText(writer.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(750, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "An Error Has Occurred", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handleError(PublicationError error) {
        display(error.getMessage(), error.getCause());
    }

}
