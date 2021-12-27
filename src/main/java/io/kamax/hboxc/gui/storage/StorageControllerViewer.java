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

package io.kamax.hboxc.gui.storage;

import io.kamax.hbox.comm.in.StorageControllerIn;
import io.kamax.hbox.comm.in.StorageControllerTypeIn;
import io.kamax.hbox.comm.io.BooleanSettingIO;
import io.kamax.hbox.comm.out.storage.StorageControllerSubTypeOut;
import io.kamax.hbox.constant.StorageControllerAttribute;
import io.kamax.hboxc.gui.Gui;
import io.kamax.tools.logging.KxLog;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;

import javax.swing.*;
import java.lang.invoke.MethodHandles;

public final class StorageControllerViewer {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private StorageControllerIn scIn;

    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel typeLabel;
    private JLabel typeValue;
    private JLabel subTypeLabel;
    private JComboBox<String> subTypeBox;

    private JLabel ioCacheLabel;
    private JCheckBox ioCacheBox;
    private JPanel panel;

    public StorageControllerViewer() {
        init();
    }

    private void init() {
        nameLabel = new JLabel("Name");
        nameField = new JTextField();

        typeLabel = new JLabel("Type");
        typeValue = new JLabel();

        subTypeLabel = new JLabel("Sub Type");
        subTypeBox = new JComboBox<>();

        ioCacheLabel = new JLabel("Host I/O Cache");
        ioCacheBox = new JCheckBox();

        panel = new JPanel(new MigLayout("ins 0"));
        panel.add(nameLabel);
        panel.add(nameField, "growx,pushx,wrap");
        panel.add(typeLabel);
        panel.add(typeValue, "growx,pushx,wrap");
        panel.add(subTypeLabel);
        panel.add(subTypeBox, "growx,pushx,wrap");
        panel.add(ioCacheLabel);
        panel.add(ioCacheBox, "growx,pushx,wrap");
    }

    public JPanel getPanel() {
        return panel;
    }

    public void display(String srvId, StorageControllerIn scIn) {
        this.scIn = scIn;

        nameField.setEditable(true);

        String name = "";
        String scTypeId = "";
        String scSubTypeId = "";
        boolean ioCache = false;

        name = scIn.getName();
        scTypeId = scIn.getSetting(StorageControllerAttribute.Type).getString();

        if (scIn.hasSetting(StorageControllerAttribute.SubType)) {
            scSubTypeId = scIn.getSetting(StorageControllerAttribute.SubType).getString();
            log.debug("Selecting " + scSubTypeId + " subType");
        } else {
            log.debug("No SubType in the object");
        }

        if (scIn.hasSetting(StorageControllerAttribute.IoCache)) {
            ioCache = scIn.getSetting(StorageControllerAttribute.IoCache).getBoolean();
        } else {
            log.debug("No IoCache in the object");
        }

        subTypeBox.removeAllItems();
        for (StorageControllerSubTypeOut scstOut : Gui.getServer(srvId).listStorageControllerSubType(
                new StorageControllerTypeIn(scTypeId))) {
            subTypeBox.addItem(scstOut.getId());
        }

        nameField.setText(name);
        typeValue.setText(scTypeId);
        subTypeBox.setSelectedItem(scSubTypeId);
        ioCacheBox.setSelected(ioCache);
    }

    public void save() {
        scIn.setSubType(subTypeBox.getSelectedItem().toString());
        scIn.setSetting(new BooleanSettingIO(StorageControllerAttribute.IoCache, ioCacheBox.isSelected()));
    }

}
