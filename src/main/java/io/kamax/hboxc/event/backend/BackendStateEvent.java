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

import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.event.ClientEventTypes;
import io.kamax.hboxc.state.BackendStates;

public final class BackendStateEvent extends BackendEvent {

    public BackendStateEvent(_Backend backend, BackendStates s) {
        super(ClientEventTypes.BackendState, backend);
        set(s);
    }

    public BackendStates getState() {
        return get(BackendStates.class);
    }

    @Override
    public String toString() {
        return "Backend ID " + getBackend().getId() + " changed state to " + getState() + " @ " + getTime();
    }

}
