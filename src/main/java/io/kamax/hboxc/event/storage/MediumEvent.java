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

package io.kamax.hboxc.event.storage;

import io.kamax.hbox.comm.out.storage.MediumOut;
import io.kamax.hbox.event.Event;

public abstract class MediumEvent extends Event {

    public MediumEvent(Enum<?> s, MediumOut medOut) {
        super(s);
        set(MediumOut.class, medOut);
    }

    public MediumOut getMedium() {
        return get(MediumOut.class);
    }

    @Override
    public String toString() {
        return "Event ID " + getEventId() + " for Medium " + getMedium().getUuid() + " located at " + getMedium().getLocation() + " occured @ "
                + getTime();
    }

}
