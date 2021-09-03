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

import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.updater._Release;
import io.kamax.tools.logging.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class UpdateAvailableNotification extends NotificationInfo {

    private static final long serialVersionUID = 2225815399285702337L;
    private URL downloadUrl;

    public UpdateAvailableNotification(_Release release) {
        downloadUrl = release.getDownloadURL();
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setText("New update available: " + release.getVersion() + " - " + downloadUrl);
        addMouseListener(new MouseListener());
    }

    private class MouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent ev) {
            if ((ev.getButton() == MouseEvent.BUTTON1) && (ev.getClickCount() == 1)) {
                if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Gui.showCopyPasteHelper("Browsing is not supported, please copy and paste the download URL in your browser", downloadUrl.toExternalForm());
                }

                try {
                    Desktop.getDesktop().browse(downloadUrl.toURI());
                } catch (IOException e) {
                    Gui.showError("Unable to browse to download location: " + e.getMessage());
                    Logger.exception(e);
                } catch (URISyntaxException e) {
                    Gui.showError("Unable to browse to download location: " + e.getMessage());
                    Logger.exception(e);
                }
            }
        }
    }

}
