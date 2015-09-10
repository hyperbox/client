package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hbox.comm.out.hypervisor.HypervisorLoaderOut;
import java.util.List;

public interface _HypervisorModuleListReceiver extends _WorkerDataReceiver {

    public void add(List<HypervisorLoaderOut> objOutList);

}
