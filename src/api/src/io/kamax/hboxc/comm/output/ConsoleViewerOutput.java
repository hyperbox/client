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

package io.kamax.hboxc.comm.output;

import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.ObjectOut;
import io.kamax.hboxc.constant.ClientEntity;
import io.kamax.hboxc.constant.ConsoleViewerAttributes;
import io.kamax.hboxc.core._ConsoleViewerReader;

public class ConsoleViewerOutput extends ObjectOut implements _ConsoleViewerReader {

    public ConsoleViewerOutput(String id, String hypervisorId, String moduleId, String viewerPath, String args) {
        super(ClientEntity.ConsoleViewer, id);
        setSetting(new StringSettingIO(ConsoleViewerAttributes.HypervisorTypeId, hypervisorId));
        setSetting(new StringSettingIO(ConsoleViewerAttributes.ModuleId, moduleId));
        setSetting(new StringSettingIO(ConsoleViewerAttributes.ViewerPath, viewerPath));
        setSetting(new StringSettingIO(ConsoleViewerAttributes.Args, args));
    }

    @Override
    public String getHypervisorId() {
        return getSetting(ConsoleViewerAttributes.HypervisorTypeId).getString();
    }

    @Override
    public String getModuleId() {
        return getSetting(ConsoleViewerAttributes.ModuleId).getString();
    }

    @Override
    public String getViewerPath() {
        return getSetting(ConsoleViewerAttributes.ViewerPath).getString();
    }

    @Override
    public String getArgs() {
        return getSetting(ConsoleViewerAttributes.Args).getString();
    }

}
