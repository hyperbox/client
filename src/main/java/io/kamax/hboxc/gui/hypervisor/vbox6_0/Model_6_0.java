/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2018 Kamax Sarl
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

package io.kamax.hboxc.gui.hypervisor.vbox6_0;

import io.kamax.hbox.hypervisor.vbox.VBoxNetMode;
import io.kamax.hbox.hypervisor.vbox.VirtualBox;
import io.kamax.hboxc.gui.hypervisor._GlobalConfigureView;
import io.kamax.hboxc.gui.hypervisor._NetAdaptorConfigureView;
import io.kamax.hboxc.gui.hypervisor.vbox.GenericModel;
import io.kamax.hboxc.gui.hypervisor.vbox.NATNetworkNicEditor;

import java.util.List;

public class Model_6_0 extends GenericModel {

    @Override
    public List<String> getSupported() {
        return VirtualBox.ID_GROUP.ALL_6_0;
    }

    @Override
    public _GlobalConfigureView getConfigureView() {
        return new GlobalConfigureView_6_0();
    }

    @Override
    public _NetAdaptorConfigureView getNetAdaptorConfig(String srvId, String modeId, String adaptId) {
        if (VBoxNetMode.NATNetwork.is(modeId)) {
            return new NATNetworkNicEditor(srvId, modeId, adaptId);
        } else {
            return super.getNetAdaptorConfig(srvId, modeId, adaptId);
        }
    }

}
