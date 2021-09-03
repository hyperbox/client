/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.store;

import io.kamax.hbox.comm.in.StoreIn;
import io.kamax.hbox.comm.out.StoreItemOut;
import io.kamax.hbox.comm.out.StoreOut;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.store.utils.StoreItemChooser;
import io.kamax.hboxc.gui.utils.CancelableUtils;
import io.kamax.tools.AxStrings;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;

public class StoreEditor implements _Saveable, _Cancelable {

    private JLabel storeLabel;
    private JTextField storeLabelValue;
    private JLabel storeLocLabel;
    private JTextField storeLocValue;
    private JButton browseButton;
    private JLabel storeTypeLabel;
    private JComboBox<String> storeTypeBox;

    private JButton saveButton;
    private JButton cancelButton;

    private JDialog dialog;

    private String srvId;
    private StoreIn stoIn;
    private StoreOut stoOut;


    private class BrowseAction extends AbstractAction {

        private static final long serialVersionUID = -7633225674262983219L;

        public BrowseAction() {
            super("Browse");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            StoreItemOut stiOut = StoreItemChooser.getExisitingFolder(srvId);
            if (stiOut != null) {
                storeLocValue.setText(stiOut.getPath());
                storeLocValue.requestFocus();
            }
        }

    }

    private class EmptyValueListener implements DocumentListener {

        private void validate() {
            saveButton.setEnabled(!AxStrings.isEmpty(storeLabelValue.getText()) && !AxStrings.isEmpty(storeLocValue.getText()));
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            validate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validate();
        }

    }

    public StoreEditor(String srvId) {

        this.srvId = srvId;

        saveButton = new JButton(new SaveAction(this));
        saveButton.setEnabled(false);
        cancelButton = new JButton(new CancelAction(this));
        JPanel buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        storeLabel = new JLabel("Label");
        storeLocLabel = new JLabel("Location");

        storeLabelValue = new JTextField();
        storeLabelValue.getDocument().addDocumentListener(new EmptyValueListener());
        storeLocValue = new JTextField();
        storeLocValue.getDocument().addDocumentListener(new EmptyValueListener());
        browseButton = new JButton(new BrowseAction());
        storeTypeLabel = new JLabel("Type");
        storeTypeBox = new JComboBox<>();
        // TODO retrieve full list of supported store types.
        storeTypeBox.addItem("Native Folder");

        dialog = JDialogBuilder.get(saveButton);
        dialog.add(storeLabel);
        dialog.add(storeLabelValue, "growx, pushx, span 2, wrap");
        dialog.add(storeTypeLabel);
        dialog.add(storeTypeBox, "growx, pushx, span 2, wrap");
        dialog.add(storeLocLabel);
        dialog.add(storeLocValue, "growx, pushx");
        dialog.add(browseButton, "wrap");
        dialog.add(buttonPanel, "center, span 3");
        CancelableUtils.set(this, dialog.getRootPane());
    }

    public StoreIn create() {

        dialog.setTitle("Create new Store");
        show();
        return stoIn;
    }

    public StoreIn register() {

        dialog.setTitle("Registering new Store");
        show();
        return stoIn;
    }

    public StoreIn edit(StoreOut stoOut) {

        this.stoOut = stoOut;
        dialog.setTitle("Edit store " + stoOut.getLabel());

        storeLabelValue.setText(stoOut.getLabel());
        storeLocValue.setText(stoOut.getLocation());

        show();
        return stoIn;
    }

    public static StoreIn getInputCreate(String srvId) {
        return new StoreEditor(srvId).create();
    }

    public static StoreIn getInputRegister(String srvId) {
        return new StoreEditor(srvId).register();
    }

    public static StoreIn getInputEdit(String srvId, StoreOut stoOut) {
        return new StoreEditor(srvId).edit(stoOut);

    }

    private void show() {

        dialog.pack();
        dialog.setSize(323, dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    private void hide() {

        dialog.setVisible(false);
    }

    @Override
    public void cancel() {

        hide();
    }

    @Override
    public void save() {

        if (stoOut != null) {
            stoIn = new StoreIn(stoOut.getId());
        } else {
            stoIn = new StoreIn();
        }
        stoIn.setLabel(storeLabelValue.getText());
        stoIn.setLocation(storeLocValue.getText());
        hide();
    }

}
