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

package io.kamax.hboxc;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.tools.JProperties;
import io.kamax.tools.logging.KxLog;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class PreferencesManager {

    private static final Logger log = KxLog.make(MethodHandles.lookup().lookupClass());

    private static String defaultPerfExtention = ".pref";
    private static File userPrefPath;
    private static String userPrefFilename = "global" + defaultPerfExtention;
    private static File userPrefFile;
    private static JProperties userPref;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                savePref();
            }
        });
    }

    protected PreferencesManager() {
        // static only
    }

    private static JProperties loadPref(File prefFile) throws HyperboxException {
        JProperties newSettings = new JProperties();
        try {
            if (prefFile.exists()) {
                newSettings.load(new FileReader(prefFile));
            }
        } catch (Throwable e) {
            throw new HyperboxException("Unable to load the preferences file: " + e.getMessage(), e);
        }
        return newSettings;
    }

    public static void init() throws HyperboxException {
        initUserPref();
    }

    public static void initUserPref() throws HyperboxException {
        initUserPrefAll(Configuration.getUserDataPath());

        userPrefFile = new File(userPrefPath.getAbsolutePath() + File.separator + userPrefFilename);
        userPref = loadPref(userPrefFile);
        log.debug("Default Preference Extention: " + defaultPerfExtention);
        log.debug("User Preference Path: " + userPrefPath.getAbsolutePath());
        log.debug("User Global Preference File: " + userPrefFile.getAbsolutePath());
    }

    private static void initUserPrefAll(String homePath) throws HyperboxException {
        userPrefPath = new File(homePath);
        if (!userPrefPath.exists() && !userPrefPath.mkdirs()) {
            throw new HyperboxException("Unable to create User preference folder: " + userPrefPath.getAbsolutePath());
        }
    }

    public static boolean has(String namespace) {
        File configFile = new File(userPrefPath + File.separator + namespace + defaultPerfExtention);
        return configFile.exists() && configFile.isFile() && configFile.canRead();
    }

    /**
     * Without trailing file separator & absolute path
     *
     * @return where to store the settings
     */
    public static String getUserPrefPath() {
        return userPrefPath.getAbsolutePath();
    }

    /**
     * Get preferences of the user, saved in the roaming profile or in $HOME/.hbox
     *
     * @return Properties
     */
    public static JProperties getUserPref() {
        return userPref;
    }

    public static JProperties get() {
        return getUserPref();
    }

    public static void savePref() {
        try {
            log.debug("Saving global user preferences to " + userPrefFile);
            userPref.store(new FileWriter(userPrefFile), "");
        } catch (IOException e) {
            log.error("Unable to save user preferences: " + e.getMessage());
        }
    }

}
