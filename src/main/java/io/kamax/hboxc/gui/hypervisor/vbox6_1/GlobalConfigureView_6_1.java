/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2018 Kamax Sarl
 *
 * https://kamax.io/hbox/
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

package io.kamax.hboxc.gui.hypervisor.vbox6_1;

import io.kamax.hbox.comm.in.HypervisorIn;
import io.kamax.hbox.comm.io.BooleanSettingIO;
import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hboxc.gui.hypervisor.vbox.GlobalConfigureView;

import javax.swing.*;

public class GlobalConfigureView_6_1 extends GlobalConfigureView {

    private JLabel virtExLabel;
    private JCheckBox virtExValue;

    public GlobalConfigureView_6_1() {
        virtExLabel = new JLabel("Hardware Virtualization Exclusive");
        virtExValue = new JCheckBox();

        getComponent().add(virtExLabel);
        getComponent().add(virtExValue, "growx, pushx, wrap");
    }

    @Override
    public void update(HypervisorOut hypOut) {
        super.update(hypOut);
        virtExValue.setSelected(hypOut.hasSetting("vbox.global.virtEx") ? hypOut.getSetting("vbox.global.virtEx").getBoolean() : false);
    }

    @Override
    public HypervisorIn getUserInput() {
        HypervisorIn hypIn = super.getUserInput();
        hypIn.setSetting(new BooleanSettingIO("vbox.global.virtEx", virtExValue.isSelected()));
        return hypIn;
    }

}
