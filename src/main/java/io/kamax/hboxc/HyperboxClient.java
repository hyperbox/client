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

package io.kamax.hboxc;

import io.kamax.hboxc.front._Front;

public class HyperboxClient {

    public static final String CFGKEY_BASE_DIR = "client.base.dir";
    public static final String CFGVAL_BASE_DIR = ".";

    private static _Front view;

    public static void initView(_Front view) {
        if (HyperboxClient.view == null) {
            HyperboxClient.view = view;
        }
    }

    public static _Front getView() {
        return view;
    }

}
