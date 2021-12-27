package io.kamax.hboxc.gui.action;

import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;


public class LoadingAction extends AbstractAction {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private static LoadingAction action = new LoadingAction();

    public LoadingAction() {
        this(null);
    }

    public LoadingAction(String text) {
        super(text, IconBuilder.LoadingIcon);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        log.error("Tracing Exception", new Exception("Trying to use the loading action!"));
    }

    public static LoadingAction get() {
        return action;
    }

}
