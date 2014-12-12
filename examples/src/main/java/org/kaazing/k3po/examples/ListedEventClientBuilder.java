/*
 * Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
 *
 * This file is part of Robot.
 *
 * Robot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.kaazing.k3po.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class ListedEventClientBuilder {

    private final ListedEventClient client;

    public ListedEventClientBuilder() {
        this.client = new ListedEventClient();
    }

    public ListedEventClientBuilder connect(String host, int port) {
        final Connect step = new Connect(host, port);
        client.addStep(step);
        return this;
    }

    public ListedEventClientBuilder write(String s) {
        final Write step = new Write(s);
        client.addStep(step);
        return this;
    }

    public ListedEventClientBuilder read(String s) {
        final Read step = new Read(s);
        client.addStep(step);
        return this;
    }

    public ListedEventClientBuilder close() {
        final Close step = new Close();
        client.addStep(step);
        return this;
    }

    public ListedEventClient done() {
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
