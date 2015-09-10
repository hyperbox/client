package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hbox.comm.out.hypervisor.HypervisorOut;


public interface _HypervisorReceiver extends _WorkerDataReceiver {

    public void put(HypervisorOut hypOut);

}
