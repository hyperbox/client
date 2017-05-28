/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
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

package io.kamax.hboxc.gui.vm.view;

import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hboxc.event.machine.MachineDataChangedEvent;
import io.kamax.hboxc.event.machine.MachineRemovedEvent;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.snapshot.SnapshotManagementView;
import io.kamax.hboxc.gui.worker.receiver._MachineReceiver;
import io.kamax.hboxc.gui.workers.MachineGetWorker;
import io.kamax.hboxc.gui.workers._WorkerTracker;
import io.kamax.tools.AxStrings;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public final class VmDetailedView implements _MachineReceiver, _Refreshable {

    private MachineOut mOut;

    private VmSummaryView summaryTab;
    private SnapshotManagementView snapTab;
    private JTabbedPane tabs;
    private JLabel loadingLabel;
    private JPanel panel;
    private JLabel errorLabel;

    public VmDetailedView(MachineOut mOut) {
        this.mOut = mOut;

        summaryTab = new VmSummaryView();
        snapTab = new SnapshotManagementView();

        tabs = new JTabbedPane();
        tabs.addTab("Summary", summaryTab.getComponent());
        tabs.addTab("Snapshots", snapTab.getComponent());

        loadingLabel = new JLabel("Loading...");
        errorLabel = new JLabel();

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(loadingLabel, "growx, pushx, wrap, hidemode 3");
        panel.add(errorLabel, "growx, pushx, wrap, hidemode 3");
        panel.add(tabs, "grow, push, wrap, hidemode 3");

        ViewEventManager.register(this);

        refresh();
    }

    private void update() {
        if (mOut.isAvailable()) {
            summaryTab.show(mOut, true);
            tabs.setEnabledAt(tabs.indexOfComponent(summaryTab.getComponent()), true);
            snapTab.show(mOut);
            tabs.setEnabledAt(tabs.indexOfComponent(snapTab.getComponent()), true);
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    @Handler
    public void getMachineUpdate(MachineDataChangedEvent ev) {
        if (AxStrings.equals(ev.getUuid(), mOut.getUuid())) {
            put(ev.getMachine());
        }
    }

    @Handler
    public void getMachineRemove(MachineRemovedEvent ev) {
        if (AxStrings.equals(ev.getUuid(), mOut.getUuid())) {
            clear();
        }
    }

    private void clear() {
        errorLabel.setVisible(false);
        tabs.setVisible(false);
        summaryTab.clear();
    }

    @Override
    public void loadingStarted() {
        clear();
        loadingLabel.setVisible(true);
        if (tabs.indexOfComponent(summaryTab.getComponent()) > -1) {
            tabs.setEnabledAt(tabs.indexOfComponent(summaryTab.getComponent()), false);
        }
        if (tabs.indexOfComponent(snapTab.getComponent()) > -1) {
            tabs.setEnabledAt(tabs.indexOfComponent(snapTab.getComponent()), false);
        }
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable message) {
        loadingLabel.setVisible(false);
        tabs.setEnabled(isSuccessful);
        if (!isSuccessful) {
            errorLabel.setText("Unable to retrieve VM information: " + message.getMessage());
            errorLabel.setVisible(true);
        } else {
            tabs.setVisible(mOut.isAvailable());
        }
    }

    @Override
    public void put(MachineOut mOut) {
        this.mOut = mOut;
        update();
    }

    @Override
    public void refresh() {
        MachineGetWorker.execute(_WorkerTracker.EMPTY, this, mOut);
    }

}
