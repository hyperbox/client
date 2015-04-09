/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2013 Maxime Dor
 * hyperbox at altherian dot org
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

package io.kamax.hboxc;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.HyperboxAPI;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.tool.Version;
import io.kamax.tool.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class Hyperbox {

   private static Properties buildProperties;
   private static Version version = Version.UNKNOWN;

   public static String getConfigFilePath() throws HyperboxException {
      return Configuration.getUserDataPath() + File.separator + "main.cfg";
   }

   private static void failedToLoad(Exception e) {
      Logger.error("Unable to access the build.properties file: " + e.getMessage());
      Logger.error("Version and revision will not be accurate");
   }

   static {
      buildProperties = new Properties();
      try {
         buildProperties.load(Hyperbox.class.getResourceAsStream("/client.build.properties"));
         Version rawVersion = new Version(buildProperties.getProperty("version"));
         if (rawVersion.isValid()) {
            version = rawVersion;
         } else {
            Logger.error("Invalid client version in properties: " + rawVersion);
            Logger.error("Failing back to " + version);
         }
      } catch (IOException e) {
         failedToLoad(e);
      } catch (NullPointerException e) {
         failedToLoad(e);
      }
   }

   public static Version getVersion() {
      return version;
   }

   public static void processArgs(Set<String> args) {
      HyperboxAPI.processArgs(args);

      if (args.contains("-?") || args.contains("--help")) {
         System.out.println("Hyperbox available executable switches:\n");
         System.out.println("--help or -? : Print this help");
         // TODO enable more command line switches
         System.out.println("--apiversion : Print API version");
         System.out.println("--apirevision : Print API revision");
         System.out.println("--netversion : Print Net protocol version");
         System.out.println("--version : Print Client version");
         System.out.println("--revision : Print Client revision");
         System.exit(0);
      }
      if (args.contains("--version")) {
         System.out.println(getVersion());
         System.exit(0);
      }
   }

}
