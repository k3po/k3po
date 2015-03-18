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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * A simple TCP client implementation that can be used as an example to run K3po tests against.
 *
 */
public class ListedEventClient extends ListedEventRunnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;


    ListedEventClient() {
        super();
    }

    void setSocket(Socket socket) {
        this.socket = socket;
    }

    BufferedReader getBufferedReader() throws IOException {
        if (in == null) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        return in;
    }

    BufferedWriter getBufferedWriter() throws IOException {
        if (out == null) {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        return out;
    }

    Socket getSocket() {
        return socket;
    }

    @Override
    protected void cleanUp() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                //NOOP
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                //NOOP
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                //NOOP
            }
        }
    }
}
