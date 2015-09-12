package io.kamax.hboxc.gui.utils;

import io.kamax.hboxc.gui.worker.receiver.WorkerDataReceiver;

public abstract class KxAnonymousWorker extends AxSwingWorker<WorkerDataReceiver, Void, Void> {

    public KxAnonymousWorker() {
        super(new WorkerDataReceiver());
    }

}
