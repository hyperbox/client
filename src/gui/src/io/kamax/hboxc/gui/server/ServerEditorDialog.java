/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.gui.server;

import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class ServerEditorDialog implements _Saveable, _Cancelable {

    private JLabel nameLabel;
    private JTextField nameValue;
    private JLabel logLevelLabel;
    private JComboBox logLevelValue;

    private JButton saveButton;
    private JButton cancelButton;

    private JDialog dialog;

    private ServerIn srvIn;
    private ServerOut srvOut;

    public static ServerIn getInput(String srvId) {

        return new ServerEditorDialog().getUserInput(srvId);
    }

    public ServerEditorDialog() {
        nameValue = new JTextField();
        nameLabel = new JLabel("Name");
        nameLabel.setLabelFor(nameValue);

        logLevelValue = new JComboBox();
        logLevelLabel = new JLabel("Log level");
        logLevelLabel.setLabelFor(logLevelValue);

        saveButton = new JButton(new SaveAction(this));
        cancelButton = new JButton(new CancelAction(this));
        JPanel buttonPanel = new JPanel(new MigLayout("ins 0"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog = JDialogBuilder.get("Server Configuration", IconBuilder.getEntityType(EntityType.Server).getImage(), saveButton);
        dialog.add(nameLabel);
        dialog.add(nameValue, "growx, pushx, wrap");
        dialog.add(logLevelLabel);
        dialog.add(logLevelValue, "growx, pushx, wrap");
        dialog.add(buttonPanel, "center, span 2");
    }

    private ServerIn getUserInput(String srvId) {

        srvOut = Gui.getServerInfo(srvId);
        logLevelValue.addItem("");
        for (String level : Gui.getServer(srvId).listLogLevel()) {
            logLevelValue.addItem(level);
        }

        nameValue.setText(srvOut.getName());
        logLevelValue.setSelectedItem(srvOut.getLogLevel());

        show();
        return srvIn;
    }

    private void show() {
        dialog.pack();
        dialog.setSize(650, dialog.getHeight());
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    private void hide() {
        dialog.setVisible(false);
    }

    @Override
    public void cancel() {

        srvIn = null;
        hide();
    }

    @Override
    public void save() {

        srvIn = new ServerIn(srvOut.getId());
        srvIn.setName(nameValue.getText());
        srvIn.setLogLevel(logLevelValue.getSelectedItem().toString());
        hide();
    }

}
