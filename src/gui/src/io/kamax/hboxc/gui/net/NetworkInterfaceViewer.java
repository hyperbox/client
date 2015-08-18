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

package io.kamax.hboxc.gui.net;

import io.kamax.hbox.comm.in.NetworkInterfaceIn;
import io.kamax.hbox.comm.out.network.NetworkAttachModeOut;
import io.kamax.hbox.comm.out.network.NetworkAttachNameOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceTypeOut;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachModeReceiver;
import io.kamax.hboxc.gui.worker.receiver._NetworkAttachNameReceiver;
import io.kamax.hboxc.gui.worker.receiver._NetworkInterfaceTypeReceiver;
import io.kamax.hboxc.gui.workers.NetworkAttachModeListWorker;
import io.kamax.hboxc.gui.workers.NetworkAttachNameListWorker;
import io.kamax.hboxc.gui.workers.NetworkInterfaceTypeListWorker;
import io.kamax.tool.AxStrings;
import io.kamax.tool.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class NetworkInterfaceViewer {

   private String srvId;
   private NetworkInterfaceOut nicOut;
   private NetworkInterfaceIn nicIn;

   private String loadingItem = "Loading...";

   private JLabel enableNicLabel = new JLabel();
   private JCheckBox enableNicValue = new JCheckBox();
   private JLabel connectedLabel = new JLabel();
   private JCheckBox connectedValue = new JCheckBox();
   private JLabel attachToLabel = new JLabel();
   private JComboBox attachModeValue = new JComboBox();
   private JLabel attachNameLabel = new JLabel();
   private JComboBox attachNameValue = new JComboBox();
   private JLabel adapterTypeLabel = new JLabel();
   private JComboBox adapterTypeValue = new JComboBox();
   private JLabel macAddrLabel = new JLabel();
   private JTextField macAddrValue = new JTextField(30);

   private JPanel mainPanel = new JPanel(new MigLayout());

   public static NetworkInterfaceViewer show(String srvId, NetworkInterfaceOut nicOut) {
      NetworkInterfaceViewer viewer = new NetworkInterfaceViewer(srvId, nicOut);
      return viewer;
   }

   public static NetworkInterfaceViewer show(String srvId, NetworkInterfaceOut nicOut, NetworkInterfaceIn nicIn) {
      NetworkInterfaceViewer viewer = new NetworkInterfaceViewer(srvId, nicOut, nicIn);
      return viewer;
   }

   public NetworkInterfaceViewer(String srvId, NetworkInterfaceOut nicOut) {
      this.srvId = srvId;
      this.nicOut = nicOut;

      setLabels();

      enableNicValue.setSelected(false);
      connectedValue.setSelected(false);
      attachModeValue.addActionListener(new AttachTypeListener());
      attachModeValue.removeAllItems();
      attachNameValue.setEditable(true);
      attachNameValue.removeAllItems();
      adapterTypeValue.removeAllItems();
      macAddrValue.setText(null);

      mainPanel.add(enableNicLabel);
      mainPanel.add(enableNicValue, "growx, pushx, wrap");
      mainPanel.add(connectedLabel);
      mainPanel.add(connectedValue, "growx, pushx, wrap");
      mainPanel.add(attachToLabel);
      mainPanel.add(attachModeValue, "growx, pushx, wrap");
      mainPanel.add(attachNameLabel);
      mainPanel.add(attachNameValue, "growx, pushx, wrap");
      mainPanel.add(adapterTypeLabel);
      mainPanel.add(adapterTypeValue, "growx, pushx, wrap");
      mainPanel.add(macAddrLabel);
      mainPanel.add(macAddrValue, "growx, pushx, wrap");

      enableNicValue.setSelected(nicOut.isEnabled());
      connectedValue.setSelected(nicOut.isCableConnected());
      macAddrValue.setText(nicOut.getMacAddress());

      NetworkAttachModeListWorker.execute(new NetworkModeReceiver(), srvId);
      NetworkInterfaceTypeListWorker.execute(new NetworkInterfaceTypeReceiver(), srvId);
   }

   public NetworkInterfaceViewer(String srvId, NetworkInterfaceOut nicOut, NetworkInterfaceIn nicIn) {
      this(srvId, nicOut);
      this.nicIn = nicIn;
   }

   private void setLabels() {
      loadingItem = "Loading...";
      enableNicLabel.setText("Enabled");
      connectedLabel.setText("Cable Connected");
      attachToLabel.setText("Attach Mode");
      attachNameLabel.setText("Attach To");
      adapterTypeLabel.setText("Adapter Type");
      macAddrLabel.setText("MAC Address");
   }

   public JPanel getPanel() {
      return mainPanel;
   }

   public NetworkInterfaceIn save() {
      if (nicIn != null) {
         nicIn.setEnabled(enableNicValue.isSelected());
         if (enableNicValue.isEnabled()) {
            nicIn.setCableConnected(connectedValue.isSelected());
            nicIn.setAttachMode(attachModeValue.getSelectedItem().toString());
            nicIn.setAttachName(AxStrings.get(attachNameValue.getSelectedItem()));
            nicIn.setAdapterType(adapterTypeValue.getSelectedItem().toString());
            nicIn.setMacAddress(macAddrValue.getText());
         }
      }

      if (nicIn.hasNewData()) {
         return nicIn;
      } else {
         return null;
      }
   }

   private class AttachTypeListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent e) {
         if (attachModeValue.getSelectedItem() == loadingItem) {
            return;
         }

         String attachTypeId = attachModeValue.getSelectedItem().toString();
         if (AxStrings.isEmpty(attachTypeId)) {
            Logger.info("Selected attach mode value is empty, skipping listing of attach type");
            return;
         }

         Logger.info(attachTypeId + " was selected as attachment type, fetching list of attachment names");
         NetworkAttachNameListWorker.execute(new NetworkAttachNameReceiver(), srvId, attachTypeId);
      }
   }

   private class NetworkInterfaceTypeReceiver implements _NetworkInterfaceTypeReceiver {

      @Override
      public void loadingStarted() {
         adapterTypeValue.setEnabled(false);
         adapterTypeValue.removeAllItems();
         adapterTypeValue.addItem(loadingItem);
         adapterTypeValue.setSelectedItem(loadingItem);
      }

      @Override
      public void loadingFinished(boolean isSuccessful, String message) {
         if (isSuccessful) {
            adapterTypeValue.removeItem(loadingItem);
            adapterTypeValue.setSelectedItem(nicOut.getAdapterType());
         } else {
            adapterTypeValue.removeAllItems();
            adapterTypeValue.addItem("Error loading network interface types: " + message);
         }
         adapterTypeValue.setEnabled(true);
      }

      @Override
      public void add(List<NetworkInterfaceTypeOut> objOutList) {
         for (NetworkInterfaceTypeOut adapterType : objOutList) {
            adapterTypeValue.addItem(adapterType.getId());
         }
      }

   }

   private class NetworkModeReceiver implements _NetworkAttachModeReceiver {

      @Override
      public void loadingStarted() {
         attachModeValue.setEnabled(false);
         attachModeValue.removeAllItems();
         attachModeValue.addItem(loadingItem);
         attachModeValue.setSelectedItem(loadingItem);
      }

      @Override
      public void loadingFinished(boolean isSuccessful, String message) {
         if (isSuccessful) {
            attachModeValue.removeItem(loadingItem);
            attachModeValue.setSelectedItem(nicOut.getAttachMode());
         } else {
            attachModeValue.removeAllItems();
            attachModeValue.addItem("Error loading attach modes: " + message);
         }
         attachModeValue.setEnabled(true);
      }

      @Override
      public void add(List<NetworkAttachModeOut> attachModes) {
         for (NetworkAttachModeOut attachMode : attachModes) {
            attachModeValue.addItem(attachMode.getId());
         }
      }

   }

   private class NetworkAttachNameReceiver implements _NetworkAttachNameReceiver {

      @Override
      public void loadingStarted() {
         attachModeValue.setEnabled(false);
         attachNameValue.setEnabled(false);
         attachNameValue.removeAllItems();
         attachNameValue.addItem(loadingItem);
         attachNameValue.setSelectedItem(loadingItem);
      }

      @Override
      public void loadingFinished(boolean isSuccessful, String message) {
         if (isSuccessful) {
            attachNameValue.removeItem(loadingItem);
            if (attachModeValue.getSelectedItem().equals(nicOut.getAttachMode())) {
               attachNameValue.setSelectedItem(nicOut.getAttachName());
            } else {
               attachNameValue.setSelectedIndex(-1);
            }
         } else {
            attachNameValue.removeAllItems();
            attachNameValue.addItem("Error loading attach names: " + message);
         }
         attachNameValue.setEnabled(true);
         attachModeValue.setEnabled(true);
      }

      @Override
      public void add(List<NetworkAttachNameOut> nanOut) {
         for (NetworkAttachNameOut attachName : nanOut) {
            Logger.debug("Adding attachment name: " + attachName.getId());
            attachNameValue.addItem(attachName.getId());
         }
      }

   }

}
