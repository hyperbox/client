/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
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

package io.kamax.hboxc.gui.session;

import io.kamax.hbox.comm.out.SessionOut;
import io.kamax.hboxc.gui.utils.AbstractOutputListTableModel;


public final class SessionListTableModel extends AbstractOutputListTableModel<SessionOut> {

    private static final long serialVersionUID = -7048437106115357750L;
    private final String ID = "ID";
    private final String USER = "User";
    private final String STATE = "State";
    private final String C_TIME = "Create Time";

    @Override
    protected void addColumns() {
        addColumn(ID);
        addColumn(USER);
        addColumn(STATE);
        addColumn(C_TIME);
    }

    @Override
    protected Object getValueAt(SessionOut sesOut, String columnName) {
        if (columnName == ID) {
            return sesOut.getId();
        }
        if (columnName == USER) {
            return sesOut.getUser().getDomainLogonName();
        }
        if (columnName == STATE) {
            return sesOut.getState();
        }
        if (columnName == C_TIME) {
            return sesOut.getCreateTime().toString();
        }

        return null;
    }

}
