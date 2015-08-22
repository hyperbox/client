/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Maxime Dor
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

package io.kamax.hboxc.server;

import io.kamax.hbox.comm.io.NetServiceIO;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hbox.comm.out.network.NetAdaptorOut;
import io.kamax.hbox.comm.out.network.NetModeOut;
import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.exception.net.InvalidNetworkModeException;
import io.kamax.hbox.exception.net.NetworkAdaptorNotFoundException;
import io.kamax.hbox.hypervisor._MachineLogFile;
import java.util.List;

public interface _Hypervisor {

    public HypervisorOut getInfo();

    public String getServerId();

    public String getType();

    public String getVendor();

    public String getProduct();

    public String getVersion();

    public String getRevision();

    public boolean hasToolsMedium();

    public MediumOut getToolsMedium();

    /**
     * List all supported network modes for the adaptors
     *
     * @return List of network modes or empty list if none is found
     */
    public List<NetModeOut> listNetworkModes();

    public NetModeOut getNetworkMode(String id);

    /**
     * List Network adaptors accessible to the VMs
     *
     * @return List of network adaptors or empty list if none is found
     */
    public List<NetAdaptorOut> listAdaptors();

    /**
     * List all network adaptors for the given network mode
     *
     * @param modeId Network mode ID to match
     * @return List of network adaptor of the specified network mode, or empty list if none is found
     * @throws InvalidNetworkModeException If the netmork mode does not exist
     */
    public List<NetAdaptorOut> listAdaptors(String modeId) throws InvalidNetworkModeException;

    public NetAdaptorOut createAdaptor(String modeId, String name) throws InvalidNetworkModeException;

    public void removeAdaptor(String modeId, String adaptorId) throws InvalidNetworkModeException;

    public NetAdaptorOut getNetAdaptor(String modId, String adaptorId) throws NetworkAdaptorNotFoundException;

    public NetServiceIO getNetService(String modeId, String adaptorId, String svcTypeId) throws NetworkAdaptorNotFoundException;

    public List<String> getLogFileList(String vmId);

    public _MachineLogFile getLogFile(String vmId, String logid);

}
