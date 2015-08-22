/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
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

package io.kamax.hboxc.server;

import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm.RequestProcessType;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.SessionIn;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.comm.out.SessionOut;
import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hboxc.comm.utils.Transaction;
import io.kamax.hboxc.exception.ServerDisconnectedException;

public interface _ServerWriter {

    public Transaction sendRequest(Request req) throws ServerDisconnectedException;

    public Transaction sendRequest(Request req, RequestProcessType type) throws ServerDisconnectedException;

    public MachineOut createMachine(MachineIn mIn);

    public MachineOut registerMachine(MachineIn mIn);

    public MachineOut modifyMachine(MachineIn mIn);

    public MachineOut unregisterMachine(MachineIn mIn);

    public MachineOut deleteMachine(MachineIn mIn);

    public void startMachine(MachineIn mIn);

    public void stopMachine(MachineIn mIn);

    public void acpiPowerMachine(MachineIn mIn);

    public UserOut addUser(UserIn uIn);

    public UserOut modifyUser(UserIn uIn);

    public UserOut deleteUser(UserIn uIn);

    public SessionOut closeSession(SessionIn sIn);

}
