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

package org.kaazing.robot.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class ListedEventClient extends ListedEventRunnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;


    public ListedEventClient() {
        super();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBufferedReader() throws IOException {
        if (in == null) {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        return in;
    }

    public BufferedWriter getBufferedWriter() throws IOException {
        if (out == null) {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        return out;
    }

    public Socket getSocket() {
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
