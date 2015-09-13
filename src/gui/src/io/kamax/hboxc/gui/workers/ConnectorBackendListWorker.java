package io.kamax.hboxc.gui.workers;

import io.kamax.hboxc.comm.output.BackendOutput;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._ConnectorBackendListReceiver;
import java.util.List;


public class ConnectorBackendListWorker extends AxSwingWorker<_ConnectorBackendListReceiver, Void, BackendOutput> {

    public ConnectorBackendListWorker(_ConnectorBackendListReceiver recv) {
        super(recv);
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (BackendOutput bOut : Gui.getReader().listBackends()) {
            publish(bOut);
        }

        return null;
    }

    @Override
    protected void process(List<BackendOutput> list) {
        getReceiver().add(list);
    }

    public static void execute(_ConnectorBackendListReceiver recv) {
        new ConnectorBackendListWorker(recv).execute();
    }

}
