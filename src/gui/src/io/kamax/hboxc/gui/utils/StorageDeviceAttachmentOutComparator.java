/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2015 - Maxime Dor
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package io.kamax.hboxc.gui.utils;

import io.kamax.hbox.comm.out.storage.StorageDeviceAttachmentOut;
import java.util.Comparator;


public class StorageDeviceAttachmentOutComparator implements Comparator<StorageDeviceAttachmentOut> {

    @Override
    public int compare(StorageDeviceAttachmentOut o1, StorageDeviceAttachmentOut o2) {
        return (o1.getPortId() + ":" + o1.getDeviceId()).compareTo((o2.getPortId() + ":" + o2.getDeviceId()));
    }

}
