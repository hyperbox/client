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

package io.kamax.hboxc.core.console.viewer;

import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.comm.io.factory.ConsoleViewerIoFactory;
import io.kamax.hboxc.core._ConsoleViewer;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.consoleviewer.ConsoleViewerModifiedEvent;

import java.io.File;

public class ConsoleViewer implements _ConsoleViewer {

    private String id;
    private String hypervisorId;
    private String moduleId;
    private File viewerPath;
    private String args;

    public ConsoleViewer(String id, String hypervisorId, String moduleId, String viewerPath, String args) {
        this.id = id;
        this.hypervisorId = hypervisorId;
        this.moduleId = moduleId;
        this.viewerPath = new File(viewerPath);
        this.args = args;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHypervisorId() {
        return hypervisorId;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public String getViewerPath() {
        return viewerPath.getAbsolutePath();
    }

    @Override
    public String getArgs() {
        return args;
    }

    @Override
    public void setHypervisorId(String hypervisorId) {
        this.hypervisorId = hypervisorId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public void setViewer(String viewerPath) {
        this.viewerPath = new File(viewerPath);
    }

    @Override
    public void setArgs(String args) {
        this.args = args;
    }

    @Override
    public void remove() {
        // nothing to do for now
    }

    @Override
    public void save() {
        if (!viewerPath.exists()) {
            throw new HyperboxException(viewerPath + " does not exist");
        }
        if (!viewerPath.isAbsolute()) {
            throw new HyperboxException(viewerPath + " is not an absolute path");
        }
        if (!viewerPath.isFile()) {
            throw new HyperboxException(viewerPath + " is not a file");
        }

        EventManager.get().post(new ConsoleViewerModifiedEvent(ConsoleViewerIoFactory.getOut(this)));
    }

}
