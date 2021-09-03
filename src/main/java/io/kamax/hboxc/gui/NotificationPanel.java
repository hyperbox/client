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

package io.kamax.hboxc.gui;

import io.kamax.hboxc.event.updater.UpdaterUpdateAvailableEvent;
import io.kamax.hboxc.gui.notification.UpdateAvailableNotification;
import io.kamax.tools.logging.Logger;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class NotificationPanel extends JPanel {

    private static final long serialVersionUID = 3064525253691111344L;
    private volatile Map<Enum<?>, Component> notifications = new HashMap<Enum<?>, Component>();

    public NotificationPanel() {
        super(new MigLayout("ins 0"));
        ViewEventManager.register(this);
        setVisible(false);
    }

    @Handler
    private void putUpdateAvailableEvent(UpdaterUpdateAvailableEvent ev) {
        if (notifications.containsKey(ev.getEventId())) {
            Logger.debug("Update available panel is already added, skipping");
        } else {
            Component updateLabel = new UpdateAvailableNotification(Gui.getReader().getUpdater().getUpdate());
            notifications.put(ev.getEventId(), updateLabel);
            add(updateLabel, "hidemode 3, growx, pushx, wrap");
            if (!isVisible()) {
                setVisible(true);
            }
        }
    }

}
