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

package io.kamax.hboxc.comm.io.factory;

import io.kamax.hbox.comm.io.BooleanSettingIO;
import io.kamax.hbox.comm.io.SettingIO;
import io.kamax.hbox.comm.io.StringSettingIO;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.constant.ServerAttribute;
import io.kamax.hboxc.server._Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerIoFactory {

    public static ServerOut get(_Server srv) {
        List<SettingIO> settings = new ArrayList<SettingIO>();
        settings.add(new StringSettingIO(ServerAttribute.Name, srv.getName()));
        settings.add(new StringSettingIO(ServerAttribute.Type, srv.getType()));
        settings.add(new StringSettingIO(ServerAttribute.Version, srv.getVersion()));
        settings.add(new BooleanSettingIO(ServerAttribute.IsHypervisorConnected, srv.isHypervisorConnected()));
        settings.add(new StringSettingIO(ServerAttribute.NetProtocolVersion, srv.getProtocolVersion()));
        settings.add(new StringSettingIO(ServerAttribute.LogLevel, srv.getLogLevel()));
        ServerOut srvOut = new ServerOut(srv.getId(), settings);
        return srvOut;
    }

    public static List<ServerOut> getList(Collection<_Server> objList) {
        List<ServerOut> listOut = new ArrayList<ServerOut>();
        for (_Server obj : objList) {
            listOut.add(get(obj));
        }
        return listOut;
    }

}
