/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.out.network.NetAdaptorOut;
import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui._Refreshable;
import io.kamax.hboxc.gui.action.network.NetAdaptorAddAction;
import io.kamax.hboxc.gui.action.network.NetAdaptorEditAction;
import io.kamax.hboxc.gui.action.network.NetAdaptorRemoveAction;
import io.kamax.hboxc.gui.worker.receiver._NetAdaptorListReceiver;
import io.kamax.hboxc.gui.workers.NetAdaptorListWorker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class HypervisorNetModeViewer implements _Refreshable, _NetAdaptorListReceiver {

    private String srvId;
    private String hypId;
    private NetModeOut mode;

    private JPanel panel = new JPanel(new MigLayout());

    public HypervisorNetModeViewer(String srvId, String hypId, NetModeOut mode) {
        this.srvId = srvId;
        this.hypId = hypId;
        this.mode = mode;
        ViewEventManager.register(this);
        refresh();
    }

    @Override
    public void refresh() {
        NetAdaptorListWorker.execute(this, srvId, mode.getId());
    }

    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void loadingStarted() {
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        if (!isSuccessful) {
            panel.add(new JLabel(t.getMessage()), "wrap");
        } else {
            if (panel.getComponents().length == 0) {
                panel.add(new JLabel("No adaptor"), "wrap");
            }
            if (mode.canAddAdaptor()) {
                panel.add(new JButton(new NetAdaptorAddAction(srvId, hypId, mode.getId())), "wrap");
            }
        }
        // TODO find out why this is needed
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void add(List<NetAdaptorOut> adaptOutList) {
        for (NetAdaptorOut adapt : adaptOutList) {
            panel.add(new JLabel(adapt.getLabel()), "growx, pushx");
            if (mode.canRemoveAdaptor()) {
                panel.add(new JButton(new NetAdaptorEditAction(srvId, hypId, mode.getId(), adapt.getId())));
                panel.add(new JButton(new NetAdaptorRemoveAction(srvId, hypId, mode.getId(), adapt.getId())), "wrap");
            } else {
                panel.add(new JLabel(), "span 2, wrap");
            }
        }
    }

}
