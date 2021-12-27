/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Max Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc.core.storage;

import com.thoughtworks.xstream.XStream;
import io.kamax.hbox.Configuration;
import io.kamax.hbox.comm.in.UserIn;
import io.kamax.hbox.constant.EntityType;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hboxc.core._ConsoleViewer;
import io.kamax.hboxc.core.connector.Connector;
import io.kamax.hboxc.core.connector._Connector;
import io.kamax.hboxc.core.console.viewer.ConsoleViewer;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;

public class UserProfileCoreStorage implements _CoreStorage {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private static final String CONSOLE_VIEWER_ID = "consoleViewer";
    private static final String CONSOLE_VIEWERS_FILE = "consoleViewers.xml";

    private static final String CONNECTOR_ID = "connector";
    private static final String CONNECTORS_FILE = "connectors.xml";
    private static final String CONNECTORS_CRED_FOLDER = "connectors" + File.separator + "cred";

    private XStream storage;

    private File userDataFolder;
    private File consoleViewersFile;
    private File connectorFile;
    private File connectorCredFolder;

    private void initFolder(File target) throws HyperboxException {
        if (!target.exists() && !target.mkdirs()) {
            throw new HyperboxException("Unable to create data storage directory: " + userDataFolder.getAbsolutePath());
        }
        if (!target.canRead() || !target.canWrite()) {
            throw new HyperboxException("Data directory is not readable or writable, check permissions: " + userDataFolder.getAbsolutePath());
        }
    }

    @Override
    public void init() throws HyperboxException {
        log.debug("Initializing Data Manager");

        try {
            storage = new XStream();
            storage.allowTypesByWildcard(new String[]{
                    "io.kamax.hbox.**",
                    "io.kamax.hboxc.**"
            });
            storage.alias(CONSOLE_VIEWER_ID, _ConsoleViewer.class, ConsoleViewer.class);
            storage.alias(CONNECTOR_ID, _Connector.class, Connector.class);

            // legacy names
            storage.alias("io.kamax.hbox.comm.input.UserInput", UserIn.class);
            storage.alias("io.kamax.hbox.comm.in.UserIn", UserIn.class);
            storage.alias("io.kamax.hbox.constant.Entity", EntityType.class);
            storage.alias("io.kamax.hbox.constant.EntityType", EntityType.class);

            storage.alias("user", UserIn.class);
            storage.alias("entity", EntityType.class);
            storage.omitField(Connector.class, "server");
            storage.omitField(Connector.class, "state");

            log.debug("Initiated storage serializer");
        } catch (Throwable t) {
            throw new HyperboxException("Unable to storage serializer: " + t.getMessage());
        }

        userDataFolder = new File(Configuration.getUserDataPath() + File.separator + "data");
        initFolder(userDataFolder);

        connectorCredFolder = new File(userDataFolder.getAbsoluteFile() + File.separator + CONNECTORS_CRED_FOLDER);
        initFolder(connectorCredFolder);

        consoleViewersFile = new File(userDataFolder.getAbsolutePath() + File.separator + CONSOLE_VIEWERS_FILE);
        connectorFile = new File(userDataFolder.getAbsolutePath() + File.separator + CONNECTORS_FILE);

        log.info("Initiated data directory: " + userDataFolder.getAbsolutePath());
    }

    @Override
    public void start() {
        // nothing to do
    }

    @Override
    public void stop() {
        // nothing to do
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    @Override
    public void storeViewers(Collection<_ConsoleViewer> viewers) {
        if (viewers.isEmpty() && !hasConsoleViewers()) {
            log.debug("Nothing was created, skipping");
        } else {
            log.debug("Saving " + viewers.size() + " console viewers to " + consoleViewersFile.getAbsolutePath());
            try {
                OutputStream fileStream = new FileOutputStream(consoleViewersFile);
                try {
                    storage.toXML(new ArrayList<_ConsoleViewer>(viewers), fileStream);
                } finally {
                    fileStream.close();
                }
            } catch (Throwable t) {
                throw new HyperboxException("Unable to store viewers: " + t.getMessage());
            }
        }
    }

    @Override
    public void storeConnectors(Collection<_Connector> conns) {
        if (conns.isEmpty() && !hasConnectors()) {
            log.debug("Nothing was created, skipping");
        } else {
            log.debug("Saving " + conns.size() + " connectors to " + connectorFile.getAbsolutePath());
            try {
                OutputStream fileStream = new FileOutputStream(connectorFile);
                try {
                    storage.toXML(new ArrayList<_Connector>(conns), fileStream);
                } finally {
                    fileStream.close();
                }
            } catch (Throwable t) {
                throw new HyperboxException("Unable to store connectors: " + t.getMessage());
            }
        }
    }

    @Override
    public boolean hasConsoleViewers() {
        return consoleViewersFile.exists() && consoleViewersFile.isFile() && consoleViewersFile.canRead();
    }

    @Override
    public boolean hasConnectors() {
        return connectorFile.exists() && connectorFile.isFile() && connectorFile.canRead();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<_ConsoleViewer> loadViewers() {
        log.debug("Loading console viewers from " + consoleViewersFile.getAbsolutePath());
        return (Collection<_ConsoleViewer>) storage.fromXML(consoleViewersFile);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<_Connector> loadConnectors() {
        log.debug("Loading connectors from " + connectorFile.getAbsolutePath());
        return (Collection<_Connector>) storage.fromXML(connectorFile);
    }

    @Override
    public void storeConnectorCredentials(String id, UserIn usrIn) {
        try {
            OutputStream fileStream = new FileOutputStream(connectorCredFolder.getAbsolutePath() + File.separator + id + ".xml");
            log.debug("Saving Connector ID " + id + " credentials to " + connectorCredFolder.getAbsolutePath() + File.separator + id + ".xml");
            try {
                storage.toXML(usrIn, fileStream);
            } finally {
                fileStream.close();
            }
        } catch (Throwable t) {
            throw new HyperboxException("Unable to store Connector credentials: " + t.getMessage());
        }
    }

    @Override
    public UserIn loadConnectorCredentials(String id) {
        log.debug("Loading Connector ID " + id + " credentials from " + connectorCredFolder.getAbsolutePath() + File.separator + id + ".xml");
        return (UserIn) storage.fromXML(new File(connectorCredFolder.getAbsolutePath() + File.separator + id + ".xml"));
    }

    @Override
    public void removeConnectorCredentials(String id) {
        File credFile = new File(connectorCredFolder.getAbsolutePath() + File.separator + id + ".xml");
        if (!credFile.delete()) {
            throw new HyperboxException("Unable to delete credentials file, remove manually");
        }
    }

}
