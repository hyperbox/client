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

package io.kamax.hboxc.server;

import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hboxc.state.ConnectionState;

public interface _Server extends _ServerReader, _ServerWriter {

    String CFGKEY_SERVER_VALIDATE = "server.validate";
    String CFGVAL_SERVER_VALIDATE = "1";
    String CFGKEY_SERVER_VALIDATE_VERSION = "server.validate.version";
    String CFGVAL_SERVER_VALIDATE_VERSION = "1";

    public ConnectionState getState();

    public void connect(String address, String backendId, UserIn usrIn);

    public void disconnect();

    public boolean isConnected();

}
