/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

/**
 * A simple builder to build a simple TCP client to run example K3po tests againsts.
 *
 */
public class ListedEventClientBuilder {

    private final ListedEventClient client;

    ListedEventClientBuilder() {
        this.client = new ListedEventClient();
    }

    ListedEventClientBuilder connect(String host, int port) {
        final Connect step = new Connect(host, port);
        client.addStep(step);
        return this;
    }

    ListedEventClientBuilder write(String s) {
        final Write step = new Write(s);
        client.addStep(step);
        return this;
    }

    ListedEventClientBuilder read(String s) {
        final Read step = new Read(s);
        client.addStep(step);
        return this;
    }

    ListedEventClientBuilder close() {
        final Close step = new Close();
        client.addStep(step);
        return this;
    }

    ListedEventClient done() {
        return client;
    }


    private class Connect extends ListedEventRunnable.Step {

        private final String host;
        private final int port;

        public Connect(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() throws IOException {
            final Socket socket = new Socket(host, port);
            client.setSocket(socket);
        }
    }

    private class Close extends ListedEventRunnable.Step {

        @Override
        public void run() throws IOException {
            Socket socket = client.getSocket();
            socket.close();
        }
    }

    private class Write extends ListedEventRunnable.Step {

        private final String string;

        public Write(String string) {
            this.string = string;
        }

        @Override
        public void run() throws Exception {
            BufferedWriter writer = client.getBufferedWriter();
            writer.write(string);
            writer.flush();
        }
    }

    private class Read extends ListedEventRunnable.Step {

        private final String expected;

        public Read(String string) {
            this.expected = string;
        }

        @Override
        public void run() throws Exception {
            BufferedReader reader = client.getBufferedReader();
            char[] cbuf = new char[expected.length()];
            reader.read(cbuf, 0, expected.length());
            String read = new String(cbuf);
            if (!expected.equals(read)) {
                throw new Exception("Expected value of read string in client did not match expected, " +
                        "see robot script diff for details");
            }

        }
    }
}
