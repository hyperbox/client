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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.out.event.hypervisor.HypervisorConnectionStateEventOut;
import io.kamax.hbox.comm.out.event.net.NetAdaptorEventOut;
import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hboxc.event.connector.ConnectorStateChangedEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.utils.RefreshUtil;
import io.kamax.hboxc.gui.worker.receiver._NetModeListReceiver;
import io.kamax.hboxc.gui.workers.NetModeListWorker;
import io.kamax.tool.AxStrings;
import io.kamax.tool.logging.Logger;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

public class HypervisorNetViewer implements _Refreshable, _NetModeListReceiver {

    private volatile boolean isRefreshing = false;

    private String srvId;

    private JLabel status;
    private JPanel dataPanel;
    private JPanel panel;

    private Map<String, HypervisorNetModeViewer> compModes = new HashMap<String, HypervisorNetModeViewer>();

    public HypervisorNetViewer(String srvId) {
        this.srvId = srvId;

        status = new JLabel();
        dataPanel = new JPanel(new MigLayout("ins 0"));
        dataPanel.setVisible(false);
        panel = new JPanel(new MigLayout());
        panel.add(status, "growx, pushx, wrap, hidemode 3");
        panel.add(dataPanel, "grow, push, wrap, hidemode 3");

        RefreshUtil.set(panel, this);
        ViewEventManager.register(this);
        refresh();
    }

    public JComponent getComponent() {
        return panel;
    }

    public void refresh(String srvId) {
        this.srvId = srvId;
        refresh();
    }

    @Override
    public void refresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            NetModeListWorker.execute(this, srvId);
        }
    }

    @Handler
    private void putHypervisorConnectionEvent(HypervisorConnectionStateEventOut ev) {
        if (AxStrings.equals(srvId, ev.getServerId())) {
            refresh();
        }
    }

    @Handler
    private void putConnectorConnectionStateEvent(ConnectorStateChangedEvent ev) {
        if (AxStrings.equals(srvId, ev.getConnector().getServerId())) {
            refresh();
        }
    }

    @Handler
    private void putNetAdaptorEvent(NetAdaptorEventOut ev) {
        if (compModes.containsKey(ev.getNetMode())) {
            Logger.debug("Refreshing panel for mode " + ev.getNetMode());
            compModes.get(ev.getNetMode()).refresh();
        } else {
            Logger.debug("No panel for mode " + ev.getNetMode() + ", skipping refresh");
        }
    }

    private void clear() {
        for (Component c : dataPanel.getComponents()) {
            dataPanel.remove(c);
        }
    }

    private void setDataVisible(boolean isVisible) {
        status.setVisible(!isVisible);
        dataPanel.setVisible(isVisible);
        dataPanel.setEnabled(isVisible);
        if (!isVisible) {
            clear();
        }
    }

    @Override
    public void loadingStarted() {
        setDataVisible(false);
        status.setText("Loading...");
    }

    @Override
    public void loadingFinished(boolean isSuccessful, String message) {
        isRefreshing = false;
        status.setText(message);
        setDataVisible(isSuccessful);
    }

    @Override
    public void add(List<NetModeOut> modesOut) {
        for (NetModeOut modeOut : modesOut) {
            if (modeOut.canLinkAdaptor()) {
                HypervisorNetModeViewer viewer = new HypervisorNetModeViewer(srvId, modeOut);
                compModes.put(modeOut.getId(), viewer);
                viewer.getComponent().setBorder(BorderFactory.createTitledBorder(modeOut.getLabel()));
                dataPanel.add(viewer.getComponent(), "growx, pushx, wrap");
            } else {
                Logger.debug("Skipped Net mode " + modeOut.getLabel() + ": does not support linking to adaptors");
            }
        }
    }

}
