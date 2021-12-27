/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 *
 * https://apps.kamax.io/hyperbox
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

package io.kamax.hboxc.controller;

import io.kamax.hbox.ClassManager;
import io.kamax.hbox.Configuration;
import io.kamax.hbox.HyperboxAPI;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.comm.in.MachineIn;
import io.kamax.hbox.comm.in.ServerIn;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.Hyperbox;
import io.kamax.hboxc.HyperboxClient;
import io.kamax.hboxc.PreferencesManager;
import io.kamax.hboxc.controller.action._ClientControllerAction;
import io.kamax.hboxc.core.ClientCore;
import io.kamax.hboxc.core.CoreReader;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.front._Front;
import io.kamax.hboxc.front.minimal.MiniUI;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public final class Controller implements _ClientMessageReceiver {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private ClientCore core;
    private _Front front = new MiniUI();

    private Map<String, _ClientControllerAction> actionsMap;

    static {
        try {
            PreferencesManager.init();
            // FIXME reconfigure logging behaviour
            System.out.println(getHeader());
            if (new File(Hyperbox.getConfigFilePath()).exists()) {
                Configuration.init(Hyperbox.getConfigFilePath());
            } else {
                log.debug("Default config file does not exist, skipping: " + Hyperbox.getConfigFilePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void loadActions() throws HyperboxException {
        actionsMap = new HashMap<String, _ClientControllerAction>();

        ShutdownAction.c = this;

        for (_ClientControllerAction ac : ClassManager.getAllOrFail(_ClientControllerAction.class)) {
            actionsMap.put(ac.getRegistration().toString(), ac);
        }
    }

    public static String getHeader() {
        return HyperboxAPI.getLogHeader(Hyperbox.getVersion().toString());
    }

    public void start() throws HyperboxException {
        try {
            log.debug("-------- Environment variables -------");
            for (String name : System.getenv().keySet()) {
                if (name.startsWith(Configuration.CFG_ENV_PREFIX + Configuration.CFG_ENV_SEPERATOR)) {
                    log.debug(name + " | " + System.getenv(name));
                } else {
                    log.debug(name + " | " + System.getenv(name));
                }
            }
            log.debug("--------------------------------------");

            EventManager.get().start();

            loadActions();

            String classToLoad = Configuration.getSetting("view.class", "io.kamax.hboxc.gui.Gui");
            log.info("Loading frontend class: " + classToLoad);
            _Front front = ClassManager.loadClass(_Front.class, classToLoad);
            front.start();
            front.setRequestReceiver(this);

            this.front = front;

            core = new ClientCore();
            core.init();
            front.setCoreReader(new CoreReader(core));
            core.start();

            HyperboxClient.initView(front);
        } catch (Throwable t) {
            log.error("Tracing exception", t);
            try {
                front.postError(t);
            } catch (Throwable t1) {
                t.printStackTrace();
                t1.printStackTrace();
            }
            stop();
        }
    }

    public void stop() {
        try {
            if (core != null) {
                core.stop();
                core.destroy();
            }
            log.debug("Core was stopped");
            if (front != null) {
                front.stop();
            }
            log.debug("Front-end was stopped");

            EventManager.get().stop();
            log.debug("EventManager was stopped");
            log.info("Exiting");
            System.exit(0);
        } catch (Throwable t) {
            log.warn("Exception while stopping the client", t);
            log.info("Exiting");
            System.exit(1);
        }
    }

    @Override
    public void post(MessageInput mIn) {
        Request req = mIn.getRequest();
        _AnswerReceiver recv = mIn.getReceiver();

        try {
            if (actionsMap.containsKey(mIn.getRequest().getName())) {
                _ClientControllerAction action = actionsMap.get(mIn.getRequest().getName());
                action.run(core, front, req, recv);
            } else {
                if (req.has(ServerIn.class)) {
                    core.getServer(req.get(ServerIn.class).getId()).sendRequest(req);
                } else if (req.has(MachineIn.class)) {
                    core.getServer(req.get(MachineIn.class).getServerId()).sendRequest(req);
                } else {
                    throw new HyperboxException("Server ID or Machine ID is required for generic requests");
                }
            }
        } catch (RuntimeException e) {
            log.error("Unable to perform the request [ " + req.getName() + " ]", e);
            throw e;
        }
    }

    @Override
    public void putRequest(Request request) {
        post(new MessageInput(request));
    }

}
