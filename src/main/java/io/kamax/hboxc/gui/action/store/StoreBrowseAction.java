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

package io.kamax.hboxc.gui.action.store;

import io.kamax.hbox.comm.HyperboxTasks;
import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.hboxc.gui.store._StoreSelector;
import io.kamax.hboxc.gui.store.utils.StoreItemChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public final class StoreBrowseAction extends AbstractAction {

    private static final long serialVersionUID = 7743810756155591064L;
    private _StoreSelector selector;

    public StoreBrowseAction(_StoreSelector selector) {
        this(selector, "Browse");
    }

    public StoreBrowseAction(_StoreSelector selector, String label) {
        super(label, IconBuilder.getTask(HyperboxTasks.StoreGet));
        this.selector = selector;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        List<String> selection = selector.getSelection();
        if (!selection.isEmpty()) {
            StoreItemChooser.browse(selector.getServer().getId());
        }
    }

}
