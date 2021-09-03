/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
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

package io.kamax.hboxc.server;

import io.kamax.hbox.comm.in.*;
import io.kamax.hbox.comm.out.*;
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
import io.kamax.hboxc.server.task._Task;

import java.util.List;
import java.util.Set;

public interface _ServerReader {

    public String getId();

    public String getName();

    public String getType();

    public String getVersion();

    public String getProtocolVersion();

    public String getLogLevel();

    public _GuestReader getGuest(String vmId);

    public MachineOut getMachine(MachineIn mIn);

    public MachineOut getMachine(String vmId);

    public _Machine getMachineReader(String id);

    public MediumOut getMedium(MediumIn mIn);

    public SessionOut getSession(SessionIn sIn);

    public StorageControllerSubTypeOut getStorageControllerSubType(StorageControllerSubTypeIn scstIn);

    public StorageControllerTypeOut getStorageControllerType(StorageControllerTypeIn sctIn);

    public StoreOut getStore(StoreIn sIn);

    public StoreItemOut getStoreItem(StoreItemIn siIn);

    public TaskOut getTask(TaskIn tIn);

    public UserOut getUser(UserIn uIn);

    public List<StorageDeviceAttachmentOut> listAttachments(StorageControllerIn scIn);

    public List<StorageDeviceAttachmentOut> listAttachments(String machineUuid);

    public List<String> listKeyboardMode(MachineIn mIn);

    public List<MachineOut> listMachines();

    public List<MediumOut> listMediums();

    public List<NetworkInterfaceOut> listNetworkInterfaces(MachineIn mIn);

    public List<NetworkAttachModeOut> listNetworkAttachModes();

    public List<NetworkAttachNameOut> listNetworkAttachNames(NetworkAttachModeIn namIn);

    public List<NetworkInterfaceTypeOut> listNetworkInterfaceTypes();

    public List<String> listMouseMode(MachineIn mIn);

    public List<OsTypeOut> listOsType();

    public List<OsTypeOut> listOsType(MachineIn mIn);

    public List<SessionOut> listSessions();

    public List<StorageControllerSubTypeOut> listStorageControllerSubType(StorageControllerTypeIn sctIn);

    public List<StorageControllerTypeOut> listStorageControllerType();

    public List<StoreItemOut> listStoreItems(StoreIn sIn);

    public List<StoreItemOut> listStoreItems(StoreIn sIn, StoreItemIn siIn);

    public List<StoreOut> listStores();

    public List<TaskOut> listTasks();

    public List<UserOut> listUsers();

    public List<PermissionOut> listPermissions(UserIn usrIn);

    public SnapshotOut getRootSnapshot(String vmId);

    public SnapshotOut getSnapshot(String vmId, String snapUuid);

    public SnapshotOut getSnapshot(MachineIn mIn, SnapshotIn snapIn);

    public SnapshotOut getCurrentSnapshot(String vmId);

    public SnapshotOut getCurrentSnapshot(MachineIn mIn);

    public boolean isHypervisorConnected();

    public _Hypervisor getHypervisor();

    public ScreenshotOut getScreenshot(MachineIn mIn);

    public List<HypervisorLoaderOut> listHypervisors();

    public HostOut getHost();

    public List<ModuleOut> listModules();

    public ModuleOut getModule(String modId);

    public Set<String> listLogLevel();

    public _Task getTask(String id);

}
