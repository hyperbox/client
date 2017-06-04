package io.kamax.hboxc.gui.worker.receiver;

import io.kamax.hboxc.comm.output.ConsoleViewerOutput;

import java.util.List;


public interface _ConsoleViewerListReceiver extends _WorkerDataReceiver {

    public void add(List<ConsoleViewerOutput> objOutList);

}
