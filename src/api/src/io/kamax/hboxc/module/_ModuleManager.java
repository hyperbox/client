/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Maxime Dor
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

import java.util.Set;

public interface _ModuleManager {

   /**
    * Configuration Key for Module Auto-loading.
    */
   String CFGKEY_MODULE_AUTOLOAD = "client.module.autoload";
   /**
    * Default value for {@value #CFGKEY_MODULE_AUTOLOAD}
    */
   String CFGVAL_MODULE_AUTOLOAD = "1";

   /**
    * <p>
    * Configuration Key for Module search directory.
    * </p>
    * If a relative path is given, it will be from the installation directory of Hyperbox
    */
   String CFGKEY_MODULE_BASEPATH = "client.module.dir";
   /**
    * Default value for {@value #CFGKEY_MODULE_BASEPATH}
    */
   String CFGVAL_MODULE_BASEPATH = "modules";

   /**
    * Start the module manager.
    * <p>
    * If supported, the Module Manager will also refresh the modules in the base directories or fail silently. If Auto-load is supported, modules
    * found during the refresh will be loaded.
    * </p>
    */
   public void start();

   /**
    * Stop the module manager.
    * <p>
    * All loaded modules will be unloaded and any references to them cleared.
    * </p>
    */
   public void stop();

   /**
    * Scan the base directories for new modules. Does not affected already loaded modules.
    */
   public void refreshModules();

   /**
    * Change the base directories for modules.
    * <p>
    * Modules will be refreshed using {{@link #refreshModules()} if the manager is started.
    * </p>
    * 
    * @param basedir The new base directories for module search
    */
   public void setModuleBasedir(String... basedir);

   /**
    * List all loaded modules.
    * 
    * @return a Set of loaded modules
    */
   public Set<_Module> listModules();

   /**
    * Get the module information given its ID.
    * 
    * @param moduleId The Module ID
    * @return the Module object or null if no such module is loaded
    */
   public _Module getModule(String moduleId);

   /**
    * Load a module given its descriptor file path.
    * 
    * @param path Relative path to the base directories or an absolute path.
    * @return The loaded module or null if no module was found at the given path.
    */
   public _Module registerModule(String path);

   /**
    * Checks if the module ID or Descriptor File path is already registered.
    * 
    * @param id Module ID or path.
    * @return true if this ID/path represents a registered module, false if not.
    */
   public boolean isRegistered(String id);

}
