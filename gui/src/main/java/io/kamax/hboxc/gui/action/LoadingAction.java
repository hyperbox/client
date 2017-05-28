package io.kamax.hboxc.gui.action;

import io.kamax.hboxc.gui.builder.IconBuilder;
import io.kamax.tools.logging.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class LoadingAction extends AbstractAction {

    private static final long serialVersionUID = 8384934950347892029L;
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
        Logger.exception(new Exception("Trying to use the loading action!"));
    }

    public static LoadingAction get() {
        return action;
    }

}
