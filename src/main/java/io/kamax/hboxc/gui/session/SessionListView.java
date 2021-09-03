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

package io.kamax.hboxc.gui.session;

import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.ViewEventManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class SessionListView {

    private ServerOut srvOut;
    private JPanel panel;
    private SessionListTableModel sessionListModel;
    private JTable sessionList;

    public void init() throws HyperboxException {

        sessionListModel = new SessionListTableModel();
        sessionList = new JTable(sessionListModel);
        sessionList.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(sessionList);

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(scrollPane, "grow,push");

        ViewEventManager.register(this);
    }

    public JComponent getComponent() {
        return panel;
    }

    private void update() {
        sessionListModel.put(Gui.getServer(srvOut).listSessions());
    }

    public void update(ServerOut srvOut) {
        this.srvOut = srvOut;
        update();
    }

}
