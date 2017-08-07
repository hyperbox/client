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

package io.kamax.hboxc.gui.storage;

import io.kamax.hbox.comm.in.MediumIn;
import io.kamax.hbox.comm.in.StoreItemIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hbox.constant.HardDiskFormat;
import io.kamax.hboxc.gui.MainView;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.store.utils.StoreItemChooser;
import io.kamax.tools.logging.Logger;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HarddiskCreateDialog implements _Saveable, _Cancelable {

    private static final String MB = "MB";
    private static final String GB = "GB";

    private ServerOut srvOut;

    private JDialog dialog;

    private JLabel locationLabel;
    private JTextField locationField;
    private JButton locationButton;

    private JLabel sizeLabel;
    private JTextField sizeField;
    private JComboBox<String> sizeUnit;

    private JLabel formatLabel;
    private JComboBox<HardDiskFormat> formatBox;

    private JButton saveButton;
    private JButton cancelButton;

    private MediumIn medIn;

    private class BrowseAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            StoreItemIn stiIn = StoreItemChooser.getFilename(srvOut.getId());
            if (stiIn != null) {
                locationField.setText(stiIn.getPath());
                sizeField.requestFocus();
            }
        }

    }

    private HarddiskCreateDialog(ServerOut srvOut) {
        this.srvOut = srvOut;
        locationLabel = new JLabel("Location");
        locationField = new JTextField(50);
        locationButton = new JButton("Browse...");
        locationButton.addActionListener(new BrowseAction());

        sizeLabel = new JLabel("Size");
        sizeField = new JTextField();
        sizeUnit = new JComboBox<>();
        sizeUnit.addItem(GB);
        sizeUnit.addItem(MB);

        formatLabel = new JLabel("Format");
        formatBox = new JComboBox<>();
        for (HardDiskFormat format : HardDiskFormat.values()) {
            formatBox.addItem(format);
        }

        // TODO add provisionning type

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));

        dialog = new JDialog(MainView.getMainFrame());
        dialog.setTitle("Create Harddisk");
        dialog.setModalityType(ModalityType.DOCUMENT_MODAL);

        JPanel content = new JPanel(new MigLayout());
        content.add(locationLabel);
        content.add(locationField, "growx,pushx");
        content.add(locationButton, "wrap");

        content.add(sizeLabel);
        content.add(sizeField, "growx,pushx");
        content.add(sizeUnit, "wrap");

        content.add(formatLabel);
        content.add(formatBox, "growx,pushx,span 2,wrap");

        content.add(saveButton);
        content.add(cancelButton);

        dialog.add(content);
    }

    /**
     * Get a new Medium info from the user
     *
     * @param srvOut the server
     * @return MediumInput object if user has entered valid data, or <code>null</code> if the user cancelled.
     */
    public static MediumIn show(ServerOut srvOut) {

        HarddiskCreateDialog diskCreateDialog = new HarddiskCreateDialog(srvOut);
        MediumIn medIn = diskCreateDialog.getUserInput();
        return medIn;
    }

    private MediumIn getUserInput() {

        dialog.pack();
        dialog.setLocationRelativeTo(MainView.getMainFrame());
        dialog.setVisible(true);
        Logger.debug("User input : " + medIn);
        return medIn;
    }

    @Override
    public void cancel() {

        medIn = null;
        dialog.setVisible(false);
        dialog.dispose();
    }

    @Override
    public void save() {

        String path = locationField.getText();
        Long size = Long.parseLong(sizeField.getText());
        if (sizeUnit.getSelectedItem().toString().equalsIgnoreCase(MB)) {
            size = size * 1048576;
        }
        if (sizeUnit.getSelectedItem().toString().equalsIgnoreCase(GB)) {
            size = size * 1073741824;
        }
        String format = formatBox.getSelectedItem().toString();

        medIn = new MediumIn();
        medIn.setDeviceType(EntityType.HardDisk.getId());
        medIn.setLocation(path);
        medIn.setFormat(format);
        medIn.setLogicalSize(size);

        dialog.setVisible(false);
    }

}
