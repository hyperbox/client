/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 Max Dor
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

package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.out.security.UserOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._UserListReceiver;

import java.util.List;

public class UserListWorker extends AxSwingWorker<_UserListReceiver, Void, UserOut> {

    private String srvId;

    public UserListWorker(_UserListReceiver recv, String srvId) {
        super(recv);
        this.srvId = srvId;
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (UserOut usrOut : Gui.getServer(srvId).listUsers()) {
            publish(usrOut);
        }

        return null;
    }

    @Override
    protected void process(List<UserOut> usrOutList) {
        getReceiver().add(usrOutList);
    }

    public static void execute(_UserListReceiver recv, String srvId) {
        (new UserListWorker(recv, srvId)).execute();
    }

}
