/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
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

package io.kamax.hboxc.controller.action;

import io.kamax.hbox.comm.AnswerType;

public abstract class AbstractClientControllerMultiAction extends AbstractClientControllerAction {

    @Override
    public AnswerType getStartReturn() {
        return AnswerType.STARTED;
    }

    @Override
    public AnswerType getFinishReturn() {
        return AnswerType.COMPLETED;
    }

    @Override
    public AnswerType getFailReturn() {
        return AnswerType.FAILED;
    }

}
