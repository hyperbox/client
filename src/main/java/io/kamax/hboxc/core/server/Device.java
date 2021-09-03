/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2014 Max Dor
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

package io.kamax.hboxc.core.server;

import io.kamax.hboxc.core.Entity;
import io.kamax.hboxc.server._Device;
import io.kamax.hboxc.server._Machine;

public abstract class Device extends Entity implements _Device {

    private _Machine machine;

    public Device(_Machine machine, String id) {
        super(id);
        this.machine = machine;
    }

    public abstract void refresh();

    @Override
    public _Machine getMachine() {
        return machine;
    }

    @Override
    public abstract String getType();

}
