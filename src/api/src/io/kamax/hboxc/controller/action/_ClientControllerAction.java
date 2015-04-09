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
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.core._Core;
import io.kamax.hboxc.front._Front;

public interface _ClientControllerAction {

   public Enum<?> getRegistration();

   public AnswerType getStartReturn();

   public AnswerType getFinishReturn();

   public AnswerType getFailReturn();

   public Class<?>[] getRequiredClasses();

   public Enum<?>[] getRequiredEnums();

   public String[] getRequiredData();

   public void run(_Core core, _Front view, Request req, _AnswerReceiver recv) throws HyperboxException;

}
