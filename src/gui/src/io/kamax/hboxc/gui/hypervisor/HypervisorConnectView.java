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

package io.kamax.hboxc.gui.hypervisor;

import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.LoadingAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.worker.receiver._HypervisorLoaderListReceiver;
import io.kamax.hboxc.gui.workers.HypervisorLoaderListWorker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.miginfocom.swing.MigLayout;

public class HypervisorConnectView implements _Saveable, _Cancelable {

    private String srvId;
    private Set<HypervisorLoaderOut> loaders = new HashSet<HypervisorLoaderOut>();
    private Set<HypervisorLoaderOut> possibleLoaders = new HashSet<HypervisorLoaderOut>();
    private Set<String> vendors = new HashSet<String>();
    private Set<String> products = new HashSet<String>();
    private Set<String> versions = new HashSet<String>();

    private JDialog dialog;

    private JLabel vendorLabel;
    private JComboBox vendorData;
    private JLabel productLabel;
    private JComboBox productData;
    private JLabel versionLabel;
    private JComboBox versionData;
    private JLabel typeLabel;
    private JComboBox typeData;

    private JLabel optionsLabel;
    private JTextField optionsData;

    private JButton connectButton;
    private JButton cancelButton;

    private JPanel panel;
    private JPanel buttonPanel;
    private HypervisorIn hypIn;

    public HypervisorConnectView(String srvId) {
        this.srvId = srvId;

        vendorLabel = new JLabel("Vendor");
        productLabel = new JLabel("Product");
        versionLabel = new JLabel("Version");
        typeLabel = new JLabel("Type");

        vendorData = new JComboBox();
        vendorData.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (vendorData.getSelectedIndex() != -1 && vendorData.getModel().getSize() != 0) {
                    products.clear();
                    possibleLoaders.clear();
                    productData.removeAllItems();

                    for (HypervisorLoaderOut hypLoadOut : loaders) {
                        if (isMatching(hypLoadOut, vendorData)) {
                            if (!products.contains(hypLoadOut.getProduct())) {
                                products.add(hypLoadOut.getProduct());
                                productData.addItem(hypLoadOut.getProduct());
                            }
                        }
                    }
                }
            }
        });
        vendorData.getModel().addListDataListener(new RefreshActionListener());

        productData = new JComboBox();
        productData.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (productData.getModel().getSize() != 0) {
                    versions.clear();
                    versionData.removeAllItems();
                    for (HypervisorLoaderOut hypLoadOut : loaders) {
                        if (isMatching(hypLoadOut, productData)) {
                            if (!versions.contains(hypLoadOut.getVersion())) {
                                versions.add(hypLoadOut.getVersion());
                                versionData.addItem(hypLoadOut.getVersion());
                            }
                        }
                    }
                }
            }

        });
        productData.getModel().addListDataListener(new RefreshActionListener());

        versionData = new JComboBox();
        versionData.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (versionData.getModel().getSize() != 0) {
                    typeData.removeAllItems();
                    for (HypervisorLoaderOut hypLoadOut : loaders) {
                        if (isMatching(hypLoadOut, versionData)) {
                            typeData.addItem(hypLoadOut.getTypeId());
                        }
                    }
                }
            }
        });
        versionData.getModel().addListDataListener(new RefreshActionListener());

        typeData = new JComboBox();
        typeData.getModel().addListDataListener(new RefreshActionListener());

        optionsLabel = new JLabel("Connector Options");
        optionsData = new JTextField();

        connectButton = new JButton(new SaveAction(this, "Connect"));
        cancelButton = new JButton(new CancelAction(this));

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(vendorLabel);
        panel.add(vendorData, "growx,pushx,wrap");
        panel.add(productLabel);
        panel.add(productData, "growx,pushx,wrap");
        panel.add(versionLabel);
        panel.add(versionData, "growx,pushx,wrap");
        panel.add(typeLabel);
        panel.add(typeData, "growx,pushx,wrap");
        panel.add(optionsLabel);
        panel.add(optionsData, "growx,pushx,wrap");

        buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Connect to Hypervisor", connectButton);
        dialog.add(panel, "growx,pushx,wrap");
        dialog.add(buttonPanel, "growx,pushx,wrap");
    }

    private boolean isMatching(HypervisorLoaderOut hlo, Object origin) {
        if (vendorData.getSelectedIndex() != -1 && !hlo.getVendor().equals(vendorData.getSelectedItem())) {
            return false;
        }
        if (vendorData.equals(origin)) {
            return true;
        }

        if (productData.getSelectedIndex() != -1 && !hlo.getProduct().equals(productData.getSelectedItem())) {
            return false;
        }
        if (productData.equals(origin)) {
            return true;
        }

        if (versionData.getSelectedIndex() != -1 && !hlo.getVersion().equals(versionData.getSelectedItem())) {
            return false;
        }
        if (versionData.equals(origin)) {
            return true;
        }

        if (typeData.getSelectedIndex() != -1 && !hlo.getTypeId().equals(typeData.getSelectedItem())) {
            return false;
        }

        return true;
    }

    private void clear() {
        loaders.clear();
        possibleLoaders.clear();
        vendors.clear();
        products.clear();
        versions.clear();

        optionsData.setEnabled(false);
        optionsData.setText(null);
        typeData.setEnabled(false);
        typeData.removeAllItems();
        versionData.setEnabled(false);
        versionData.removeAllItems();
        productData.setEnabled(false);
        productData.removeAllItems();
        vendorData.setEnabled(false);
        vendorData.removeAllItems();
    }

    public void show() {
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        HypervisorLoaderListWorker.execute(new _HypervisorLoaderListReceiver() {

            private Action action;

            @Override
            public void loadingStarted() {
                clear();
                action = connectButton.getAction();
                connectButton.setAction(LoadingAction.get());
            }

            @Override
            public void loadingFinished(boolean isSuccessful, Throwable t) {
                action.setEnabled(false);
                connectButton.setAction(action);
                for (HypervisorLoaderOut hypLoadOut : loaders) {
                    if (!vendors.contains(hypLoadOut.getVendor())) {
                        vendors.add(hypLoadOut.getVendor());
                        vendorData.addItem(hypLoadOut.getVendor());
                    }
                }
            }

            @Override
            public void add(List<HypervisorLoaderOut> hypLoadList) {
                loaders.addAll(hypLoadList);

            }

        }, srvId);

        dialog.setVisible(true);
    }

    public void hide() {
        dialog.setVisible(false);
    }

    @Override
    public void cancel() {
        hide();
    }

    @Override
    public void save() {
        for (HypervisorLoaderOut hypLoadOut : loaders) {
            if (isMatching(hypLoadOut, typeData)) {
                hypIn = new HypervisorIn(hypLoadOut.getHypervisorId());
                hypIn.setConnectionOptions(optionsData.getText());
                hypIn.setAutoConnect(true);
                hide();
                break;
            }

        }

    }

    public HypervisorIn getUserInput() {
        show();
        return hypIn;
    }

    public static HypervisorIn getInput(ServerOut srvOut) {
        return new HypervisorConnectView(srvOut.getId()).getUserInput();
    }

    private class RefreshActionListener implements ListDataListener {

        private void refresh() {
            vendorData.setEnabled(vendorData.getModel().getSize() > 1);
            productData.setEnabled(productData.getModel().getSize() > 1);
            versionData.setEnabled(versionData.getModel().getSize() > 1);
            typeData.setEnabled(typeData.getModel().getSize() > 1);

            connectButton.setEnabled(
                    vendorData.getSelectedIndex() > -1 &&
                    productData.getSelectedIndex() > -1 &&
                    versionData.getSelectedIndex() > -1 &&
                    typeData.getSelectedIndex() > -1
                    );
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            refresh();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            refresh();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            refresh();
        }

    }

}
