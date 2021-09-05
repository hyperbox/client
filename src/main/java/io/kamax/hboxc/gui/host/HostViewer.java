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

package io.kamax.hboxc.gui.host;

import io.kamax.hbox.comm.out.event.hypervisor.HypervisorConnectedEventOut;
import io.kamax.hbox.comm.out.event.hypervisor.HypervisorDisconnectedEventOut;
import io.kamax.hbox.comm.out.host.HostOut;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.utils.RefreshUtil;
import io.kamax.hboxc.gui.worker.receiver._HostReceiver;
import io.kamax.hboxc.gui.workers.HostGetWorker;
import io.kamax.tools.AxStrings;
import io.kamax.tools.helper.swing.JTextFieldUtils;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class HostViewer implements _Refreshable, _HostReceiver {

    private String srvId;

    private JLabel hostnameLabel;
    private JTextField hostnameValue;
    private JLabel memPercLabel;
    private JProgressBar memPercValue;
    private JLabel memUsedLabel;
    private JTextField memUsedValue;
    private JLabel memFreeLabel;
    private JTextField memFreeValue;
    private JLabel memTotalLabel;
    private JTextField memTotalValue;

    private JLabel status;
    private JPanel dataPanel;
    private JPanel panel;

    public HostViewer() {
        hostnameLabel = new JLabel("Hostname");
        memPercLabel = new JLabel("Memory Usage");
        memUsedLabel = new JLabel("Memory Used");
        memFreeLabel = new JLabel("Memory Free");
        memTotalLabel = new JLabel("Memory Total");

        hostnameValue = JTextFieldUtils.createNonEditable();
        memPercValue = new JProgressBar(SwingConstants.HORIZONTAL, 0, 10000);
        memUsedValue = JTextFieldUtils.createNonEditable();
        memFreeValue = JTextFieldUtils.createNonEditable();
        memTotalValue = JTextFieldUtils.createNonEditable();

        dataPanel = new JPanel(new MigLayout("ins 0"));
        dataPanel.add(hostnameLabel);
        dataPanel.add(hostnameValue, "growx,pushx,wrap");
        dataPanel.add(memPercLabel);
        dataPanel.add(memPercValue, "growx,pushx,wrap");
        dataPanel.add(memUsedLabel);
        dataPanel.add(memUsedValue, "growx,pushx,wrap");
        dataPanel.add(memFreeLabel);
        dataPanel.add(memFreeValue, "growx,pushx,wrap");
        dataPanel.add(memTotalLabel);
        dataPanel.add(memTotalValue, "growx,pushx,wrap");

        status = new JLabel();
        status.setVisible(false);

        panel = new JPanel(new MigLayout());
        panel.add(status, "growx, pushx, wrap, hidemode 3");
        panel.add(dataPanel, "grow, push, wrap, hidemode 3");

        RefreshUtil.set(panel, this);
        ViewEventManager.register(this);
    }

    public void refresh(String srvId) {
        this.srvId = srvId;
        refresh();
    }

    @Override
    public void refresh() {
        if (srvId != null) {
            HostGetWorker.execute(this, srvId);
        }
    }

    private void clear() {
        hostnameValue.setText(null);
        memPercValue.setValue(0);
        memPercValue.setString(null);
        memPercValue.setStringPainted(false);
        memUsedValue.setText(null);
        memFreeValue.setText(null);
        memTotalValue.setText(null);
    }

    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void loadingStarted() {
        clear();
        dataPanel.setEnabled(false);
        dataPanel.setVisible(false);
        status.setText("Loading...");
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        setDataVisible(isSuccessful);
        if (!isSuccessful) {
            status.setText(t.getMessage());
        }
    }

    @Override
    public void put(HostOut hostOut) {
        Long memUsed = hostOut.getMemorySize() - hostOut.getMemoryAvailable();
        hostnameValue.setText(hostOut.getHostname());
        memPercValue.setStringPainted(true);
        memPercValue.setValue((int) Math.ceil(((1 - (hostOut.getMemoryAvailable().doubleValue() / hostOut.getMemorySize().doubleValue())) * 10000)));

        memPercValue.setString(memUsed + "MB / " + hostOut.getMemorySize() + " MB (" + memPercValue.getString() + ")");
        memUsedValue.setText(memUsed.toString() + " MB");
        memFreeValue.setText(hostOut.getMemoryAvailable().toString() + " MB");
        memTotalValue.setText(hostOut.getMemorySize().toString() + " MB");
    }

    @Handler
    public void putHypervisorConnected(HypervisorConnectedEventOut ev) {
        if (AxStrings.equals(srvId, ev.getServerId())) {
            refresh();
        }
    }

    @Handler
    public void putHypervisorDisconnected(HypervisorDisconnectedEventOut ev) {
        if (AxStrings.equals(srvId, ev.getServerId())) {
            refresh();
        }
    }

    private void setDataVisible(boolean isVisible) {
        status.setVisible(!isVisible);
        status.setEnabled(!isVisible);
        dataPanel.setVisible(isVisible);
        dataPanel.setEnabled(isVisible);
    }

}
