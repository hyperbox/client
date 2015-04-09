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

package io.kamax.hboxc.core.back.kryonet;

import io.kamax.hbox.Configuration;
import io.kamax.hbox.comm.Answer;
import io.kamax.hbox.comm.AnswerType;
import io.kamax.hbox.comm.Request;
import io.kamax.hbox.comm._AnswerReceiver;
import io.kamax.hbox.comm.out.event.EventOut;
import io.kamax.hbox.exception.HyperboxException;
import io.kamax.hbox.kryonet.KryoRegister;
import io.kamax.hbox.kryonet.KryonetDefaultSettings;
import io.kamax.hboxc.back._Backend;
import io.kamax.hboxc.event.EventManager;
import io.kamax.hboxc.event.backend.BackendConnectionStateEvent;
import io.kamax.hboxc.event.backend.BackendStateEvent;
import io.kamax.hboxc.state.BackendConnectionState;
import io.kamax.hboxc.state.BackendStates;
import io.kamax.tool.logging.Logger;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoNetException;
import com.esotericsoftware.kryonet.Listener;

public final class KryonetClientBack implements _Backend {

   private Map<String, _AnswerReceiver> ansReceivers;
   private Client client;
   private volatile BackendStates state = BackendStates.Stopped;
   private volatile BackendConnectionState connState = BackendConnectionState.Disconnected;

   private Thread mainThread = Thread.currentThread();
   private Queue<Throwable> updateThreadEx = new LinkedList<Throwable>();

   @Override
   public String getId() {
      return "Kryonet";
   }

   private void setState(BackendStates state) {
      if ((state != null) && !this.state.equals(state)) {
         this.state = state;
         Logger.info("Kryonet Connector state: " + state);
         EventManager.post(new BackendStateEvent(this, state));
      } else {
         Logger.debug("Got a null state or state matches current one");
      }
   }

   private void setState(BackendConnectionState connState) {
      if ((connState != null) && !this.connState.equals(connState)) {
         this.connState = connState;
         EventManager.post(new BackendConnectionStateEvent(this, connState));
      } else {
         Logger.debug("Got a null state or state matches current one");
      }
   }

   @Override
   public void start() throws HyperboxException {
      setState(BackendStates.Starting);
      try {
         Logger.info("Backend Init Sequence started");
         ansReceivers = new HashMap<String, _AnswerReceiver>();
         int netBufferWriteSize = Integer.parseInt(Configuration.getSetting(KryonetDefaultSettings.CFGKEY_KRYO_NET_WRITE_BUFFER_SIZE,
               KryonetDefaultSettings.CFGVAL_KRYO_NET_WRITE_BUFFER_SIZE));
         int netBufferObjectSize = Integer.parseInt(Configuration.getSetting(KryonetDefaultSettings.CFGVAL_KRYO_NET_OBJECT_BUFFER_SIZE,
               KryonetDefaultSettings.CFGVAL_KRYO_NET_OBJECT_BUFFER_SIZE));
         client = new Client(netBufferWriteSize, netBufferObjectSize);
         client.start();
         client.getUpdateThread().setUncaughtExceptionHandler(new KryoUncaughtExceptionHandler());
         client.addListener(new MainListener());
         KryoRegister.register(client.getKryo());
         Logger.info("Backend Init Sequence completed");
         setState(BackendStates.Started);
      } catch (NumberFormatException e) {
         Logger.error("Invalid configuration value");
         stop(e);
      } catch (Throwable e) {
         stop(e);
      }

   }

   private void stop(Throwable e) throws HyperboxException {
      Logger.error("Backend Init Sequence failed");
      stop();
      throw new HyperboxException("Unable to connect to init Kryonet backend : " + e.getMessage());
   }

   @Override
   public void stop() {
      setState(BackendStates.Stopping);
      disconnect();
      if (client != null) {
         client.stop();
      }
      setState(BackendStates.Stopped);
   }

   @Override
   public void setAnswerReceiver(String requestId, _AnswerReceiver ar) {
      ansReceivers.put(requestId, ar);
   }

   @Override
   public void connect(String address) throws HyperboxException {
      if (!state.equals(BackendStates.Started)) {
         throw new HyperboxException("Backend is not initialized");
      }

      setState(BackendConnectionState.Connecting);

      String[] options = address.split(":", 2);
      String host = options[0];
      Integer port = options.length == 2 ? Integer.parseInt(options[1]) : Integer.parseInt(KryonetDefaultSettings.CFGVAL_KRYO_NET_TCP_PORT);
      if (options.length == 2) {
         try {
            port = Integer.parseInt(options[1]);
         } catch (NumberFormatException e) {
            throw new HyperboxException("Invalid port number: " + options[1]);
         }
      }

      try {
         client.connect(5000, host, port);
         setState(BackendConnectionState.Connected);
      } catch (Throwable e) {
         try {
            if (!updateThreadEx.isEmpty()) {
               e = updateThreadEx.poll();
               if (e instanceof KryoNetException) {
                  throw new HyperboxException("Server is using an incompatible network protocol version", e);
               }
            }
            throw new HyperboxException(e);
         } finally {
            Logger.debug(e.getMessage());
            disconnect();
         }
      }
   }

   @Override
   public void disconnect() {
      if ((client != null) && client.isConnected()) {
         setState(BackendConnectionState.Disconnecting);
         client.close();
      }
      setState(BackendConnectionState.Disconnected);
   }

   @Override
   public boolean isConnected() {
      return (client != null) && client.isConnected();
   }

   @Override
   public void putRequest(Request req) {
      if (!isConnected()) {
         Logger.debug("Tried to send a message but client is not connected");
         throw new HyperboxException("Client is not connected to a server");
      }

      try {
         Logger.debug("Sending request");
         client.sendTCP(req);
         Logger.debug("Send request");
      } catch (Throwable t) {
         Logger.exception(t);
      }
   }

   private class MainListener extends Listener {

      @Override
      public void connected(Connection connection) {
         Logger.info(connection.getRemoteAddressTCP().getAddress() + " connected.");
         setState(BackendConnectionState.Connected);
      }

      @Override
      public void received(Connection connection, Object object) {
         if (object.getClass().equals(Answer.class)) {
            Answer ans = (Answer) object;
            Logger.debug("Received answer from server : " + ans.getExchangeId() + " - " + ans.getType() + " - " + ans.getCommand() + " - " + ans.getName());
            if (ansReceivers.containsKey(ans.getExchangeId())) {
               ansReceivers.get(ans.getExchangeId()).putAnswer(ans);
               if (ans.isExchangedFinished() && !ans.getType().equals(AnswerType.QUEUED)) {
                  ansReceivers.remove(ans.getExchangeId());
               }
            } else {
               Logger.warning("Oprhan answer: " + ans.getExchangeId() + " - " + ans.getType() + " - " + ans.getCommand() + " - " + ans.getName());
            }
         }
         if (object instanceof EventOut) {
            EventManager.get().post(object);
         }
      }

      @Override
      public void disconnected(Connection connection) {
         Logger.info("Disconnected from Hyperbox server");
         disconnect();
      }
   }

   private class KryoUncaughtExceptionHandler implements UncaughtExceptionHandler {

      @Override
      public void uncaughtException(Thread arg0, Throwable arg1) {
         Logger.error("Uncaught exception in Kryonet Update Thread: " + arg1.getMessage());
         try {
            updateThreadEx.add(arg1);
            mainThread.interrupt();
         } catch (Throwable t) {
            Logger.error("Failed to insert exception of update thread: " + t.getMessage());
         }
      }
   }

}
