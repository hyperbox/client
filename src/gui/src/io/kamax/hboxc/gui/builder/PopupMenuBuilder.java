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

package io.kamax.hboxc.gui.builder;

import io.kamax.hbox.comm.out.ModuleOut;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.StoreOut;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.gui.action.connector.ConnectorConnectAction;
import io.kamax.hboxc.gui.action.connector.ConnectorDisconnectAction;
import io.kamax.hboxc.gui.action.connector.ConnectorModifyAction;
import io.kamax.hboxc.gui.action.connector.ConnectorRemoveAction;
import io.kamax.hboxc.gui.action.hypervisor.HypervisorConfigureAction;
import io.kamax.hboxc.gui.action.hypervisor.HypervisorConnectAction;
import io.kamax.hboxc.gui.action.hypervisor.HypervisorDisconnectAction;
import io.kamax.hboxc.gui.action.machine.MachineAcpiPowerAction;
import io.kamax.hboxc.gui.action.machine.MachineCreateAction;
import io.kamax.hboxc.gui.action.machine.MachineDeleteAction;
import io.kamax.hboxc.gui.action.machine.MachineEditAction;
import io.kamax.hboxc.gui.action.machine.MachinePauseAction;
import io.kamax.hboxc.gui.action.machine.MachineRegisterAction;
import io.kamax.hboxc.gui.action.machine.MachineResetAction;
import io.kamax.hboxc.gui.action.machine.MachineResumeAction;
import io.kamax.hboxc.gui.action.machine.MachineSaveStateAction;
import io.kamax.hboxc.gui.action.machine.MachineStartAction;
import io.kamax.hboxc.gui.action.machine.MachineStopAction;
import io.kamax.hboxc.gui.action.machine.MachineUnregisterAction;
import io.kamax.hboxc.gui.action.module.ModuleLoadAction;
import io.kamax.hboxc.gui.action.server.ServerConfigureAction;
import io.kamax.hboxc.gui.action.server.ServerShutdownAction;
import io.kamax.hboxc.gui.action.storage.HypervisorToolsMediumAttachAction;
import io.kamax.hboxc.gui.action.storage.MediumAttachAction;
import io.kamax.hboxc.gui.action.storage.MediumDettachAction;
import io.kamax.hboxc.gui.action.store.StoreBrowseAction;
import io.kamax.hboxc.gui.action.store.StoreDeleteAction;
import io.kamax.hboxc.gui.action.store.StoreUnregisterAction;
import io.kamax.hboxc.gui.connector._ConnectorSelector;
import io.kamax.hboxc.gui.module._ModuleSelector;
import io.kamax.hboxc.gui.server._ServerSelector;
import io.kamax.hboxc.gui.store._StoreSelector;
import io.kamax.hboxc.gui.vm._MachineSelector;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class PopupMenuBuilder {

    public static JPopupMenu get(String serverId, StorageDeviceAttachmentOut sdaOut) {
        JPopupMenu stoMenuActions = new JPopupMenu();
        stoMenuActions.add(new JMenuItem(new HypervisorToolsMediumAttachAction(serverId, sdaOut)));
        stoMenuActions.add(new JMenuItem(new MediumAttachAction(serverId, sdaOut)));
        stoMenuActions.add(new JMenuItem(new MediumDettachAction(serverId, sdaOut, sdaOut.hasMediumInserted())));
        return stoMenuActions;
    }

    public static JPopupMenu get(_MachineSelector select, MachineOut mOut) {

        JPopupMenu machineMenu = new JPopupMenu();
        machineMenu.add(new JMenuItem(new MachineStartAction(select)));
        machineMenu.add(new JMenuItem(new MachineStopAction(select)));
        machineMenu.add(new JMenuItem(new MachineResetAction(select)));
        machineMenu.add(new JMenuItem(new MachineAcpiPowerAction(select)));
        machineMenu.add(new JSeparator());
        machineMenu.add(new JMenuItem(new MachineSaveStateAction(select)));
        machineMenu.add(new JMenuItem(new MachinePauseAction(select)));
        machineMenu.add(new JMenuItem(new MachineResumeAction(select)));
        machineMenu.add(new JSeparator());
        machineMenu.add(new JMenuItem(new MachineEditAction(select)));
        machineMenu.add(new JMenuItem(new MachineUnregisterAction(select)));
        machineMenu.add(new JMenuItem(new MachineDeleteAction(select)));

        return machineMenu;
    }

    public static JPopupMenu get(ServerOut srvOut) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem("Not implemented"));
        return menu;
    }

    public static JPopupMenu get(_ConnectorSelector conSelect, _ServerSelector srvSelect, ConnectorOutput conOut) {
        JPopupMenu conPopupMenu = new JPopupMenu();
        if (conOut.isConnected()) {
            if (conOut.getServer().isHypervisorConnected()) {
                JMenu vmActions = new JMenu("Machine");
                vmActions.add(new JMenuItem(new MachineCreateAction(srvSelect)));
                vmActions.add(new JMenuItem(new MachineRegisterAction(srvSelect)));
                conPopupMenu.add(vmActions);
            }
            JMenu hypActions = new JMenu("Hypervisor");
            if (conOut.getServer().isHypervisorConnected()) {
                hypActions.add(new JMenuItem(new HypervisorConfigureAction(srvSelect)));
                hypActions.add(new JMenuItem(new HypervisorDisconnectAction(srvSelect)));
            } else {
                hypActions.add(new JMenuItem(new HypervisorConnectAction(srvSelect)));
            }
            conPopupMenu.add(hypActions);

            JMenu srvMenu = new JMenu("Server");
            srvMenu.add(new JMenuItem(new ServerConfigureAction(srvSelect)));
            srvMenu.add(new JMenuItem(new ServerShutdownAction(srvSelect)));
            conPopupMenu.add(srvMenu);

            conPopupMenu.add(new JSeparator());
            conPopupMenu.add(new JMenuItem(new ConnectorDisconnectAction(conSelect)));
        } else {
            conPopupMenu.add(new JMenuItem(new ConnectorConnectAction(conSelect)));
            conPopupMenu.add(new JMenuItem(new ConnectorModifyAction(conSelect, !conOut.isConnected())));
            conPopupMenu.add(new JMenuItem(new ConnectorRemoveAction(conSelect)));
        }
        return conPopupMenu;
    }

    public static JPopupMenu get(_StoreSelector stoSelect, StoreOut stoOut) {
        Action browse = new StoreBrowseAction(stoSelect);
        Action unregister = new StoreUnregisterAction(stoSelect);
        Action delete = new StoreDeleteAction(stoSelect);

        JPopupMenu actions = new JPopupMenu();
        actions.add(new JMenuItem(browse));
        actions.add(new JSeparator(SwingConstants.HORIZONTAL));
        actions.add(new JMenuItem(unregister));
        actions.add(new JMenuItem(delete));
        return actions;
    }

    public static JPopupMenu get(_ModuleSelector modSelect, ModuleOut modOut) {
        if (modOut.isLoaded()) {
            return null;
        } else {
            JPopupMenu actions = new JPopupMenu();
            actions.add(new JMenuItem(new ModuleLoadAction(modSelect)));
            return actions;
        }
    }

}
