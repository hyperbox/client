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

package io.kamax.hboxc.factory;

import io.kamax.hbox.ClassManager;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.back._Backend;

import java.util.*;

public class BackendFactory {

    private static Map<String, String> backends;

    static {
        backends = new HashMap<String, String>();

        try {
            Set<_Backend> backs = ClassManager.getAllOrFail(_Backend.class);
            for (_Backend backend : backs) {
                backends.put(backend.getId(), backend.getClass().getName());
            }
        } catch (HyperboxException e) {
            throw new HyperboxException(e);
        }
    }

    public static _Backend get(String id) {
        // TODO throw exception if not found
        return ClassManager.loadClass(_Backend.class, backends.get(id));
    }

    public static List<String> list() {
        return new ArrayList<String>(backends.keySet());
    }

}
