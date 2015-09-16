/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.connector;

import io.kamax.hbox.comm.out.event.hypervisor.HypervisorConnectionStateEventOut;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.event.connector.ConnectorStateChangedEvent;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.host.HostViewer;
import io.kamax.hboxc.gui.hypervisor.HypervisorNetViewer;
import io.kamax.hboxc.gui.module.ModuleListView;
import io.kamax.hboxc.gui.security.user.UserListView;
import io.kamax.hboxc.gui.store.StoreListView;
import io.kamax.hboxc.gui.tasks.ServerTaskListView;
import io.kamax.hboxc.gui.utils.RefreshUtil;
import io.kamax.tool.Validate;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

public class ConnectorDetailedView implements _Refreshable {

    private String conId;
    private JTabbedPane tabs;
    private JLabel loadingLabel;
    private JPanel panel;

    private ConnectorSummaryViewer summaryView;
    private HostViewer hostViewer;
    private HypervisorNetViewer netViewer;
    private ServerTaskListView taskViewer;
    private StoreListView storeView;
    private UserListView userView;
    private ModuleListView modView;

    public ConnectorDetailedView(String conId) {
        Validate.notEmpty(conId);

        this.conId = conId;

        summaryView = new ConnectorSummaryViewer(conId);
        hostViewer = new HostViewer();
        netViewer = new HypervisorNetViewer();
        taskViewer = new ServerTaskListView();
        storeView = new StoreListView();
        userView = new UserListView();
        modView = new ModuleListView();

        tabs = new JTabbedPane();
        tabs.addTab("Summary", IconBuilder.getEntityType(EntityType.Server), summaryView.getComponent());
        tabs.addTab("Host", IconBuilder.getEntityType(EntityType.Server), hostViewer.getComponent());
        tabs.addTab("Network", IconBuilder.getEntityType(EntityType.Network), netViewer.getComponent());
        tabs.addTab("Tasks", IconBuilder.getEntityType(EntityType.Task), taskViewer.getComponent());
        tabs.addTab("Stores", IconBuilder.getEntityType(EntityType.Store), storeView.getComponent());
        tabs.addTab("Users", IconBuilder.getEntityType(EntityType.User), userView.getComponent());
        tabs.addTab("Modules", IconBuilder.getEntityType(EntityType.Module), modView.getComponent());

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false);

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(loadingLabel, "growx,pushx,wrap,hidemode 3");
        panel.add(tabs, "grow,push,wrap");

        RefreshUtil.set(panel, this);
        refresh();
        ViewEventManager.register(this);
    }

    private void update(ConnectorOutput conOut, HypervisorOut hypOut) {
        tabs.setEnabledAt(tabs.indexOfTab("Host"), conOut.isConnected() && hypOut != null);
        tabs.setEnabledAt(tabs.indexOfTab("Network"), conOut.isConnected() && hypOut != null);
        tabs.setEnabledAt(tabs.indexOfTab("Tasks"), conOut.isConnected());
        tabs.setEnabledAt(tabs.indexOfTab("Stores"), conOut.isConnected());
        tabs.setEnabledAt(tabs.indexOfTab("Users"), conOut.isConnected());
        tabs.setEnabledAt(tabs.indexOfTab("Modules"), conOut.isConnected());

        summaryView.update(conOut);
        if (conOut.isConnected()) {
            hostViewer.refresh(conOut.getServerId());
            taskViewer.refresh(conOut.getServerId());
            storeView.show(conOut.getServer());
            userView.show(conOut.getServer());
            modView.show(conOut.getServerId());
            if (hypOut != null) {
                netViewer.refresh(conOut.getServerId(), hypOut.getId());
            } else {
                if (tabs.getSelectedIndex() == tabs.indexOfTab("Host") || tabs.getSelectedIndex() == tabs.indexOfTab("Network")) {
                    tabs.setSelectedIndex(0);
                }
            }
        } else {
            tabs.setSelectedComponent(summaryView.getComponent());
        }
    }

    @Override
    public void refresh() {

        new SwingWorker<Void, Void>() {

            private ConnectorOutput conOut;
            private HypervisorOut hypOut;

            {
                tabs.setIconAt(0, IconBuilder.LoadingIcon);
            }

            @Override
            protected Void doInBackground() throws Exception {
                conOut = Gui.getReader().getConnector(conId);
                if (conOut.isConnected()) {
                    if (Gui.getReader().getServerReader(conOut.getServerId()).isHypervisorConnected()) {
                        hypOut = Gui.getReader().getServerReader(conOut.getServerId()).getHypervisor().getInfo();
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    tabs.setIconAt(0, IconBuilder.getEntityType(EntityType.Server));
                    update(conOut, hypOut);
                } catch (InterruptedException e) {
                    Gui.showError(e);
                } catch (ExecutionException e) {
                    Gui.showError(e.getCause());
                }
            }

        }.execute();
    }

    public JComponent getComponent() {
        return panel;
    }

    @Handler
    private void putConnectorStateEvent(ConnectorStateChangedEvent ev) {
        if (conId.equals(ev.getConnector().getId())) {
            refresh();
        }
    }

    @Handler
    private void putHypervisorStateEvent(HypervisorConnectionStateEventOut ev) {
        if (Gui.getReader().getConnectorForServer(ev.getServerId()).getId() == conId) {
            refresh();
        }
    }

}
