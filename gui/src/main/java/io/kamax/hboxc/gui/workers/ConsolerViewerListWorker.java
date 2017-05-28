package io.kamax.hboxc.gui.workers;

import io.kamax.hboxc.comm.output.ConsoleViewerOutput;
import io.kamax.hboxc.gui.Gui;
import io.kamax.hboxc.gui.utils.AxSwingWorker;
import io.kamax.hboxc.gui.worker.receiver._ConsoleViewerListReceiver;

import java.util.List;


public class ConsolerViewerListWorker extends AxSwingWorker<_ConsoleViewerListReceiver, Void, ConsoleViewerOutput> {

    public ConsolerViewerListWorker(_ConsoleViewerListReceiver recv) {
        super(recv);
    }

    @Override
    protected Void innerDoInBackground() throws Exception {
        for (ConsoleViewerOutput con : Gui.getReader().listConsoleViewers()) {
            publish(con);
        }

        return null;
    }

    @Override
    protected void process(List<ConsoleViewerOutput> objOutList) {
        getReceiver().add(objOutList);
    }

    public static void execute(_ConsoleViewerListReceiver recv) {
        new ConsolerViewerListWorker(recv).execute();
    }

}
