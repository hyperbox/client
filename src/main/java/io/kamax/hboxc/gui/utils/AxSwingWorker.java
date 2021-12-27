/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.gui.utils;

import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.worker.receiver._WorkerDataReceiver;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;

/**
 * @param <K> Worker receiver type
 * @param <T> Final return value
 * @param <V> Progress return value
 */
public abstract class AxSwingWorker<K extends _WorkerDataReceiver, T, V> extends SwingWorker<T, V> {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private K recv;
    private boolean failed = false;

    public AxSwingWorker(K recv) {
        setReceiver(recv);
    }

    protected K getReceiver() {
        return recv;
    }

    protected void setReceiver(final K recv) {
        this.recv = recv;

        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent ev) {
                if ("state".equals(ev.getPropertyName()) && (SwingWorker.StateValue.STARTED == ev.getNewValue())) {
                    recv.loadingStarted();
                }
            }
        });
    }

    @Override
    protected final T doInBackground() throws Exception {
        return innerDoInBackground();

    }

    protected abstract T innerDoInBackground() throws Exception;

    @Override
    protected final void done() {
        try {
            innerDone();
            try {
                recv.loadingFinished(true, null);
            } catch (Throwable t) {
                log.error("Tracing Exception", t);
                Gui.showError(t);
            }
        } catch (ExecutionException e) {
            failed = true;
            recv.loadingFinished(false, e.getCause());
        } catch (Throwable t) {
            failed = true;
            recv.loadingFinished(false, t);
        }
    }

    protected void innerDone() throws InterruptedException, ExecutionException {
        get();
    }

    public boolean hasFailed() {
        return failed;
    }

}
