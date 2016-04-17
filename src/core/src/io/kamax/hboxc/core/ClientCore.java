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

package io.kamax.hboxc.core;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.constant.MachineAttribute;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.comm.input.ConnectorInput;
import io.kamax.hboxc.comm.io.factory.ConnectorIoFactory;
import io.kamax.hboxc.comm.io.factory.ConsoleViewerIoFactory;
import io.kamax.hboxc.core.connector._Connector;
import io.kamax.hboxc.core.storage.UserProfileCoreStorage;
import io.kamax.hboxc.core.storage._CoreStorage;
import io.kamax.hboxc.event.CoreStateEvent;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.connector.ConnectorAddedEvent;
import io.kamax.hboxc.event.connector.ConnectorModifiedEvent;
import io.kamax.hboxc.event.connector.ConnectorRemovedEvent;
import io.kamax.hboxc.event.connector.ConnectorStateChangedEvent;
import io.kamax.hboxc.event.consoleviewer.ConsoleViewerAddedEvent;
import io.kamax.hboxc.event.server.ServerDisconnectedEvent;
import io.kamax.hboxc.exception.ConnectorNotFoundException;
import io.kamax.hboxc.exception.ConsoleViewerNotFound;
import io.kamax.hboxc.exception.ConsoleViewerNotFoundForType;
import io.kamax.hboxc.factory.BackendFactory;
import io.kamax.hboxc.factory.ConnectorFactory;
import io.kamax.hboxc.factory.ConsoleViewerFactory;
import io.kamax.hboxc.factory.ModuleManagerFactory;
import io.kamax.hboxc.factory.UpdaterFactory;
import io.kamax.hboxc.module._ModuleManager;
import io.kamax.hboxc.server._Machine;
import io.kamax.hboxc.server._Server;
import io.kamax.hboxc.state.ConnectionState;
import io.kamax.hboxc.state.CoreState;
import io.kamax.hboxc.updater._Updater;
import io.kamax.tool.ProcessRunner;
import io.kamax.tool.logging.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.engio.mbassy.listener.Handler;

public class ClientCore implements _Core {

    private class ConnectorIdGenerator {

        private Integer id = 1;

        public String getId() {
            while (conns.containsKey(id.toString())) {
                id++;
            }
            return id.toString();
        }
    }

    private volatile CoreState state = CoreState.Stopped;

    private _ModuleManager modMgr;
    private _CoreStorage storage;

    private ConnectorIdGenerator connectIdGen = new ConnectorIdGenerator();

    private Map<String, _ConsoleViewer> consoleViewers;
    private Map<String, _Connector> conns;
    private Map<String, _Server> servers;

    private _Updater updater;

    private void setState(CoreState state) {
        if (!this.state.equals(state)) {
            Logger.verbose("Changing Core State to " + state);
            this.state = state;
            EventManager.post(new CoreStateEvent(this.state));
        } else {
            Logger.debug("Ignoring new state, same as old: " + state);
        }
    }

    private void loadViewers() throws HyperboxException {
        consoleViewers.clear();

        Collection<_ConsoleViewer> viewers = new ArrayList<_ConsoleViewer>();
        if (storage.hasConsoleViewers()) {
            viewers.addAll(storage.loadViewers());
        } else {
            viewers.addAll(ConsoleViewerFactory.getDefaults());
        }
        for (_ConsoleViewer viewer : viewers) {
            if (consoleViewers.containsKey(viewer.getId())) {
                throw new HyperboxException("Invalid Console Viewer data: duplicate ID for " + viewer.getId());
            }
            consoleViewers.put(viewer.getId(), viewer);
        }
    }

    private void loadConnectors() throws HyperboxException {
        conns.clear();

        if (storage.hasConnectors()) {
            for (_Connector conn : storage.loadConnectors()) {
                if (conns.containsKey(conn.getId())) {
                    throw new HyperboxException("Invalid Connector data: duplicate ID for " + conn.getId());
                }
                conns.put(conn.getId(), conn);
                EventManager.post(new ConnectorAddedEvent(ConnectorIoFactory.get(conn)));
            }
        }
    }

    @Override
    public void init() throws HyperboxException {
        EventManager.get().register(this);

        modMgr = ModuleManagerFactory.get();

        storage = new UserProfileCoreStorage();
        storage.init();

        updater = UpdaterFactory.get();
    }

    @Override
    public void start() throws HyperboxException {
        setState(CoreState.Starting);

        consoleViewers = new HashMap<String, _ConsoleViewer>();
        conns = new HashMap<String, _Connector>();
        servers = new HashMap<String, _Server>();

        try {
            modMgr.start();
            storage.start();

            loadViewers();
            loadConnectors();

            setState(CoreState.Started);
        } catch (Throwable e) {
            stop();
            throw new HyperboxException("Error during core start sequence", e);
        }
    }

    @Override
    public void stop() {
        if (getCoreState().equals(CoreState.Started) || getCoreState().equals(CoreState.Starting)) {
            setState(CoreState.Stopping);

            for (String connId : conns.keySet()) {
                try {
                    disconnect(connId);
                } catch (Throwable t) {
                    Logger.warning("Failed to disconnect servers during client shutdown: " + t.getMessage());
                }
            }

            if (storage != null) {
                storage.storeViewers(consoleViewers.values());
                storage.storeConnectors(conns.values());

                storage.stop();
                storage = null;
            }

            if (updater != null) {
                updater.stop();
                updater = null;
            }

            setState(CoreState.Stopped);
        }
    }

    @Override
    public void destroy() {
        if (storage != null) {
            storage.destroy();
        }
    }

    @Override
    public CoreState getCoreState() {
        return state;
    }

    @Override
    public _ConsoleViewer getConsoleViewer(String id) {
        if (!consoleViewers.containsKey(id)) {
            throw new ConsoleViewerNotFound();
        }

        return consoleViewers.get(id);
    }

    @Override
    public List<_ConsoleViewer> listConsoleViewer() {
        List<_ConsoleViewer> listOut = new ArrayList<_ConsoleViewer>();
        for (_ConsoleViewer conViewer : consoleViewers.values()) {
            listOut.add(conViewer);
        }
        return listOut;
    }

    @Override
    public List<_ConsoleViewer> listConsoleViewer(String hypervisorTypeId) {
        List<_ConsoleViewer> listOut = new ArrayList<_ConsoleViewer>();
        for (_ConsoleViewer conViewer : consoleViewers.values()) {
            if (conViewer.getHypervisorId().equalsIgnoreCase(hypervisorTypeId)) {
                listOut.add(conViewer);
            }
        }
        return listOut;
    }

    @Override
    public _ConsoleViewer addConsoleViewer(String hypervisorId, String moduleId, String viewerPath, String viewerArgs) {
        String id = hypervisorId + moduleId;
        if (consoleViewers.containsKey(id)) {
            throw new HyperboxException("Console viewer already exist for this Hypervisor and Module");
        }

        _ConsoleViewer conView = ConsoleViewerFactory.get(hypervisorId, moduleId, viewerPath, viewerArgs);
        conView.save();

        storage.storeViewers(consoleViewers.values());
        consoleViewers.put(conView.getId(), conView);

        EventManager.post(new ConsoleViewerAddedEvent(ConsoleViewerIoFactory.getOut(conView)));

        return conView;
    }

    @Override
    public void removeConsoleViewer(String id) {
        _ConsoleViewer viewer = getConsoleViewer(id);
        viewer.remove();
        consoleViewers.remove(id);

        EventManager.post(new ConsoleViewerAddedEvent(ConsoleViewerIoFactory.getOut(viewer)));
    }

    @Override
    public _ConsoleViewer findConsoleViewer(String hypervisorId, String moduleId) {
        for (_ConsoleViewer conViewer : consoleViewers.values()) {
            if (hypervisorId.matches(conViewer.getHypervisorId()) && moduleId.matches(conViewer.getModuleId())) {
                return conViewer;
            }
        }

        throw new ConsoleViewerNotFoundForType(hypervisorId, moduleId);
    }

    @Override
    public _Server getServer(String id) {
        if (!servers.containsKey(id)) {
            throw new HyperboxException("No Server for ID " + id);
        }
        return servers.get(id);
    }

    @Override
    public List<_Connector> listConnector() {
        return new ArrayList<_Connector>(conns.values());
    }

    @Override
    public _Connector getConnector(String id) {
        if (!conns.containsKey(id)) {
            throw new ConnectorNotFoundException("No Connector for ID " + id);
        }
        return conns.get(id);
    }

    @Override
    public _Backend getBackend(String id) {
        return BackendFactory.get(id);
    }

    @Override
    public List<String> listBackends() {
        return BackendFactory.list();
    }

    @Override
    public _Connector addConnector(ConnectorInput conIn, UserIn usrIn) {
        _Connector conn = ConnectorFactory.get(connectIdGen.getId(), conIn.getLabel(), conIn.getAddress(), usrIn.getUsername(), conIn.getBackendId());
        storage.storeConnectorCredentials(conn.getId(), usrIn);
        conns.put(conn.getId(), conn);
        storage.storeConnectors(new ArrayList<_Connector>(conns.values()));
        EventManager.post(new ConnectorAddedEvent(ConnectorIoFactory.get(conn)));
        return conn;
    }

    @Override
    public _Connector modifyConnector(ConnectorInput conIn, UserIn usrIn) {
        _Connector conn = getConnector(conIn.getId());
        ConnectorFactory.update(conn, conIn);
        if (usrIn != null) {
            conn.setUsername(usrIn.getUsername());
            storage.storeConnectorCredentials(conn.getId(), usrIn);
        }
        storage.storeConnectors(conns.values());
        EventManager.post(new ConnectorModifiedEvent(ConnectorIoFactory.get(conn)));
        return conn;
    }

    @Override
    public _Connector connect(String id) {
        _Connector conn = getConnector(id);
        UserIn usrIn = storage.loadConnectorCredentials(conn.getId());
        _Server srv = conn.connect(usrIn);
        servers.put(srv.getId(), srv);
        return conn;
    }

    @Override
    public void disconnect(String id) {
        _Connector conn = getConnector(id);
        conn.disconnect();
    }

    @Override
    public void removeConnector(String id) {
        disconnect(id);

        _Connector conn = conns.remove(id);
        storage.storeConnectors(conns.values());
        storage.removeConnectorCredentials(id);
        EventManager.post(new ConnectorRemovedEvent(ConnectorIoFactory.get(conn)));
    }

    @Handler
    private void putConnectorConnected(ConnectorStateChangedEvent ev) {
        if (ev.getState().equals(ConnectionState.Connected)) {
            servers.put(ev.getConnector().getServerId(), getConnector(ev.getConnector().getId()).getServer());
        }
    }

    @Handler
    protected void putServerDisconnected(ServerDisconnectedEvent ev) {
        servers.remove(ev.getServer().getId());
    }

    @Handler
    private void putStarted(CoreStateEvent ev) {
        if (CoreState.Started.equals(ev.getState())) {
            updater.start();
        }
    }

    @Override
    public _Connector getConnectorForServer(String serverId) {
        for (_Connector con : conns.values()) {
            if (con.isConnected() && con.getServer().getId().equals(serverId)) {
                return con;
            }
        }
        throw new HyperboxException("No connected server was found under ID " + serverId);
    }

    @Override
    public void launchConsoleViewer(_Machine machine) {
        launchConsoleViewer(machine.getServer().getId(), machine.getId());
    }

    @Override
    public void launchConsoleViewer(String serverId, String machineId) {
        _Server srv = getServer(serverId);
        MachineOut m = srv.getMachine(machineId);
        _ConsoleViewer cView = findConsoleViewer(getServer(serverId).getHypervisor().getType(), m.getSetting(MachineAttribute.VrdeModule).getString());
        String address = m.getSetting(MachineAttribute.VrdeAddress).getString();
        if ((address == null) || address.isEmpty()) {
            address = getConnectorForServer(srv.getId()).getAddress();
        }
        String port = m.getSetting(MachineAttribute.VrdePort).getString();

        String arg = cView.getArgs();
        arg = arg.replace("%SA%", address);
        arg = arg.replace("%SP%", port);
        arg = arg.replace("%VM_NAME%", m.getName());

        ArrayList<String> args = new ArrayList<>();
        args.add(cView.getViewerPath());

        String[] argsRaw = arg.split(" ");
        for (int i = 0; i < argsRaw.length; i++) {
            argsRaw[i] = argsRaw[i].replace("%SA%", address);
            argsRaw[i] = argsRaw[i].replace("%SP%", port);
            argsRaw[i] = argsRaw[i].replace("%VM_NAME%", m.getName());
            args.add(argsRaw[i]);
        }

        Logger.info("Starting Console viewer " + cView.getViewerPath() + " for machine " + m.getUuid() + " on server " + srv.getId()
                + " with arguments: " + arg);
        try {
            ProcessRunner.runHeadless(new ProcessBuilder(args).start());
        } catch (Throwable t) {
            throw new HyperboxException("Couldn't launch Console Viewer", t);
        }

    }

    @Override
    public _Updater getUpdater() {
        return updater;
    }

}
