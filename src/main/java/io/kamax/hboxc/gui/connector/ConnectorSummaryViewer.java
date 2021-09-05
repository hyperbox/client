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

package io.kamax.hboxc.gui.connector;

import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.event.connector.ConnectorEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.server.ServerViewer;
import io.kamax.hboxc.gui.worker.receiver._ConnectorReceiver;
import io.kamax.hboxc.gui.workers.ConnectorGetWorker;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ConnectorSummaryViewer implements _Refreshable, _ConnectorReceiver {

    private String conId;

    private ServerViewer srvView;

    private JLabel labelLabel;
    private JTextField labelValue;
    private JLabel addressLabel;
    private JTextField addressValue;
    private JLabel backendLabel;
    private JTextField backendValue;
    private JLabel stateLabel;
    private JTextField stateValue;

    private JPanel conPanel;
    private JPanel panel;

    public ConnectorSummaryViewer(String conId) {
        this.conId = conId;
        labelLabel = new JLabel("Label");
        addressLabel = new JLabel("Address");
        backendLabel = new JLabel("Backend");
        stateLabel = new JLabel("State");

        labelValue = new JTextField();
        labelValue.setEditable(false);
        addressValue = new JTextField();
        addressValue.setEditable(false);
        backendValue = new JTextField();
        backendValue.setEditable(false);
        stateValue = new JTextField();
        stateValue.setEditable(false);

        srvView = new ServerViewer();

        conPanel = new JPanel(new MigLayout());
        conPanel.setBorder(BorderFactory.createTitledBorder("Connector"));
        conPanel.add(labelLabel);
        conPanel.add(labelValue, "growx,pushx,wrap");
        conPanel.add(addressLabel);
        conPanel.add(addressValue, "growx,pushx,wrap");
        conPanel.add(backendLabel);
        conPanel.add(backendValue, "growx,pushx,wrap");
        conPanel.add(stateLabel);
        conPanel.add(stateValue, "growx,pushx,wrap");

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(conPanel, "growx, pushx, wrap");
        panel.add(srvView.getComponent(), "growx, pushx, wrap");
        srvView.getComponent().setVisible(false);

        ViewEventManager.register(this);
    }

    @Override
    public void refresh() {
        ConnectorGetWorker.execute(this, conId);
    }

    protected void update(ConnectorOutput conOut) {
        labelValue.setText(conOut.getLabel());
        addressValue.setText(conOut.getAddress());
        backendValue.setText(conOut.getBackendId());
        stateValue.setText(conOut.getState().toString());
        if (conOut.isConnected()) {
            srvView.getComponent().setVisible(true);
            srvView.show(conOut.getServer());
        } else {
            srvView.getComponent().setVisible(false);
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    @Handler
    protected void putConnectorEvent(ConnectorEvent ev) {
        if (ev.getConnector().getId().equals(conId)) {
            refresh();
        }
    }

    @Override
    public void loadingStarted() {
        // stub
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        if (!isSuccessful) {
            labelValue.setText(t.getMessage());
        }
    }

    @Override
    public void put(ConnectorOutput conOut) {
        update(conOut);
    }

}
