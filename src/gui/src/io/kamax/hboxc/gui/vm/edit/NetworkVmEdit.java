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

package io.kamax.hboxc.gui.vm.edit;

import net.miginfocom.swing.MigLayout;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.NetworkInterfaceIn;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.network.NetworkInterfaceOut;
import io.kamax.hboxc.gui.net.NetworkInterfaceViewer;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public final class NetworkVmEdit {

   private String srvId;
   private MachineIn mIn;
   private JTabbedPane nicTabs;
   private List<NetworkInterfaceViewer> viewers;
   private JPanel panel;

   public NetworkVmEdit() {
      viewers = new ArrayList<NetworkInterfaceViewer>();
      nicTabs = new JTabbedPane();
      panel = new JPanel(new MigLayout());
      panel.add(nicTabs, "grow,push");
   }

   public Component getComp() {
      return panel;
   }

   public void update(MachineOut mOut, MachineIn mIn) {
      this.srvId = mOut.getServerId();
      this.mIn = mIn;

      nicTabs.removeAll();
      viewers.clear();
      for (NetworkInterfaceOut nicOut : mOut.listNetworkInterface()) {
         NetworkInterfaceViewer viewer = NetworkInterfaceViewer.show(srvId, nicOut, new NetworkInterfaceIn(mOut.getUuid(), nicOut.getNicId()));
         viewers.add(viewer);
         nicTabs.addTab("NIC " + (nicOut.getNicId() + 1), viewer.getPanel());
      }
   }

   public void save() {
      if (mIn != null) {
         for (NetworkInterfaceViewer viewer : viewers) {
            NetworkInterfaceIn nicIn = viewer.save();
            if (nicIn != null) {
               mIn.addNetworkInterface(nicIn);
            }
         }
      }
   }

}
