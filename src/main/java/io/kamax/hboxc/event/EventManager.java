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

package io.kamax.hboxc.event;

public final class EventManager {

    private static _EventManager evMgr;

    private EventManager() {
        // static only
    }

    static {
        evMgr = new DefaultEventManager("Core-EvMgr");
    }

    public static _EventManager get() {
        return evMgr;
    }

    public static void register(Object o) {

        evMgr.register(o);
    }

    public static void unregister(Object o) {

        evMgr.unregister(o);
    }

    public static void post(Object o) {

        evMgr.post(o);
    }

}
