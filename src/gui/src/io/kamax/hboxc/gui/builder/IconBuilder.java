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

package io.kamax.hboxc.gui.builder;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hbox.comm.HypervisorTasks;
import io.kamax.hbox.comm.out.hypervisor.SnapshotOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hbox.constant.StorageControllerType;
import io.kamax.hbox.states.MachineStates;
import io.kamax.hboxc.HyperboxClient;
import io.kamax.hboxc.comm.output.ConnectorOutput;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.tool.logging.Logger;
import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class IconBuilder {

    public static final String CFGKEY_ICON_BASE_DIR = "client.gui.icon.dir";
    public static final String CFGVAL_ICON_BASE_DIR = "icons/";

    private static String basePath;
    private static ImageIcon unknownElement = new ImageIcon(getBasePath() + "help.png");

    private static ImageIcon hbIcon = new ImageIcon(getBasePath() + "hyperbox-icon_16px.png");
    private static ImageIcon hbLogo = new ImageIcon(getBasePath() + "hyperbox-logo.png");
    private static ImageIcon loginHeader = new ImageIcon(getBasePath() + "login-header.png");

    public static final ImageIcon AddIcon = new ImageIcon(getBasePath() + "add.png");
    public static final ImageIcon DelIcon = new ImageIcon(getBasePath() + "delete.png");

    private static Map<HypervisorTasks, ImageIcon> vbTasks;
    private static Map<HyperboxTasks, ImageIcon> hbTasks;
    private static Map<ClientTasks, ImageIcon> clientTasks;

    private static Map<MachineStates, ImageIcon> machineStates;

    private static Map<String, ImageIcon> entTypes;
    private static ImageIcon unknownEntType;

    private static Map<String, ImageIcon> scTypes;
    private static ImageIcon unknownScType;

    private IconBuilder() {
        // static only
    }

    static {
        initClientTasks();
        initHbTasks();
        initVbTasks();
        initMachineStates();
        initStorageControllerTypes();
        initEntityTypes();
    }

    private static String getBasePath() {
        if (basePath == null) {
            basePath = Configuration.getSetting(CFGKEY_ICON_BASE_DIR, Configuration.getSetting(HyperboxClient.CFGKEY_BASE_DIR, HyperboxClient.CFGVAL_BASE_DIR)
                    + File.separator + CFGVAL_ICON_BASE_DIR);
        }

        return basePath;
    }

    private static void initClientTasks() {
        clientTasks = new EnumMap<ClientTasks, ImageIcon>(ClientTasks.class);
        clientTasks.put(ClientTasks.ConnectorConnect, new ImageIcon(getBasePath() + "server_connect.png"));
        clientTasks.put(ClientTasks.ConnectorGet, new ImageIcon(getBasePath() + "details.png"));
        clientTasks.put(ClientTasks.NotificationClose, new ImageIcon(getBasePath() + "cross.png"));
        clientTasks.put(ClientTasks.Exit, new ImageIcon(getBasePath() + "exit.png"));
    }

    private static void initHbTasks() {
        hbTasks = new EnumMap<HyperboxTasks, ImageIcon>(HyperboxTasks.class);
        hbTasks.put(HyperboxTasks.UserCreate, new ImageIcon(getBasePath() + "user_create.png"));
        hbTasks.put(HyperboxTasks.UserDelete, new ImageIcon(getBasePath() + "user_delete.png"));
        hbTasks.put(HyperboxTasks.UserModify, new ImageIcon(getBasePath() + "user_edit.png"));

        hbTasks.put(HyperboxTasks.StoreGet, new ImageIcon(getBasePath() + "store_browse.png"));
        hbTasks.put(HyperboxTasks.StoreCreate, new ImageIcon(getBasePath() + "store_create.png"));
        hbTasks.put(HyperboxTasks.StoreDelete, new ImageIcon(getBasePath() + "store_delete.png"));
        hbTasks.put(HyperboxTasks.StoreRegister, new ImageIcon(getBasePath() + "store_register.png"));
        hbTasks.put(HyperboxTasks.StoreUnregister, new ImageIcon(getBasePath() + "store_unregister.png"));
    }

    private static void initVbTasks() {
        vbTasks = new EnumMap<HypervisorTasks, ImageIcon>(HypervisorTasks.class);
        vbTasks.put(HypervisorTasks.MachineAcpiPowerButton, new ImageIcon(getBasePath() + "control_power_blue.png"));
        vbTasks.put(HypervisorTasks.MachineCreate, new ImageIcon(getBasePath() + "computer_add.png"));
        vbTasks.put(HypervisorTasks.MachineDelete, new ImageIcon(getBasePath() + "computer_delete.png"));
        vbTasks.put(HypervisorTasks.MachineModify, new ImageIcon(getBasePath() + "computer_edit.png"));
        vbTasks.put(HypervisorTasks.MachineRegister, new ImageIcon(getBasePath() + "computer_link.png"));
        vbTasks.put(HypervisorTasks.MachineReset, new ImageIcon(getBasePath() + "reload.png"));
        vbTasks.put(HypervisorTasks.MachinePowerOn, new ImageIcon(getBasePath() + "control_play_blue.png"));
        vbTasks.put(HypervisorTasks.MachinePowerOff, new ImageIcon(getBasePath() + "control_power.png"));
        vbTasks.put(HypervisorTasks.MachineUnregister, new ImageIcon(getBasePath() + "computer_stop.png"));

        vbTasks.put(HypervisorTasks.SnapshotDelete, new ImageIcon(getBasePath() + "camera_stop.png"));
        vbTasks.put(HypervisorTasks.SnapshotGet, new ImageIcon(getBasePath() + "camera.png"));
        vbTasks.put(HypervisorTasks.SnapshotGetRoot, new ImageIcon(getBasePath() + "camera.png"));
        vbTasks.put(HypervisorTasks.SnapshotModify, new ImageIcon(getBasePath() + "camera_edit.png"));
        vbTasks.put(HypervisorTasks.SnapshotRestore, new ImageIcon(getBasePath() + "camera_go.png"));
        vbTasks.put(HypervisorTasks.SnapshotTake, new ImageIcon(getBasePath() + "camera_add.png"));

        vbTasks.put(HypervisorTasks.StorageControllerAdd, new ImageIcon(getBasePath() + "tab_add.png"));
        vbTasks.put(HypervisorTasks.StorageControllerRemove, new ImageIcon(getBasePath() + "tab_delete.png"));
        vbTasks.put(HypervisorTasks.StorageControllerMediumAttachmentAdd, new ImageIcon(getBasePath() + "drive_add.png"));
        vbTasks.put(HypervisorTasks.StorageControllerMediumAttachmentRemove, new ImageIcon(getBasePath() + "drive_delete.png"));

        vbTasks.put(HypervisorTasks.MediumMount, new ImageIcon(getBasePath() + "cd_add.png"));
        vbTasks.put(HypervisorTasks.MediumUnmount, new ImageIcon(getBasePath() + "cd_delete.png"));
        vbTasks.put(HypervisorTasks.MediumCreate, new ImageIcon(getBasePath() + "database_add.png"));
        vbTasks.put(HypervisorTasks.MediumRegister, new ImageIcon(getBasePath() + "database_link.png"));
        vbTasks.put(HypervisorTasks.MediumModify, new ImageIcon(getBasePath() + "cd_modify.png"));

        vbTasks.put(HypervisorTasks.NetAdaptorAdd, new ImageIcon(getBasePath() + "add.png"));
        vbTasks.put(HypervisorTasks.NetAdaptorModify, new ImageIcon(getBasePath() + "edit.png"));
        vbTasks.put(HypervisorTasks.NetAdaptorRemove, new ImageIcon(getBasePath() + "cross.png"));
    }

    private static void initMachineStates() {
        machineStates = new EnumMap<MachineStates, ImageIcon>(MachineStates.class);

        machineStates.put(MachineStates.Aborted, new ImageIcon(getBasePath() + "cross.png"));
        machineStates.put(MachineStates.Inaccessible, new ImageIcon(getBasePath() + "delete.png"));
        machineStates.put(MachineStates.Paused, new ImageIcon(getBasePath() + "pause_blue.png"));
        machineStates.put(MachineStates.PoweredOff, new ImageIcon(getBasePath() + "stop_red.png"));
        machineStates.put(MachineStates.Restoring, new ImageIcon(getBasePath() + "disk_upload.png"));
        machineStates.put(MachineStates.Starting, new ImageIcon(getBasePath() + "play_blue.png"));
        machineStates.put(MachineStates.Running, new ImageIcon(getBasePath() + "play_green.png"));
        machineStates.put(MachineStates.Saved, new ImageIcon(getBasePath() + "disk.png"));
        machineStates.put(MachineStates.Saving, new ImageIcon(getBasePath() + "disk_download.png"));
        machineStates.put(MachineStates.Stuck, new ImageIcon(getBasePath() + "delete.png"));
    }

    private static void initStorageControllerTypes() {
        scTypes = new HashMap<String, ImageIcon>();
        unknownScType = new ImageIcon(getBasePath() + "help.png");

        scTypes.put(StorageControllerType.Floppy.getId(), new ImageIcon(getBasePath() + "controller.png"));
        scTypes.put(StorageControllerType.IDE.getId(), new ImageIcon(getBasePath() + "controller.png"));
        scTypes.put(StorageControllerType.SAS.getId(), new ImageIcon(getBasePath() + "controller.png"));
        scTypes.put(StorageControllerType.SATA.getId(), new ImageIcon(getBasePath() + "controller.png"));
        scTypes.put(StorageControllerType.SCSI.getId(), new ImageIcon(getBasePath() + "controller.png"));
    }

    private static void initEntityTypes() {
        entTypes = new HashMap<String, ImageIcon>();
        entTypes.put(EntityType.Hyperbox.getId(), getHyperbox());
        entTypes.put(EntityType.Guest.getId(), new ImageIcon(getBasePath() + "monitor.png"));
        entTypes.put(EntityType.Machine.getId(), new ImageIcon(getBasePath() + "computer.png"));
        entTypes.put(EntityType.DVD.getId(), new ImageIcon(getBasePath() + "cd.png"));
        entTypes.put(EntityType.HardDisk.getId(), new ImageIcon(getBasePath() + "harddisk.png"));
        entTypes.put(EntityType.Floppy.getId(), new ImageIcon(getBasePath() + "disk.png"));
        entTypes.put(EntityType.Server.getId(), new ImageIcon(getBasePath() + "server.png"));
        entTypes.put(EntityType.Display.getId(), new ImageIcon(getBasePath() + "monitor.png"));
        entTypes.put(EntityType.CPU.getId(), new ImageIcon(getBasePath() + "shape_shadow.png"));
        entTypes.put(EntityType.Audio.getId(), new ImageIcon(getBasePath() + "sound.png"));
        entTypes.put(EntityType.Network.getId(), new ImageIcon(getBasePath() + "network.png"));
        entTypes.put(EntityType.DiskDrive.getId(), new ImageIcon(getBasePath() + "drive.png"));
        entTypes.put(EntityType.DvdDrive.getId(), new ImageIcon(getBasePath() + "drive_cd.png"));
        entTypes.put(EntityType.FloppyDrive.getId(), new ImageIcon(getBasePath() + "drive_disk.png"));
        entTypes.put(EntityType.User.getId(), new ImageIcon(getBasePath() + "user.png"));
        entTypes.put(EntityType.Store.getId(), new ImageIcon(getBasePath() + "store.png"));
        entTypes.put(EntityType.Task.getId(), new ImageIcon(getBasePath() + "task.png"));
    }

    public static ImageIcon getHyperbox() {
        return hbIcon;
    }

    public static ImageIcon getLogo() {
        return hbLogo;
    }

    public static ImageIcon getLoginHeader() {
        return loginHeader;
    }

    public static ImageIcon getTask(HypervisorTasks task) {
        if (vbTasks.containsKey(task)) {
            return vbTasks.get(task);
        } else {
            Logger.debug("No icon found for VirtualboxTask: " + task);
            return unknownElement;
        }
    }

    public static ImageIcon getTask(HyperboxTasks task) {
        if (hbTasks.containsKey(task)) {
            return hbTasks.get(task);
        } else {
            Logger.debug("No icon found for HyperboxTask: " + task);
            return unknownElement;
        }
    }

    public static ImageIcon getTask(ClientTasks task) {
        if ((task != null) && clientTasks.containsKey(task)) {
            return clientTasks.get(task);
        } else {
            Logger.debug("No icon found for ClientTask: " + task);
            return unknownElement;
        }
    }

    public static ImageIcon getMachineState(String state) {
        if (state == null) {
            return unknownElement;
        }

        try {
            return getMachineState(MachineStates.valueOf(state));
        }
        // TODO catch the proper exception
        catch (Throwable t) {
            return unknownElement;
        }
    }

    public static ImageIcon getMachineState(MachineStates state) {
        if ((state != null) && machineStates.containsKey(state)) {
            return machineStates.get(state);
        } else {
            Logger.debug("No icon found for Machine State: " + state);
            return unknownElement;
        }
    }

    public static ImageIcon getStorageControllerType(String type) {
        if ((type != null) && scTypes.containsKey(type)) {
            return scTypes.get(type);
        } else {
            Logger.debug("No icon found for Storage Controller Type: " + type);
            return unknownScType;
        }
    }

    public static ImageIcon getEntityType(EntityType type) {
        return getEntityType(type.getId());
    }

    public static ImageIcon getEntityType(String type) {
        if ((type != null) && entTypes.containsKey(type)) {
            return entTypes.get(type);
        } else {
            Logger.debug("No icon found for Entity Type: " + type);
            return unknownEntType;
        }
    }

    public static ImageIcon getDeviceType(String type) {
        return getEntityType(EntityType.valueOf(type));
    }

    public static ImageIcon getSnapshot(SnapshotOut snapOut) {
        if (snapOut.isOnline()) {
            return new ImageIcon(getBasePath() + "camera_start.png");
        } else {
            return new ImageIcon(getBasePath() + "camera_stop.png");
        }
    }

    public static ImageIcon getConnector(ConnectorOutput conOut) {
        if (conOut.isConnected()) {
            if (conOut.getServer().isHypervisorConnected()) {
                return new ImageIcon(getBasePath() + "server_start.png");
            } else {
                return new ImageIcon(getBasePath() + "server_stop.png");
            }
        } else {
            return new ImageIcon(getBasePath() + "disconnect.png");
        }
    }

    public static ImageIcon getSettings() {
        return new ImageIcon(getBasePath() + "computer_wrench.png");
    }

}
