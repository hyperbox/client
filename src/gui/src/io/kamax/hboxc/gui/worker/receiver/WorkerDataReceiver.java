package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hboxc.gui.Gui;
import io.kamax.tool.logging.Logger;

public class WorkerDataReceiver implements _WorkerDataReceiver {

    @Override
    public void loadingStarted() {
        // stub

    }

    @Override
    public void loadingFinished(boolean isSuccessful, Throwable t) {
        if (!isSuccessful) {
            Logger.exception(t);
            Gui.showError(t);
        }
    }

}
