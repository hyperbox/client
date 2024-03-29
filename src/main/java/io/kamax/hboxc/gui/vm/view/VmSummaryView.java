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

package io.kamax.hboxc.gui.vm.view;

import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.MediumIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.event.storage.StorageControllerAttachmentDataModifiedEventOut;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.comm.out.storage.StorageControllerOut;
import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hbox.constant.MachineAttribute;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.event.machine.MachineStateChangedEvent;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.ViewEventManager;
import io.kamax.hboxc.gui.action.storage.StorageDeviceAttachmentMediumEditAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.net.NetworkInterfaceSummary;
import io.kamax.hboxc.gui.utils.StorageDeviceAttachmentOutComparator;
import io.kamax.hboxc.gui.worker.receiver.AnswerWorkerReceiver;
import io.kamax.hboxc.gui.worker.receiver._AnswerWorkerReceiver;
import io.kamax.tools.AxStrings;
import io.kamax.tools.helper.swing.BorderUtils;
import io.kamax.tools.helper.swing.JTextFieldUtils;
import io.kamax.tools.logging.KxLog;
import net.engio.mbassy.listener.Handler;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

public final class VmSummaryView {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private MachineOut mOut;
    private Map<String, StorageControllerOut> controllers;

    private JPanel panel;

    private JPanel generalPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel uuidLabel;
    private JTextField uuidField;
    private JLabel stateLabel;
    private JTextField stateField;
    private JLabel osTypeLabel;
    private JTextField osTypeField;

    private JPanel systemPanel;
    private JLabel cpuCountLabel;
    private JTextField cpuCountValue;
    private JLabel memoryLabel;
    private JTextField memoryValue;
    private JLabel accelLabel;
    private JTextField accelValue;

    private JPanel displayPanel;
    private JLabel vramLabel;
    private JTextField vramValue;
    private JLabel consoleModuleLabel;
    private JTextField consoleModuleValue;
    private JLabel consoleAddressLabel;
    private JTextField consoleAddressValue;
    private JButton consoleConnectButton;

    private JPanel storagePanel;

    private JPanel audioPanel;
    private JLabel hostDriverLabel;
    private JTextField hostDriverValue;
    private JLabel audioControllerLabel;
    private JTextField audioControllerValue;

    private JPanel networkPanel;

    private JPanel descPanel;
    private JTextArea descArea;

    public VmSummaryView() {
        init();
    }

    private void init() {
        initGeneral();
        initSystem();
        initDisplay();
        initStorage();
        initAudio();
        initNetwork();
        initDesc();

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(generalPanel, "growx, pushx, wrap");
        panel.add(systemPanel, "growx, pushx, wrap");
        panel.add(displayPanel, "growx, pushx, wrap");
        panel.add(storagePanel, "growx, pushx, wrap");
        panel.add(audioPanel, "growx, pushx, wrap");
        panel.add(networkPanel, "growx, pushx, wrap");
        panel.add(descPanel, "growx, pushx, wrap");

        ViewEventManager.register(this);
    }

    private void initGeneral() {
        nameLabel = new JLabel("Name");
        nameField = JTextFieldUtils.createNonEditable();

        uuidLabel = new JLabel("UUID");
        uuidField = JTextFieldUtils.createNonEditable();

        stateLabel = new JLabel("Status");
        stateField = JTextFieldUtils.createNonEditable();

        osTypeLabel = new JLabel("OS Type");
        osTypeField = JTextFieldUtils.createNonEditable();

        generalPanel = new JPanel(new MigLayout());
        generalPanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "General"));
        generalPanel.add(nameLabel);
        generalPanel.add(nameField, "growx, pushx, wrap");
        generalPanel.add(uuidLabel);
        generalPanel.add(uuidField, "growx, pushx, wrap");
        generalPanel.add(stateLabel);
        generalPanel.add(stateField, "growx, pushx, wrap");
        generalPanel.add(osTypeLabel);
        generalPanel.add(osTypeField, "growx, pushx, wrap");
    }

    private void initSystem() {
        cpuCountLabel = new JLabel("vCPU");
        cpuCountValue = JTextFieldUtils.createNonEditable();
        memoryLabel = new JLabel("Memory");
        memoryValue = JTextFieldUtils.createNonEditable();
        accelLabel = new JLabel("Acceleration");
        accelValue = JTextFieldUtils.createNonEditable();

        systemPanel = new JPanel(new MigLayout());
        systemPanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "System"));
        systemPanel.add(cpuCountLabel);
        systemPanel.add(cpuCountValue, "growx, pushx, wrap");
        systemPanel.add(memoryLabel);
        systemPanel.add(memoryValue, "growx, pushx, wrap");
        systemPanel.add(accelLabel);
        systemPanel.add(accelValue, "growx, pushx, wrap");
    }

    private void initDisplay() {
        vramLabel = new JLabel("VRAM");
        vramValue = JTextFieldUtils.createNonEditable();
        consoleModuleLabel = new JLabel("Console Module");
        consoleModuleValue = JTextFieldUtils.createNonEditable();
        consoleAddressLabel = new JLabel("Console Address");
        consoleAddressValue = JTextFieldUtils.createNonEditable();
        consoleConnectButton = new JButton("Connect");
        consoleConnectButton.setEnabled(false);
        consoleConnectButton.addActionListener(new ConnectAction());

        displayPanel = new JPanel(new MigLayout());
        displayPanel.setBorder(BorderUtils.createTitledBorder(Color.GRAY, "Display"));
        displayPanel.add(vramLabel);
        displayPanel.add(vramValue, "growx, pushx,span 2, wrap");
        displayPanel.add(consoleModuleLabel);
        displayPanel.add(consoleModuleValue, "growx, pushx, span 2, wrap");
        displayPanel.add(consoleAddressLabel);
        displayPanel.add(consoleAddressValue, "growx, pushx");
        displayPanel.add(consoleConnectButton, "wrap");
    }

    private void initStorage() {
        controllers = new HashMap<String, StorageControllerOut>();
        storagePanel = new JPanel(new MigLayout());
        storagePanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "Storage"));
    }

    private void initAudio() {
        hostDriverLabel = new JLabel("Host Driver");
        hostDriverValue = JTextFieldUtils.createNonEditable();
        audioControllerLabel = new JLabel("Controller");
        audioControllerValue = JTextFieldUtils.createNonEditable();

        audioPanel = new JPanel(new MigLayout());
        audioPanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "Audio"));
        audioPanel.add(hostDriverLabel);
        audioPanel.add(hostDriverValue, "growx, pushx, wrap");
        audioPanel.add(audioControllerLabel);
        audioPanel.add(audioControllerValue, "growx, pushx, wrap");
    }

    private void initNetwork() {
        networkPanel = new JPanel(new MigLayout());
        networkPanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "Network"));
    }

    private void initDesc() {
        descArea = new JTextArea();
        descArea.setEditable(false);

        descPanel = new JPanel(new MigLayout());
        descPanel.setBorder(BorderUtils.createTitledBorder(Color.gray, "Description"));
        descPanel.add(descArea, "grow, push, wrap");
    }

    private void clearGeneral() {
        nameField.setText(null);
        uuidField.setText(null);
        stateField.setText(null);
        osTypeField.setText(null);
    }

    private void clearSystem() {
        cpuCountValue.setText(null);
        memoryValue.setText(null);
        accelValue.setText(null);
    }

    private void clearDisplay() {
        vramValue.setText(null);
        consoleModuleValue.setText(null);
        consoleAddressValue.setText(null);
    }

    private void clearStorage() {
        storagePanel.removeAll();
    }

    private void clearAudio() {
        hostDriverValue.setText(null);
        audioControllerValue.setText(null);
    }

    private void clearNetwork() {
        networkPanel.removeAll();
    }

    private void clearDesc() {
        descArea.setText(null);
    }

    public void clear() {
        mOut = null;
        clearGeneral();
        clearSystem();
        clearDisplay();
        clearStorage();
        clearAudio();
        clearNetwork();
        clearDesc();
    }

    public void show(MachineOut mOut, boolean forced) {
        if (forced || this.mOut == null || !this.mOut.equals(mOut)) {
            this.mOut = mOut;
            refresh();
        }
    }

    public void show(MachineOut mOut) {
        show(mOut, false);
    }

    public void refreshGeneral() {
        nameField.setText(mOut.getName());
        uuidField.setText(mOut.getUuid());
        stateField.setText(mOut.getState());
        osTypeField.setText(mOut.getSetting(MachineAttribute.OsType).getString());
    }

    public void refreshSystem() {
        cpuCountValue.setText(mOut.getSetting(MachineAttribute.CpuCount).getString());
        memoryValue.setText(mOut.getSetting(MachineAttribute.Memory).getString() + " MB");
        List<String> extList = new ArrayList<String>();
        if (mOut.hasSetting(MachineAttribute.HwVirtEx) && mOut.getSetting(MachineAttribute.HwVirtEx).getBoolean()) {
            extList.add("VT-x/AMD-V");
        }
        if (mOut.hasSetting(MachineAttribute.HwVirtExExcl) && mOut.getSetting(MachineAttribute.HwVirtExExcl).getBoolean()) {
            extList.add("Virt Unrestricted");
        }
        if (mOut.hasSetting(MachineAttribute.NestedPaging) && mOut.getSetting(MachineAttribute.NestedPaging).getBoolean()) {
            extList.add("Nested Paging");
        }
        if (mOut.hasSetting(MachineAttribute.PAE) && mOut.getSetting(MachineAttribute.PAE).getBoolean()) {
            extList.add("PAE/NX");
        }
        if (mOut.hasSetting(MachineAttribute.LargePages) && mOut.getSetting(MachineAttribute.LargePages).getBoolean()) {
            extList.add("Large Pages");
        }
        if (mOut.hasSetting(MachineAttribute.Vtxvpid) && mOut.getSetting(MachineAttribute.Vtxvpid).getBoolean()) {
            extList.add("VT-x VPID");
        }
        StringBuilder extBuilder = new StringBuilder();
        for (String ext : extList) {
            extBuilder.append(ext + ", ");
        }

        if (extBuilder.lastIndexOf(", ") >= 0) {
            extBuilder.delete(extBuilder.lastIndexOf(", "), extBuilder.length());
        }
        accelValue.setText(extBuilder.toString());
    }

    public void refreshDisplay() {
        vramValue.setText(mOut.getSetting(MachineAttribute.VRAM).getString());
        consoleModuleValue.setText(mOut.getSetting(MachineAttribute.VrdeModule).getString());

        if (mOut.getSetting(MachineAttribute.VrdeEnabled).getBoolean()) {
            new SwingWorker<String, Void>() {

                private String buttonText = consoleConnectButton.getText();

                {
                    consoleAddressValue.setVisible(false);
                    consoleConnectButton.setText(null);
                    consoleConnectButton.setIcon(IconBuilder.LoadingIcon);
                }

                @Override
                protected String doInBackground() throws Exception {
                    return Gui.getReader().getConnectorForServer(mOut.getServerId()).getAddress();
                }

                @Override
                protected void done() {
                    try {
                        URI addrUri = new URI(get());
                        String addr = AxStrings.getNonEmpty(addrUri.getHost(), addrUri.getScheme());
                        if (!AxStrings.isEmpty(mOut.getSetting(MachineAttribute.VrdeAddress).getString())) {
                            addr = mOut.getSetting(MachineAttribute.VrdeAddress).getString();
                        }
                        addr = addr + ":" + mOut.getSetting(MachineAttribute.VrdePort).getString();
                        consoleAddressValue.setText(addr);
                        consoleConnectButton.setEnabled(mOut.getState().equalsIgnoreCase("running"));
                    } catch (URISyntaxException e) {
                        consoleConnectButton.setVisible(false);
                        consoleAddressValue.setText("Invalid address: " + e.getMessage());
                    } catch (Throwable t) {
                        consoleConnectButton.setVisible(false);
                        consoleAddressValue.setText("Error while retrieving console address: " + t.getMessage());
                    } finally {
                        consoleAddressValue.setVisible(true);
                        consoleConnectButton.setText(buttonText);
                        consoleConnectButton.setIcon(null);
                    }
                }

            }.execute();
        } else {
            consoleAddressValue.setText("Not available (Disabled or Console Module not installed)");
        }
    }

    public void refreshStorage() {
        clearStorage();
        if (controllers.isEmpty()) {
            for (StorageControllerOut scOut : mOut.listStorageController()) {
                controllers.put(scOut.getId(), scOut);
            }
        }

        List<StorageControllerOut> scSorted = new ArrayList<StorageControllerOut>(controllers.values());
        Collections.sort(scSorted, new Comparator<StorageControllerOut>() {

            @Override
            public int compare(StorageControllerOut o1, StorageControllerOut o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        for (StorageControllerOut scOut : scSorted) {
            try {
                storagePanel.add(new JLabel(scOut.getType()), "wrap");
                List<StorageDeviceAttachmentOut> scaSorted = new ArrayList<StorageDeviceAttachmentOut>(scOut.getAttachments());
                Collections.sort(scaSorted, new StorageDeviceAttachmentOutComparator());
                for (final StorageDeviceAttachmentOut sdaOut : scaSorted) {
                    final JLabel medLabel = new JLabel();
                    storagePanel.add(new JLabel(""));
                    storagePanel.add(new JLabel(sdaOut.getPortId() + ":" + sdaOut.getDeviceId()));

                    storagePanel.add(new JLabel(""));
                    storagePanel.add(new JLabel(""));
                    storagePanel.add(medLabel);
                    if (!sdaOut.hasMediumInserted()) {
                        medLabel.setText("[" + sdaOut.getDeviceType() + "] Empty");
                    } else {
                        new SwingWorker<MediumOut, Void>() {

                            {
                                medLabel.setIcon(IconBuilder.LoadingIcon);
                            }

                            @Override
                            protected MediumOut doInBackground() throws Exception {
                                MediumOut medOut = Gui.getServer(mOut.getServerId()).getMedium(new MediumIn(sdaOut.getMediumUuid()));
                                while (medOut.hasParent()) {
                                    medOut = Gui.getServer(mOut.getServerId()).getMedium(new MediumIn(medOut.getParentUuid()));
                                }
                                return medOut;
                            }

                            @Override
                            protected void done() {
                                try {
                                    MediumOut medOut = get();
                                    medLabel.setText("[" + sdaOut.getDeviceType() + "] " + medOut.getName());
                                } catch (InterruptedException e) {
                                    medLabel.setText("[" + sdaOut.getDeviceType() + "] " + sdaOut.getMediumUuid() + " (Interrupted while fetching info)");
                                } catch (Throwable e) {
                                    log.error("Error when fetching medium " + sdaOut.getMediumUuid(), e);
                                    medLabel.setText("[" + sdaOut.getDeviceType() + "] " + sdaOut.getMediumUuid() + " (Error while fetching info)");
                                } finally {
                                    medLabel.setIcon(null);
                                }

                            }

                        }.execute();
                    }
                    if (sdaOut.getDeviceType().contentEquals(EntityType.DVD.getId())) {
                        final JButton loader = new JButton();
                        _AnswerWorkerReceiver recv = new AnswerWorkerReceiver() {

                            private Icon oldIcon;

                            @Override
                            public void start() {
                                oldIcon = loader.getIcon();
                                loader.setIcon(IconBuilder.LoadingIcon);
                                loader.setEnabled(false);
                            }

                            @Override
                            public void success() {
                                loader.setEnabled(true);
                                loader.setIcon(oldIcon);
                            }

                            @Override
                            public void fail(Throwable t) {
                                loader.setEnabled(true);
                                loader.setIcon(oldIcon);
                            }
                        };
                        Action ac = new StorageDeviceAttachmentMediumEditAction(mOut.getServerId(), sdaOut, recv);
                        loader.setAction(ac);
                        storagePanel.add(loader, "wrap");
                    } else {
                        storagePanel.add(new JLabel(""), "wrap");
                    }
                }
            } catch (Throwable e) {
                storagePanel.removeAll();
                storagePanel.add(new JLabel("Unable to load storage info: " + e.getMessage()));
            }
        }
    }

    public void refreshAudio() {
        audioPanel.removeAll();
        if (mOut.getSetting(MachineAttribute.AudioEnable).getBoolean()) {
            hostDriverValue.setText(mOut.getSetting(MachineAttribute.AudioDriver).getString());
            audioControllerValue.setText(mOut.getSetting(MachineAttribute.AudioController).getString());

            audioPanel.add(hostDriverLabel);
            audioPanel.add(hostDriverValue, "growx, pushx, wrap");
            audioPanel.add(audioControllerLabel);
            audioPanel.add(audioControllerValue, "growx, pushx, wrap");
        } else {
            audioPanel.add(new JLabel("Disabled"));
        }
        audioPanel.revalidate();
    }

    public void refreshNetwork() {
        clearNetwork();
        for (NetworkInterfaceOut nicOut : mOut.listNetworkInterface()) {
            if (nicOut.isEnabled()) {
                networkPanel.add(new NetworkInterfaceSummary(mOut.getServerId(), mOut.getUuid(), nicOut), "growx, pushx, wrap");
            }
        }
    }

    public void refreshDesc() {
        descArea.setText(mOut.getSetting(MachineAttribute.Description).getString());
    }

    public void refresh() {
        if (mOut == null) {
            clear();
        } else {
            refreshGeneral();
            refreshSystem();
            refreshDisplay();
            controllers.clear();
            refreshStorage();
            refreshAudio();
            refreshNetwork();
            refreshDesc();
            panel.revalidate();
            panel.repaint();
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    @Handler
    public void putMachineStateChangedEvent(MachineStateChangedEvent ev) {
        if (mOut != null && AxStrings.equals(ev.getUuid(), mOut.getUuid())) {
            stateField.setText(ev.getMachine().getState());
            // TODO improve next line
            consoleConnectButton.setEnabled(ev.getMachine().getState().equalsIgnoreCase("running"));
        }
    }

    @Handler
    public void putStorageControllerAttachmentDataChanged(StorageControllerAttachmentDataModifiedEventOut ev) {
        if (mOut != null && AxStrings.equals(ev.getUuid(), mOut.getUuid())) {
            controllers.put(ev.getStorageController().getId(), ev.getStorageController());
            refreshStorage();
        }
    }

    private class ConnectAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            new SwingWorker<Void, Void>() {

                private Icon oldIcon = consoleConnectButton.getIcon();

                {
                    consoleConnectButton.setEnabled(false);
                    consoleConnectButton.setIcon(IconBuilder.LoadingIcon);
                }

                @Override
                protected Void doInBackground() throws Exception {
                    Gui.post(new Request(ClientTasks.ConsoleViewerUse, new ServerIn(mOut.getServerId()), new MachineIn(mOut)));

                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Throwable t) {
                        log.error("Unable to start Console viewer", t);
                        Gui.showError("Unable to start Console viewer: " + t.getMessage());
                    } finally {
                        consoleConnectButton.setEnabled(true);
                        consoleConnectButton.setIcon(oldIcon);
                    }
                }

            }.execute();

        }

    }

}
