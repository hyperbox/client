/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.core.server;

import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.NetAdaptorIn;
import io.kamax.hbox.comm.in.NetModeIn;
import io.kamax.hbox.comm.io.MachineLogFileIO;
import io.kamax.hbox.comm.io.NetServiceIO;
import io.kamax.hbox.comm.out.event.hypervisor.HypervisorDisconnectedEventOut;
import io.kamax.hbox.comm.out.event.hypervisor.HypervisorEventOut;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hbox.comm.out.network.NetAdaptorOut;
import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.exception.net.InvalidNetworkModeException;
import io.kamax.hbox.exception.net.NetworkAdaptorNotFoundException;
import io.kamax.hbox.hypervisor._MachineLogFile;
import io.kamax.hboxc.comm.utils.Transaction;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.server._Hypervisor;
import io.kamax.hboxc.server._Server;
import net.engio.mbassy.listener.Handler;

import java.util.ArrayList;
import java.util.List;

public class Hypervisor implements _Hypervisor {

    private _Server srv;
    private HypervisorOut hypData;

    private void refresh() {
        if (srv.isHypervisorConnected()) {
            Transaction t = srv.sendRequest(new Request(Command.HBOX, HyperboxTasks.HypervisorGet));
            hypData = t.extractItem(HypervisorOut.class);
        } else {
            hypData = null;
        }

    }

    public Hypervisor(_Server srv) {
        this.srv = srv;
        EventManager.register(this);
        refresh();
    }

    @Override
    public HypervisorOut getInfo() {
        return hypData;
    }

    @Override
    public String getType() {
        return hypData.getType();
    }

    @Override
    public String getVendor() {
        return hypData.getVendor();
    }

    @Override
    public String getProduct() {
        return hypData.getProduct();
    }

    @Override
    public String getVersion() {
        return hypData.getVersion();
    }

    @Override
    public String getRevision() {
        return hypData.getRevision();
    }

    @Override
    public boolean hasToolsMedium() {
        return getToolsMedium() != null;
    }

    @Override
    public MediumOut getToolsMedium() {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.ToolsMediumGet));
        return t.extractItem(MediumOut.class);
    }

    @Handler
    protected void putHypervisorDisconnectedEvent(HypervisorDisconnectedEventOut ev) {

        hypData = null;
    }

    @Handler
    protected void putHypervisorEvent(HypervisorEventOut ev) {

        refresh();
    }

    @Override
    public List<NetModeOut> listNetworkModes() {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetModeList));
        return t.extractItems(NetModeOut.class);
    }

    @Override
    public NetModeOut getNetworkMode(String id) {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetModeGet, new NetModeIn(id)));
        return t.extractItem(NetModeOut.class);
    }

    @Override
    public List<NetAdaptorOut> listAdaptors() {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetAdaptorList));
        return t.extractItems(NetAdaptorOut.class);
    }

    @Override
    public List<NetAdaptorOut> listAdaptors(String modeId) throws InvalidNetworkModeException {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetAdaptorList, new NetModeIn(modeId)));
        return t.extractItems(NetAdaptorOut.class);
    }

    @Override
    public NetAdaptorOut createAdaptor(String modeId, String name) throws InvalidNetworkModeException {
        NetAdaptorIn adaptIn = new NetAdaptorIn();
        adaptIn.setModeId(modeId);
        adaptIn.setLabel(name);
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetAdaptorAdd, adaptIn));
        return t.extractItem(NetAdaptorOut.class);
    }

    @Override
    public void removeAdaptor(String modeId, String adaptorId) throws InvalidNetworkModeException {
        srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetAdaptorRemove, new NetAdaptorIn(modeId, adaptorId)));
    }

    @Override
    public NetAdaptorOut getNetAdaptor(String modeId, String adaptorId) throws NetworkAdaptorNotFoundException {
        Transaction t = srv.sendRequest(new Request(Command.VBOX, HypervisorTasks.NetAdaptorGet, new NetAdaptorIn(modeId, adaptorId)));
        return t.extractItem(NetAdaptorOut.class);
    }

    @Override
    public String getServerId() {
        return srv.getId();
    }

    @Override
    public NetServiceIO getNetService(String modeId, String adaptorId, String svcTypeId) throws NetworkAdaptorNotFoundException {
        Request req = new Request(Command.VBOX, HypervisorTasks.NetServiceGet);
        req.set(NetAdaptorIn.class, new NetAdaptorIn(modeId, adaptorId));
        req.set(NetServiceIO.class, new NetServiceIO(svcTypeId));
        return srv.sendRequest(req).extractItem(NetServiceIO.class);
    }

    @Override
    public List<String> getLogFileList(String vmId) {
        Request req = new Request(Command.VBOX, HypervisorTasks.MachineLogFileList);
        req.set(MachineIn.class, new MachineIn(vmId));
        List<String> returnList = new ArrayList<String>();
        for (MachineLogFileIO logIo : srv.sendRequest(req).extractItems(MachineLogFileIO.class)) {
            returnList.add(logIo.getId());
        }
        return returnList;
    }

    @Override
    public _MachineLogFile getLogFile(String vmId, String logid) {
        Request req = new Request(Command.VBOX, HypervisorTasks.MachineLogFileGet);
        req.set(MachineLogFileIO.class, new MachineLogFileIO(String.valueOf(logid)));
        req.set(MachineIn.class, new MachineIn(vmId));
        return srv.sendRequest(req).extractItem(MachineLogFileIO.class);
    }

}
