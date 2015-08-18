/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 - Maxime Dor
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

package io.kamax.hboxc.module;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hbox.exception.ModuleAlreadyRegisteredException;
import io.kamax.hbox.exception.ModuleException;
import io.kamax.hbox.exception.ModuleNotFoundException;
import io.kamax.hboxc.event.EventManager;
import io.kamax.tool.AxBooleans;
import io.kamax.tool.logging.Logger;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultModuleManager implements _ModuleManager {

   private boolean isStarted = false;
   private String[] baseDirs = new String[0];
   private Map<String, _Module> modules = new HashMap<String, _Module>();

   @Override
   public void start() {
      if (isStarted) {
         throw new HyperboxException("Module Manager is already started");
      }

      baseDirs = Configuration.getSetting(CFGKEY_MODULE_BASEPATH, CFGVAL_MODULE_BASEPATH).split(File.pathSeparator);
      refreshModules();
      isStarted = true;
      EventManager.register(this);
      Logger.verbose("Module Manager has started");
   }

   @Override
   public void stop() {
      EventManager.unregister(this);
      isStarted = false;
      baseDirs = new String[0];
      modules.clear();
      Logger.verbose("Module manager has stopped");
   }

   @Override
   public void refreshModules() {
      Logger.info("Refreshing modules...");

      Logger.debug("Number of base module directories: " + baseDirs.length);
      for (String baseDir : baseDirs) {
         File baseDirFile = new File(baseDir).getAbsoluteFile();
         Logger.info("Searching in " + baseDirFile.getAbsolutePath() + " for modules...");
         if (!baseDirFile.isDirectory() || !baseDirFile.canRead()) {
            Logger.warning("Unable to refresh modules for Base Directory " + baseDirFile + ": either not a directory or cannot be read");
            continue;
         }

         Logger.debug(baseDirFile.getAbsolutePath() + " is a readable directory, processing...");
         for (File file : baseDirFile.listFiles()) {
            if (isRegistered(file.getAbsolutePath())) {
               Logger.verbose(file.getAbsolutePath() + " is already registered for " + modules.get(file.getAbsolutePath()).getId());
               continue;
            }
            if (!file.isDirectory()) {
               continue;
            }
            if (!file.canRead()) {
               Logger.verbose(file.getAbsolutePath() + " is not readable, skipping.");
               continue;
            }
            Logger.verbose("Module detected: " + file.getAbsolutePath());
            registerModule(file.getAbsolutePath());
         }
      }
      Logger.info("Finished refreshing modules.");
   }

   @Override
   public void setModuleBasedir(String... basedir) {
      baseDirs = basedir;
      refreshModules();
   }

   @Override
   public Set<_Module> listModules() {
      return new HashSet<_Module>(modules.values());
   }

   @Override
   public _Module getModule(String moduleId) {
      if (!modules.containsKey(moduleId)) {
         throw new ModuleNotFoundException(moduleId);
      }

      return modules.get(moduleId);
   }

   @Override
   public _Module registerModule(String path) {
      _Module mod = new DefaultModule(path);

      if (isRegistered(mod.getId()) || isRegistered(mod.getLocation())) {
         throw new ModuleAlreadyRegisteredException(mod.getId());
      }

      modules.put(mod.getId(), mod);
      modules.put(mod.getLocation(), mod);

      if (AxBooleans.get(Configuration.getSetting(CFGKEY_MODULE_AUTOLOAD, CFGVAL_MODULE_AUTOLOAD))) {
         try {
            mod.load();
            Logger.info("Module ID " + mod.getId() + " (" + mod.getId() + ") was autoloaded");
         } catch (ModuleException e) {
            Logger.warning("Module ID " + mod.getId() + " (" + mod.getId() + ") failed to autoload: " + e.getMessage());
         }
      }

      return mod;
   }

   @Override
   public boolean isRegistered(String id) {
      return modules.containsKey(id);
   }

}
