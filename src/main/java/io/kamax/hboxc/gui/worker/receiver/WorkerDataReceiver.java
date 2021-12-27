package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hboxc.gui.Gui;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class WorkerDataReceiver implements _WorkerDataReceiver {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    @Override
    public void loadingStarted() {
        // stub

    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        if (!isSuccessful) {
            log.error("Tracing Exception", t);
            Gui.showError(t);
        }
    }

}
