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

package io.kamax.hboxc.core.storage;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.core._ConsoleViewer;
import io.kamax.hboxc.core.connector._Connector;

import java.util.Collection;

public interface _CoreStorage {

    public void init() throws HyperboxException;

    public void start() throws HyperboxException;

    public void stop();

    public void destroy();

    public boolean hasConsoleViewers();

    public void storeViewers(Collection<_ConsoleViewer> viewers);

    public Collection<_ConsoleViewer> loadViewers();

    public boolean hasConnectors();

    public void storeConnectors(Collection<_Connector> servers);

    public Collection<_Connector> loadConnectors();

    public void storeConnectorCredentials(String id, UserIn usrIn);

    public UserIn loadConnectorCredentials(String id);

    public void removeConnectorCredentials(String id);

}
