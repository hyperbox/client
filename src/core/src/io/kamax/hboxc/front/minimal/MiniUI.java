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

package io.kamax.hboxc.front.minimal;

import io.kamax.hbox.comm._RequestReceiver;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.core._CoreReader;
import io.kamax.hboxc.front._Front;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * This class provides a minimal UI during load of the client, in case something breaks very early, even before the full UI is initialised.
 * 
 * @author max
 * 
 */
public final class MiniUI implements _Front {

   @Override
   public void start() throws HyperboxException {
      // stub
   }

   @Override
   public void stop() {
      // stub
   }

   @Override
   public void postError(String description, Throwable t) {
      System.out.println("Fatal error occured during startup: " + t.getMessage());
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         JOptionPane.showMessageDialog(null, "Fatal error occured during startup: " + t.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE);
      } catch (Throwable t1) {
         // we don't care, we just tried in case of
      }
   }

   @Override
   public void postError(Throwable t) {
      // stub
   }

   @Override
   public void postError(String s) {
      // stub
   }

   @Override
   public void postError(Throwable t, String s) {
      // stub
   }

   @Override
   public void setRequestReceiver(_RequestReceiver reqRec) {
      // stub
   }

   @Override
   public void setCoreReader(_CoreReader reader) {
      // stub
   }

}
