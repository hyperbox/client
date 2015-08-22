/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * 
 * http://kamax.io/hbox/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.hboxc.gui;

import io.kamax.hbox.ClassManager;
import io.kamax.hbox.comm.Command;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._RequestReceiver;
import io.kamax.hbox.comm.out.ServerOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.controller.ClientTasks;
import io.kamax.hboxc.controller.MessageInput;
import io.kamax.hboxc.core._CoreReader;
import io.kamax.hboxc.event.CoreStateEvent;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.front._Front;
import io.kamax.hboxc.gui.action.CloseAction;
import io.kamax.hboxc.gui.builder.JDialogBuilder;
import io.kamax.hboxc.gui.hypervisor._HypervisorModel;
import io.kamax.hboxc.gui.utils.JDialogUtils;
import io.kamax.hboxc.server._ServerReader;
import io.kamax.hboxc.state.CoreState;
import io.kamax.tool.logging.Logger;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.engio.mbassy.listener.Handler;

public final class Gui implements _Front {

    private static _RequestReceiver reqRecv;
    private static _CoreReader reader;

    private MainView mainView;

    @Override
    public void start() throws HyperboxException {
        EventQueueProxy proxy = new EventQueueProxy();
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        queue.push(proxy);

        ViewEventManager.get().start();
        EventManager.get().add(ViewEventManager.get());
        ViewEventManager.register(this);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    Logger.error("Couldn't switch to the System Look & Feel");
                }

                mainView = new MainView();
            }
        });

    }

    @Override
    public void stop() {
        if (mainView != null) {
            mainView.hide();
        }
    }

    @Override
    public void postError(Throwable t) {
        if (t.getCause() != null) {
            postError(t.getCause().getMessage());
        } else {
            postError(t.getMessage());
        }

    }

    @Override
    public void postError(String s) {
        showError(s);
    }

    public static void showError(String s) {
        JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Throwable t) {
        showError(t.getMessage());
    }

    public static void showCopyPasteHelper(String label, String value) {
        JLabel infoLabel = new JLabel(label);
        JTextField valueField = new JTextField(value);
        JButton closeButton = new JButton("Close");
        JDialog dialog = JDialogBuilder.get("Copy/Paste Helper", closeButton);
        dialog.add(infoLabel, "growx,pushx,wrap");
        dialog.add(valueField, "growx,pushx,wrap");
        dialog.add(closeButton, "spanx, center");
        closeButton.setAction(new CloseAction(dialog));
        JDialogUtils.setCloseOnEscapeKey(dialog, true);
        valueField.selectAll();
        valueField.requestFocus();
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    @Override
    public void postError(Throwable t, String s) {
        postError(s);
    }

    @Override
    public void postError(String s, Throwable t) {
        postError(s + ": " + t.getMessage());
    }

    @Handler
    public void getCoreState(CoreStateEvent event) {
        CoreState state = event.get(CoreState.class);
        Logger.debug("Got CoreState event : " + state);

        if (state == CoreState.Started) {
            mainView.show();
        }

        if (state == CoreState.Stopped) {
            stop();
        }
    }

    private class EventQueueProxy extends EventQueue {

        private void displayError(Throwable t) {
            ErrorDisplay.display("", t);
        }

        @Override
        protected void dispatchEvent(AWTEvent newEvent) {
            try {
                super.dispatchEvent(newEvent);
            } catch (HeadlessException e) {
                System.err.println("Cannot use GUI, headless environment detected");
                e.printStackTrace();
                System.exit(1);
            } catch (Throwable t) {
                displayError(t);
            }
        }

    }

    @Override
    public void setRequestReceiver(_RequestReceiver reqRecv) {
        Gui.reqRecv = reqRecv;
    }

    @Override
    public void setCoreReader(_CoreReader reader) {
        Gui.reader = reader;
    }

    public static void post(MessageInput msgIn) {
        getReqRecv().putRequest(msgIn.getRequest());
    }

    public static void post(Request req) {
        getReqRecv().putRequest(req);
    }

    public static _RequestReceiver getReqRecv() {
        return reqRecv;
    }

    public static _CoreReader getReader() {
        return reader;
    }

    public static _ServerReader getServer(String id) {
        return getReader().getServerReader(id);
    }

    public static ServerOut getServerInfo(String id) {
        return reader.getServerInfo(id);
    }

    public static _ServerReader getServer(ServerOut srvOut) {
        return getServer(srvOut.getId());
    }

    public static void exit() {
        Logger.info("Got exit signal from user");
        post(new MessageInput(new Request(Command.CUSTOM, ClientTasks.Exit)));
    }

    public static _HypervisorModel getHypervisorModel(String hypId) {
        Set<_HypervisorModel> models = ClassManager.getAllOrFail(_HypervisorModel.class);
        for (_HypervisorModel model : models) {
            if (model.getSupported().contains(hypId)) {
                return model;
            }
        }

        for (_HypervisorModel model : models) {
            for (String supportId : model.getSupported()) {
                if (hypId.contains(supportId)) {
                    return model;
                }
            }
        }

        throw new HyperboxException("No hypervisor model for " + hypId);

    }

}
