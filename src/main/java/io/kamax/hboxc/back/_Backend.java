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

package io.kamax.hboxc.back;

import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.comm._RequestReceiver;
import io.kamax.hbox.exception.HyperboxException;

public interface _Backend extends _RequestReceiver {

    public String getId();

    public void start() throws HyperboxException;

    public void stop();

    public void setAnswerReceiver(String requestId, _AnswerReceiver arRcv);

    public void connect(String address) throws HyperboxException;

    public void disconnect();

    public boolean isConnected();

}
