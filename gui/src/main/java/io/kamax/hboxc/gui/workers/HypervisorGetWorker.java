package io.kamax.hboxc.gui.workers;

import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._HypervisorReceiver;

import java.util.concurrent.ExecutionException;

public class HypervisorGetWorker extends AxSwingWorker<_HypervisorReceiver, HypervisorOut, Void> {

    private String srvId;

    public HypervisorGetWorker(_HypervisorReceiver recv, String srvId) {
        super(recv);
        this.srvId = srvId;
    }

    @Override
    protected HypervisorOut innerDoInBackground() throws Exception {
        return Gui.getServer(srvId).getHypervisor().getInfo();
    }

    @Override
    protected void innerDone() throws InterruptedException, ExecutionException {
        getReceiver().put(get());
    }

    public static void execute(_HypervisorReceiver recv, String srvId) {
        new HypervisorGetWorker(recv, srvId).execute();
    }

    public static void execute(_WorkerTracker tracker, _HypervisorReceiver recv, String srvId) {
        tracker.register(new HypervisorGetWorker(recv, srvId)).execute();
    }

}
