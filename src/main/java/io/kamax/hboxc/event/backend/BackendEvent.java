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

package io.kamax.hboxc.event.backend;

import io.kamax.hbox.event.Event;
import io.kamax.hboxc.back._Backend;

public abstract class BackendEvent extends Event {

    public BackendEvent(Enum<?> s, _Backend backend) {
        super(s);
        set(_Backend.class, backend);
    }

    public _Backend getBackend() {
        return get(_Backend.class);
    }

    @Override
    public String toString() {
        return "Event ID " + getEventId() + " for Backed Type " + getBackend().getId() + " occured @ " + getTime();
    }

}
