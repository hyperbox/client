/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.security.user;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.gui._Cancelable;
import io.kamax.hboxc.gui._Saveable;
import io.kamax.hboxc.gui.action.CancelAction;
import io.kamax.hboxc.gui.action.SaveAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.security.perm.UserPermissionEditor;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class UserEditor implements _Saveable, _Cancelable {

   private UserPermissionEditor permEditor;

   private JLabel domainLabel;
   private JTextField domainValue;
   private JLabel usernameLabel;
   private JTextField usernameValue;
   private JLabel firstPassLabel;
   private JPasswordField firstPassValue;
   private JLabel secondPassLabel;
   private JPasswordField secondPassValue;

   private JPanel buttonPanel;
   private JButton saveButton;
   private JButton cancelButton;

   private JDialog dialog;

   private UserIn usrIn;
   private UserOut usrOut;

   public UserEditor() {
      permEditor = new UserPermissionEditor();

      domainValue = new JTextField();
      domainLabel = new JLabel("Domain");
      domainLabel.setLabelFor(domainValue);

      usernameValue = new JTextField();
      usernameLabel = new JLabel("Username");
      usernameLabel.setLabelFor(usernameValue);

      firstPassValue = new JPasswordField();
      firstPassLabel = new JLabel("Enter New Password");
      firstPassLabel.setLabelFor(firstPassValue);

      secondPassValue = new JPasswordField();
      secondPassLabel = new JLabel("Confirm New Password");
      secondPassLabel.setLabelFor(secondPassValue);

      saveButton = new JButton(new SaveAction(this));
      cancelButton = new JButton(new CancelAction(this));

      buttonPanel = new JPanel(new MigLayout("ins 0"));
      buttonPanel.add(saveButton);
      buttonPanel.add(cancelButton);

      dialog = JDialogBuilder.get(saveButton);
      dialog.add(usernameLabel);
      dialog.add(usernameValue, "growx, pushx, wrap");
      dialog.add(firstPassLabel);
      dialog.add(firstPassValue, "growx, pushx, wrap");
      dialog.add(secondPassLabel);
      dialog.add(secondPassValue, "growx, pushx, wrap");
      dialog.add(permEditor.getComponent(), "hidemode 3,span 2, growx, pushx, wrap");
      dialog.add(buttonPanel, "span 2, center, bottom");
   }

   public UserIn create() {

      dialog.setTitle("Create new User");
      permEditor.getComponent().setVisible(false);
      show();
      return usrIn;
   }

   public UserIn edit(String serverId, UserOut usrOut) {

      dialog.setTitle("Editing user " + usrOut.getDomainLogonName());
      this.usrOut = usrOut;

      domainValue.setText(usrOut.getDomain());
      usernameValue.setText(usrOut.getUsername());

      permEditor.show(serverId, usrOut);

      show();
      return usrIn;
   }

   public static UserIn getInput() {

      return new UserEditor().create();
   }

   public static UserIn getInput(String serverId, UserOut usrOut) {

      return new UserEditor().edit(serverId, usrOut);
   }

   private void show() {

      dialog.pack();
      dialog.setSize(375, dialog.getHeight());
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

      if (usrOut != null) {
         usrIn = new UserIn(usrOut.getId());
         permEditor.save();
      } else {
         usrIn = new UserIn();

         if (!domainValue.getText().isEmpty()) {
            usrIn.setDomain(domainValue.getText());
         }

         if ((firstPassValue.getPassword().length == 0) || (secondPassValue.getPassword().length == 0)) {
            throw new HyperboxException("Password cannot be empty");
         }
      }

      if (usernameValue.getText().isEmpty()) {
         throw new HyperboxException("Username cannot be empty");
      }
      usrIn.setUsername(usernameValue.getText());

      if ((firstPassValue.getPassword().length > 0) || (secondPassValue.getPassword().length > 0)) {
         if (!Arrays.equals(firstPassValue.getPassword(), secondPassValue.getPassword())) {
            throw new HyperboxException("Password do not match");
         }
         usrIn.setPassword(firstPassValue.getPassword());
      }

      hide();
   }

}
