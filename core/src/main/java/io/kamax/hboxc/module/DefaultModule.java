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

import io.kamax.hbox.ClassManager;
import io.kamax.hbox.exception.ModuleAlreadyLoadedException;
import io.kamax.hbox.exception.ModuleException;
import io.kamax.hboxc.exception.ClientModuleException;

import java.io.File;
import java.net.URL;
import java.util.Set;

public class DefaultModule implements _Module {

    private String id;
    private String base;
    private ModuleClassLoader loader;

    public DefaultModule(String path) {
        this.base = path;
        this.id = (new File(path)).getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocation() {
        return base;
    }

    @Override
    public Set<URL> getRessources() {
        if (!isReady()) {
            throw new ModuleException("Module must be enabled and loaded before retrieving list of ressources");
        }

        return loader.getRessources();
    }

    @Override
    public void load() throws ClientModuleException {
        if (isLoaded()) {
            throw new ModuleAlreadyLoadedException(getId());
        }

        loader = new ModuleClassLoader();
        loader.load(getLocation());

        ClassManager.reload(getRessources(), loader.getClassLoader());
    }

    @Override
    public boolean isLoaded() {
        return loader != null;
    }

    @Override
    public boolean isReady() {
        return isLoaded();
    }

}
