/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

package io.kamax.hboxc.gui.hypervisor.vbox;

import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hbox.hypervisor.vbox.VBoxNetMode;
import io.kamax.hbox.hypervisor.vbox.VirtualBox;
import io.kamax.hboxc.gui.hypervisor._GlobalConfigureView;
import io.kamax.hboxc.gui.hypervisor._HypervisorModel;
import io.kamax.hboxc.gui.hypervisor._NetAdaptorConfigureView;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class GenericModel implements _HypervisorModel {

    @Override
    public List<String> getSupported() {
        return Arrays.asList(VirtualBox.VBOX_ID);
    }

    @Override
    public _GlobalConfigureView getConfigureView() {
        return new GlobalConfigureView();
    }

    @Override
    public _NetAdaptorConfigureView getNetAdaptorConfig(String srvId, String modeId, String adaptId) {
        if (VBoxNetMode.HostOnly.is(modeId)) {
            return new HostOnlyNicEditor(srvId, modeId, adaptId);
        } else if (VBoxNetMode.NATNetwork.is(modeId)) {
            return new NATNetworkNicEditor(srvId, modeId, adaptId);
        } else {
            throw new HyperboxException(modeId + " is not supported in GUI");
        }

    }

    @Override
    public ListCellRenderer getTypeRenderer(ListCellRenderer parent) {
        return new HypervisorTypeRenderer(parent);
    }

}
