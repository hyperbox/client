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

package io.kamax.hboxc.core.server;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.HyperboxAPI;
import io.kamax.hbox.comm.*;
import io.kamax.hbox.comm.in.*;
import io.kamax.hbox.comm.out.*;
import io.kamax.hbox.comm.out.event.hypervisor.HypervisorEventOut;
import io.kamax.hbox.comm.out.event.server.ServerPropertyChangedEventOut;
import io.kamax.hbox.comm.out.event.server.ServerShutdownEventOut;
import io.kamax.hbox.comm.out.host.HostOut;
import io.kamax.hbox.comm.out.hypervisor.*;
import io.kamax.hbox.comm.out.network.NetworkAttachModeOut;
import io.kamax.hbox.comm.out.network.NetworkAttachNameOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceTypeOut;
import io.kamax.hbox.comm.out.security.PermissionOut;
import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.comm.out.storage.StorageControllerSubTypeOut;
import io.kamax.hbox.comm.out.storage.StorageControllerTypeOut;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.comm.io.factory.ServerIoFactory;
import io.kamax.hboxc.comm.utils.Transaction;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.backend.BackendConnectionStateEvent;
import io.kamax.hboxc.event.server.ServerConnectedEvent;
import io.kamax.hboxc.event.server.ServerDisconnectedEvent;
import io.kamax.hboxc.event.server.ServerModifiedEvent;
import io.kamax.hboxc.factory.BackendFactory;
import io.kamax.hboxc.server._GuestReader;
import io.kamax.hboxc.server._Hypervisor;
import io.kamax.hboxc.server._Machine;
import io.kamax.hboxc.server._Server;
import io.kamax.hboxc.server.task._Task;
import io.kamax.hboxc.state.ConnectionState;
import io.kamax.tools.AxBooleans;
import io.kamax.tools.logging.Logger;
import net.engio.mbassy.listener.Handler;

import java.util.*;

public class Server implements _Server, _AnswerReceiver {

    private Map<String, _AnswerReceiver> ansRecv;
    private _Backend backend;

    private String id;
    private String name;
    private String type;
    private String version;
    private String protocolVersion;
    private String logLevel;
    private boolean isHypConnected = false;

    private ConnectionState state = ConnectionState.Disconnected;
    private _Hypervisor hypReader;

    private void setState(ConnectionState state) {
        if (this.state.equals(state)) {
            Logger.debug("Ignoring setState(" + state + ") - same as current");
        }

        this.state = state;
        Logger.info("Changed Server #" + id + " object state to " + state);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public ConnectionState getState() {
        return state;
    }

    @Override
    public Transaction sendRequest(Request req) {
        return sendRequest(req, RequestProcessType.WaitForRequest);
    }

    @Override
    public Transaction sendRequest(Request req, RequestProcessType type) {
        if (type.equals(RequestProcessType.NoWait)) {
            backend.putRequest(req);
        }
        Transaction t = getTransaction(req);

        if (type.equals(RequestProcessType.WaitForRequest)) {
            if (!t.sendAndWait()) {
                throw new HyperboxException(t.getError());
            }
        }
        if (type.equals(RequestProcessType.WaitForTask)) {
            if (!t.sendAndWaitForTask()) {
                throw new HyperboxException(t.getError());
            }
        }

        return t;
    }

    @Override
    public MachineOut createMachine(MachineIn mIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public MachineOut registerMachine(MachineIn mIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public MachineOut modifyMachine(MachineIn mIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public MachineOut unregisterMachine(MachineIn mIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public MachineOut deleteMachine(MachineIn mIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public void startMachine(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachinePowerOn, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to start the VM : " + trans.getError());
        }
    }

    @Override
    public void stopMachine(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachinePowerOff, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to stop the VM : " + trans.getError());
        }
    }

    @Override
    public void acpiPowerMachine(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachineAcpiPowerButton, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to send ACPI Power signal to the VM : " + trans.getError());
        }
    }

    @Override
    public UserOut addUser(UserIn uIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public UserOut modifyUser(UserIn uIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public UserOut deleteUser(UserIn uIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public SessionOut closeSession(SessionIn sIn) {
        // TODO To implement
        throw new HyperboxException("Not implemented");
    }

    @Override
    public MachineOut getMachine(MachineIn mIn) {
        Transaction t = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachineGet, mIn));
        if (!t.sendAndWait()) {
            throw new HyperboxException(t.getError());
        }

        MachineOut mOut = t.extractItem(MachineOut.class);
        return mOut;
    }

    @Override
    public MachineOut getMachine(String machineId) {
        return getMachine(new MachineIn(machineId));
    }

    @Override
    public MediumOut getMedium(MediumIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MediumGet, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve medium : " + trans.getError());
        }

        return trans.extractItem(MediumOut.class);
    }

    @Override
    public SessionOut getSession(SessionIn sIn) {
        // TODO Auto-generated method stub
        throw new HyperboxException("Not implemented");
    }

    @Override
    public StorageControllerSubTypeOut getStorageControllerSubType(StorageControllerSubTypeIn scstIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerSubTypeGet, scstIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve Storage Controller SubType [" + scstIn.getId() + "] : " + trans.getError());
        }

        StorageControllerSubTypeOut scstOut = trans.extractItem(StorageControllerSubTypeOut.class);
        return scstOut;
    }

    @Override
    public StorageControllerTypeOut getStorageControllerType(StorageControllerTypeIn sctIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerTypeGet, sctIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve Storage Controller Type [" + sctIn.getId() + "] : " + trans.getError());
        }

        StorageControllerTypeOut sctOut = trans.extractItem(StorageControllerTypeOut.class);
        return sctOut;
    }

    @Override
    public StoreOut getStore(StoreIn sIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.StoreGet, sIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Stores : " + trans.getError());
        }
        return trans.extractItem(StoreOut.class);
    }

    @Override
    public StoreItemOut getStoreItem(StoreItemIn siIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.StoreItemGet, siIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Stores : " + trans.getError());
        }

        return trans.getBody().poll().get(StoreItemOut.class);
    }

    @Override
    public Transaction getTransaction(Request req) {
        if ((backend == null) || !backend.isConnected() || (getId() == null)) {
            throw new HyperboxException("Not connected to the server");
        }

        if (!req.has(ServerIn.class)) {
            req.set(new ServerIn(id));
        }
        Transaction t = new Transaction(backend, req);
        ansRecv.put(req.getExchangeId(), t);
        return t;
    }

    @Override
    public UserOut getUser(UserIn uIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.UserGet, uIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve user : " + trans.getError());
        }

        return trans.extractItem(UserOut.class);
    }

    @Override
    public List<StorageDeviceAttachmentOut> listAttachments(StorageControllerIn scIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerMediumAttachmentList, scIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of attachments : " + trans.getError());
        }

        List<StorageDeviceAttachmentOut> attachList = trans.extractItems(StorageDeviceAttachmentOut.class);
        return attachList;
    }

    @Override
    public List<String> listKeyboardMode(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.KeyboardModeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Keyboard Types : " + trans.getError());
        }

        List<String> keyboardMode = new ArrayList<String>();
        for (Answer ans : trans.getBody()) {
            keyboardMode.add((String) ans.get(CommObjets.KeyboardMode));
        }
        return keyboardMode;
    }

    @Override
    public List<MachineOut> listMachines() {
        Transaction t = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachineList));
        if (!t.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of machines: " + t.getError());
        }

        List<MachineOut> objOutList = t.extractItems(MachineOut.class);
        return objOutList;
    }

    @Override
    public List<MediumOut> listMediums() {
        Transaction t = getTransaction(new Request(Command.VBOX, HypervisorTasks.MediumList));
        if (!t.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of mediums: " + t.getError());
        }

        List<MediumOut> objOutList = t.extractItems(MediumOut.class);
        return objOutList;
    }

    @Override
    public List<String> listMouseMode(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MouseModeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Mouse Types : " + trans.getError());
        }

        List<String> keyboardMode = new ArrayList<String>();
        for (Answer ans : trans.getBody()) {
            keyboardMode.add((String) ans.get(CommObjets.MouseMode));
        }
        return keyboardMode;
    }

    @Override
    public List<OsTypeOut> listOsType() {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.OsTypeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of OS : " + trans.getError());
        }

        List<OsTypeOut> osOutList = new ArrayList<OsTypeOut>();
        for (Answer ans : trans.getBody()) {
            osOutList.add(ans.get(OsTypeOut.class));
        }
        return osOutList;
    }

    @Override
    public List<OsTypeOut> listOsType(MachineIn mIn) {
        return listOsType();
    }

    @Override
    public List<SessionOut> listSessions() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.SessionList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of sessions : " + trans.getError());
        }

        List<SessionOut> objOutList = trans.extractItems(SessionOut.class);
        return objOutList;
    }

    @Override
    public List<StorageControllerSubTypeOut> listStorageControllerSubType(StorageControllerTypeIn sctIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerSubTypeList, sctIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Storage Controller Sub types : " + trans.getError());
        }

        List<StorageControllerSubTypeOut> objOutList = trans.extractItems(StorageControllerSubTypeOut.class);
        return objOutList;
    }

    @Override
    public List<StorageControllerTypeOut> listStorageControllerType() {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerTypeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Storage Controller Types : " + trans.getError());
        }

        List<StorageControllerTypeOut> objOutList = trans.extractItems(StorageControllerTypeOut.class);
        return objOutList;
    }

    @Override
    public List<StoreItemOut> listStoreItems(StoreIn sIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.StoreItemList, sIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Stores Items : " + trans.getError());
        }

        List<StoreItemOut> objOutList = trans.extractItems(StoreItemOut.class);
        return objOutList;
    }

    @Override
    public List<StoreItemOut> listStoreItems(StoreIn sIn, StoreItemIn siIn) {
        Request req = new Request(Command.HBOX, HyperboxTasks.StoreItemList);
        req.set(sIn);
        req.set(siIn);
        Transaction trans = getTransaction(req);
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Stores Items : " + trans.getError());
        }

        List<StoreItemOut> objOutList = trans.extractItems(StoreItemOut.class);
        return objOutList;
    }

    @Override
    public List<StoreOut> listStores() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.StoreList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Stores : " + trans.getError());
        }

        List<StoreOut> objOutList = trans.extractItems(StoreOut.class);
        return objOutList;
    }

    @Override
    public List<TaskOut> listTasks() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.TaskList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of tasks : " + trans.getError());
        }

        List<TaskOut> objOutList = trans.extractItems(TaskOut.class);
        return objOutList;
    }

    @Override
    public List<UserOut> listUsers() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.UserList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of users : " + trans.getError());
        }

        List<UserOut> objOutList = trans.extractItems(UserOut.class);
        return objOutList;
    }

    @Override
    public List<NetworkInterfaceOut> listNetworkInterfaces(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.NetworkInterfaceList, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Network interfaces for [" + mIn + "] : " + trans.getError());
        }

        List<NetworkInterfaceOut> objOutList = trans.extractItems(NetworkInterfaceOut.class);
        return objOutList;
    }

    @Override
    public List<NetworkAttachModeOut> listNetworkAttachModes() {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.NetworkAttachModeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Network Attach modes : " + trans.getError());
        }

        List<NetworkAttachModeOut> objOutList = trans.extractItems(NetworkAttachModeOut.class);
        return objOutList;
    }

    @Override
    public List<NetworkAttachNameOut> listNetworkAttachNames(NetworkAttachModeIn namIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.NetworkAttachNameList, namIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Network Attach modes : " + trans.getError());
        }

        List<NetworkAttachNameOut> objOutList = trans.extractItems(NetworkAttachNameOut.class);
        return objOutList;
    }

    @Override
    public List<NetworkInterfaceTypeOut> listNetworkInterfaceTypes() {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.NetworkAdapterTypeList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Network Interface types : " + trans.getError());
        }

        List<NetworkInterfaceTypeOut> objOutList = trans.extractItems(NetworkInterfaceTypeOut.class);
        return objOutList;
    }

    @Override
    public TaskOut getTask(TaskIn tIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.TaskGet, tIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to get Task: " + trans.getError());
        }
        TaskOut tOut = trans.extractItem(TaskOut.class);
        return tOut;
    }

    @Override
    public SnapshotOut getRootSnapshot(String vmId) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.SnapshotGetRoot, new MachineIn(vmId)));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve snapshot information: " + trans.getError());
        }

        SnapshotOut snapOut = trans.extractItem(SnapshotOut.class);
        return snapOut;
    }

    @Override
    public SnapshotOut getSnapshot(MachineIn mIn, SnapshotIn snapIn) {
        Request req = new Request(Command.VBOX, HypervisorTasks.SnapshotGet);
        req.set(mIn);
        req.set(snapIn);
        Transaction trans = getTransaction(req);
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve snapshot information: " + trans.getError());
        }

        SnapshotOut snapOut = trans.extractItem(SnapshotOut.class);
        return snapOut;
    }

    @Override
    public SnapshotOut getCurrentSnapshot(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.SnapshotGetCurrent, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve snapshot information: " + trans.getError());
        }

        SnapshotOut snapOut = trans.extractItem(SnapshotOut.class);
        return snapOut;
    }

    @Override
    public _Hypervisor getHypervisor() {
        return hypReader;
    }

    @Override
    public ScreenshotOut getScreenshot(MachineIn mIn) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.MachineDisplayGetScreenshot, mIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve screenshot information: " + trans.getError());
        }

        ScreenshotOut screen = trans.extractItem(ScreenshotOut.class);
        return screen;
    }

    @Override
    public List<StorageDeviceAttachmentOut> listAttachments(String machineUuid) {
        Transaction trans = getTransaction(new Request(Command.VBOX, HypervisorTasks.StorageControllerMediumAttachmentList, new MachineIn(machineUuid)));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of Attachment : " + trans.getError());
        }

        List<StorageDeviceAttachmentOut> objOutList = trans.extractItems(StorageDeviceAttachmentOut.class);
        return objOutList;
    }

    @Override
    public boolean isHypervisorConnected() {
        return isHypConnected;
    }

    @Override
    public List<HypervisorLoaderOut> listHypervisors() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.HypervisorList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to list hypervisors: " + trans.getError());
        }

        return trans.extractItems(HypervisorLoaderOut.class);
    }

    @Override
    public void putAnswer(Answer ans) {
        Logger.error("Oprhan answer: " + ans);
    }

    private void refreshInfo() {
        Transaction srvTrans = new Transaction(backend, new Request(Command.HBOX, HyperboxTasks.ServerGet));
        if (!srvTrans.sendAndWait()) {
            disconnect();
            throw new HyperboxException("Unable to retrieve server information: " + srvTrans.getError());
        }
        ServerOut srvOut = srvTrans.extractItem(ServerOut.class);

        id = srvOut.getId();
        name = srvOut.getName();
        type = srvOut.getType();
        version = srvOut.getVersion();
        protocolVersion = srvOut.getNetworkProtocolVersion();
        isHypConnected = srvOut.isHypervisorConnected();
        logLevel = srvOut.getLogLevel();
        if (isHypConnected && (hypReader == null)) {
            hypReader = new Hypervisor(this);
        }
        if (!isHypConnected && (hypReader != null)) {
            hypReader = null;
        }

        EventManager.post(new ServerModifiedEvent(ServerIoFactory.get(this)));
    }

    @Override
    public void connect(String address, String backendId, UserIn usrIn) {
        setState(ConnectionState.Connecting);

        ansRecv = new HashMap<String, _AnswerReceiver>();
        try {
            EventManager.register(this);
            backend = BackendFactory.get(backendId);
            backend.start();
            backend.connect(address);
            Logger.info("Connected to Hyperbox Server");

            Transaction helloTrans = new Transaction(backend, new Request(Command.HBOX, HyperboxTasks.Hello));
            if (!helloTrans.sendAndWait()) {
                if (AnswerType.INVALID_PROTOCOL.equals(helloTrans.getFooter().getType())) {
                    throw new HyperboxException("Incompatible protocol version. Make sure client and server are at the same version.");
                } else {
                    throw new HyperboxException("Error occured during initial handshake with the server.");
                }
            }

            if (AxBooleans.get(Configuration.getSetting(CFGKEY_SERVER_VALIDATE, CFGVAL_SERVER_VALIDATE))) {
                HelloOut helloOut = helloTrans.extractItem(HelloOut.class);
                if (helloOut == null) {
                    throw new HyperboxException("Incompatible protocol version. Make sure client and server are at the same version.");
                }
                Logger.info("Server Network Protocol Version: " + helloOut.getProtocolVersion());

                if (AxBooleans.get(Configuration.getSetting(CFGKEY_SERVER_VALIDATE_VERSION, CFGVAL_SERVER_VALIDATE_VERSION))) {
                    if (!HyperboxAPI.getProtocolVersion().isCompatible(helloOut.getProtocolVersion())) {
                        throw new HyperboxException("Client and Server Network protocol do not match, cannot connect: Client version is "
                                + HyperboxAPI.getProtocolVersion() + " but Server version is " + helloOut.getProtocolVersion());
                    }
                }
            }

            Transaction loginTrans = new Transaction(backend, new Request(Command.HBOX, HyperboxTasks.Login, usrIn));
            if (!loginTrans.sendAndWait()) {
                Logger.error("Login failure : " + loginTrans.getError());
                disconnect();
                throw new HyperboxException("Login failure : " + loginTrans.getError());
            } else {
                Logger.info("Authentication successfull");
            }

            refreshInfo();

            setState(ConnectionState.Connected);
            EventManager.post(new ServerConnectedEvent(ServerIoFactory.get(this)));
        } finally {
            if (ConnectionState.Connecting.equals(getState())) {
                disconnect();
            }
        }
    }

    @Override
    public void disconnect() {
        if (!getState().equals(ConnectionState.Disconnected)) {
            setState(ConnectionState.Disconnecting);
            try {
                if ((backend != null) && backend.isConnected()) {
                    Transaction logOffTrans = getTransaction(new Request(Command.HBOX, HyperboxTasks.Logout));
                    if (!logOffTrans.sendAndWait()) {
                        Logger.warning("Couldn't logout from the server before disconnecting: " + logOffTrans.getError());
                    } else {
                        Logger.verbose("Successful logout from server");
                    }
                    backend.disconnect();
                    backend.stop();
                }
            } catch (Throwable t) {
                Logger.error("Error in backend when trying to disconnect: " + t.getMessage());
                Logger.exception(t);
            }
            backend = null;
            ansRecv = null;
            setState(ConnectionState.Disconnected);
            EventManager.post(new ServerDisconnectedEvent(ServerIoFactory.get(this)));
            EventManager.unregister(this);
        }

    }

    @Override
    public boolean isConnected() {
        return state.equals(ConnectionState.Connected);
    }

    @Handler
    protected final void putBackendConnectionStateEvent(BackendConnectionStateEvent ev) {
        if (ev.getBackend().equals(backend) && !backend.isConnected() && isConnected()) {
            disconnect();
        }
    }

    @Handler
    protected void putHypervisorEvent(HypervisorEventOut ev) {
        if (id.equals(ev.getServerId())) {
            refreshInfo();
        }
    }

    @Handler
    protected final void putServerShutdownEvent(ServerShutdownEventOut ev) {
        if (id.equals(ev.getServerId())) {
            disconnect();
        }
    }

    @Handler
    private void putServerPropertyChanged(ServerPropertyChangedEventOut ev) {
        if (id.equals(ev.getServerId())) {
            refreshInfo();
        }
    }

    @Override
    public _GuestReader getGuest(String machineUuid) {
        // TODO Add machine validation
        return new GuestReader(this, machineUuid);
    }

    @Override
    public List<PermissionOut> listPermissions(UserIn usrIn) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.PermissionList, usrIn));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of permissions : " + trans.getError());
        }

        List<PermissionOut> permOutList = trans.extractItems(PermissionOut.class);
        return permOutList;
    }

    @Override
    public HostOut getHost() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.HostGet));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve host information: " + trans.getError());
        }

        HostOut objOut = trans.extractItem(HostOut.class);
        return objOut;
    }

    @Override
    public SnapshotOut getSnapshot(String vmUuid, String snapUuid) {
        return getSnapshot(new MachineIn(vmUuid), new SnapshotIn(snapUuid));
    }

    @Override
    public SnapshotOut getCurrentSnapshot(String vmUuid) {
        return getCurrentSnapshot(new MachineIn(vmUuid));
    }

    @Override
    public List<ModuleOut> listModules() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.ModuleList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve list of modules : " + trans.getError());
        }

        List<ModuleOut> objOutList = trans.extractItems(ModuleOut.class);
        return objOutList;
    }

    @Override
    public ModuleOut getModule(String modId) {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.ModuleGet, new ModuleIn(modId)));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve module information: " + trans.getError());
        }

        ModuleOut objOut = trans.extractItem(ModuleOut.class);
        return objOut;
    }

    @Override
    public Set<String> listLogLevel() {
        Transaction trans = getTransaction(new Request(Command.HBOX, HyperboxTasks.ServerLogLevelList));
        if (!trans.sendAndWait()) {
            throw new HyperboxException("Unable to retrieve Log levels : " + trans.getError());
        }

        List<String> objOutList = trans.extractItems(String.class);
        return new HashSet<String>(objOutList);
    }

    @Override
    public _Machine getMachineReader(String id) {
        return new Machine(this, id);
    }

    @Override
    public _Task getTask(String id) {
        return new Task(this, id);
    }

}
