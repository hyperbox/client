package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hboxc.comm.output.BackendOutput;
import java.util.List;

public interface _ConnectorBackendListReceiver extends _WorkerDataReceiver {

    public void add(List<BackendOutput> objOutList);

}
