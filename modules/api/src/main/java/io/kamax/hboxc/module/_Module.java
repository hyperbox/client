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

import io.kamax.hboxc.exception.ClientModuleException;

import java.net.URL;
import java.util.Set;

/**
 * A Module allows to provide custom services to Hyperbox like extra Hypervisors connectors, new Store backends, etc.
 * <p>
 * A Module is implemented into a Descriptor File which is an XML file located within the Server Modules Directory.<br/>
 * It can provide any kind of service, referring to them via the interface(s) that these implements.<br/>
 * The Descriptor file will be picked up by the {@link _ModuleManager}. Module ID and Descriptor file path are linked and are both considered valid
 * unique identifiers, so it wouldn't be possible to have two modules registered under different IDs with the same Descriptor file or the other way
 * around.
 * </p>
 * <p>
 * A typical descriptor file will look like this:
 * <p>
 * <pre>
 * &lt;module&gt;
 *    &lt;id&gt;vbox-4.3-xpcom&lt;/id&gt;
 *    &lt;path&gt;vbox-4.3-xpcom&lt;/path&gt;
 *    &lt;name&gt;VirtualBox 4.3 XPCOM Connector&lt;/name&gt;
 *    &lt;desc&gt;This module provides the UNIX-based XPCOM connector for VirtualBox 4.3.&lt;/desc&gt;
 *    &lt;version&gt;1&lt;/version&gt;
 *    &lt;vendor&gt;Altherian&lt;/vendor&gt;
 *    &lt;url&gt;http://kamax.io/hbox/&lt;/url&gt;
 *    &lt;providers&gt;
 *       &lt;provider&gt;
 *          &lt;type&gt;io.kamax.hboxd.hypervisor._Hypervisor&lt;/type&gt;
 *          &lt;impl&gt;io.kamax.hboxd.vbox4_3.xpcom.VBoxXpcomHypervisor&lt;/impl&gt;
 *       &lt;/provider&gt;
 *    &lt;/providers&gt;
 * &lt;/module&gt;
 *
 * </pre>
 * <p>
 * See the template in the sources for a more detail description of each possible element.<br/>
 * </p>
 * <p>
 * A Module can either be enabled or disabled, and loaded or not. These states can only flow this way :
 * <ul>
 * <li>Disabled & Unloaded -> Enabled & Unloaded</li>
 * <li>Enabled & Unloaded -> Enabled & Loaded</li>
 * <li>Enabled & Loaded -> Enabled & Unloaded</li>
 * <li>Enabled & Unloaded -> Disabled & Unloaded</li>
 * </ul>
 * </p>
 *
 * @author max
 */
public interface _Module {

    /**
     * Return this module's ID.
     *
     * @return a String uniquely identifying the module.
     */
    public String getId();

    /**
     * Return this module location, either base path or single file.
     *
     * @return The absolute path to this module file(s)..
     */
    public String getLocation();

    /**
     * Return the resources that compose this module.
     *
     * @return {@link Set} of {@link URL} pointing to the Java code for this module.
     */
    public Set<URL> getRessources();

    /**
     * Load this module, generating the provider type's classes and the provider's classes.
     *
     * @throws ClientModuleException If an error occurred during the load process.
     */
    public void load() throws ClientModuleException;

    /**
     * Indicates if the module is loaded.
     *
     * @return true if the module is loaded, else false.
     */
    public boolean isLoaded();

    /**
     * Indicates if the module is loaded and ready for usage.
     *
     * @return true if the module is ready, else false.
     */
    public boolean isReady();

}
