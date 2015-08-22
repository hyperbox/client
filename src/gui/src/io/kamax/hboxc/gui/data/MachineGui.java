/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Maxime Dor
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

package io.kamax.hboxc.gui.data;

import io.kamax.hbox.comm.out.hypervisor.MachineOut;
import io.kamax.hbox.states.MachineStates;
import io.kamax.tool.AxStrings;

public class MachineGui extends EntityGui {

    private boolean isAvailable = false;
    private String name;
    private String state;

    private SnapshotGui currSnap;

    public MachineGui(MachineOut mOut) {
        super(mOut.getEntityTypeId(), mOut.getId());
        isAvailable = mOut.isAvailable();

        if (isAvailable) {
            name = mOut.getName();
            state = mOut.getState();
        } else {
            name = "<Unavailable>";
            state = MachineStates.Inaccessible.getId();
        }
    }

    public MachineGui(MachineOut mOut, SnapshotGui snapGui) {
        this(mOut);
        setCurrentSnapshot(snapGui);
    }

    @Override
    public String toString() {
        StringBuilder label = new StringBuilder();
        label.append(AxStrings.isEmpty(getName()) ? getId() : getName());
        if (isAvailable && (currSnap != null)) {
            label.append(" (" + currSnap + ")");
        }
        return label.toString();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the currSnap
     */
    public SnapshotGui getCurrentSnapshot() {
        return currSnap;
    }

    /**
     * @param currSnap the currSnap to set
     */
    public void setCurrentSnapshot(SnapshotGui currSnap) {
        this.currSnap = currSnap;
    }

}
