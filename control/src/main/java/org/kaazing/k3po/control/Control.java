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

package org.kaazing.k3po.control;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kaazing.k3po.control.command.AbortCommand;
import org.kaazing.k3po.control.command.Command;
import org.kaazing.k3po.control.command.PrepareCommand;
import org.kaazing.k3po.control.command.StartCommand;
import org.kaazing.k3po.control.event.CommandEvent;
import org.kaazing.k3po.control.event.ErrorEvent;
import org.kaazing.k3po.control.event.FinishedEvent;
import org.kaazing.k3po.control.event.PreparedEvent;
import org.kaazing.k3po.control.event.StartedEvent;

public final class Control {

    private static final String FINISHED_EVENT = "FINISHED";
    private static final String ERROR_EVENT = "ERROR";
    private static final String STARTED_EVENT = "STARTED";
    private static final String PREPARED_EVENT = "PREPARED";

    private static final Pattern HEADER_PATTERN = Pattern.compile("([a-z\\-]+):([^\n]+)");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final URL location;
    private URLConnection connection;
    BufferedReader textIn;

    public Control(URL location) {
        this.location = location;
    }

    public void connect() throws Exception {
        connection = location.openConnection();
        connection.connect();
        InputStream bytesIn = connection.getInputStream();
        CharsetDecoder decoder = UTF_8.newDecoder();
        textIn = new BufferedReader(new InputStreamReader(bytesIn, decoder));
    }

    public void disconnect() throws Exception {

        if (connection != null) {
            try {
                if (connection instanceof Closeable) {
                    ((Closeable) connection).close();
                }
                else {
                    try {
                        connection.getInputStream().close();
                    }
                    catch (IOException e) {
                        // ignore
                    }

                    try {
                        connection.getOutputStream().close();
                    }
                    catch (IOException e) {
                        // ignore
                    }
                }
            } finally {
                connection = null;
            }
        }
    }

    public void writeCommand(Command command) throws Exception {

        checkConnected();

        switch (command.getKind()) {
        case PREPARE:
            writeCommand((PrepareCommand) command);
            break;
        case START:
            writeCommand((StartCommand) command);
            break;
        case ABORT:
            writeCommand((AbortCommand) command);
            break;
        default:
            throw new IllegalArgumentException("Urecognized command kind: " + command.getKind());
        }
    }

    public CommandEvent readEvent() throws Exception {
        // defaults to infinite
        return readEvent(0, MILLISECONDS);
    }

    public CommandEvent readEvent(int timeout, TimeUnit unit) throws Exception {

        checkConnected();

        connection.setReadTimeout((int) unit.toMillis(timeout));

        String eventType = textIn.readLine();
        if (eventType != null) {
            switch (eventType) {
            case PREPARED_EVENT:
                return readPreparedEvent();
            case STARTED_EVENT:
                return readStartedEvent();
            case ERROR_EVENT:
                return readErrorEvent();
            case FINISHED_EVENT:
                return readFinishedEvent();
            }
        }

        throw new IllegalStateException("Invalid protocol frame: " + eventType);
    }

    private void checkConnected() throws Exception {
        if (connection == null) {
            throw new IllegalStateException("Not connected");
        }
    }

    private void writeCommand(PrepareCommand prepare) throws Exception {
        OutputStream bytesOut = connection.getOutputStream();
        CharsetEncoder encoder = UTF_8.newEncoder();
        Writer textOut = new OutputStreamWriter(bytesOut, encoder);

        Iterable<String> names = prepare.getNames();

        textOut.append("PREPARE\n");
        textOut.append("version:2.0\n");
        for (String name : names) {
            textOut.append(format("name:%s\n", name));
        }
        textOut.append("\n");
        textOut.flush();
    }

    private void writeCommand(StartCommand start) throws Exception {

        OutputStream bytesOut = connection.getOutputStream();
        CharsetEncoder encoder = UTF_8.newEncoder();
        Writer textOut = new OutputStreamWriter(bytesOut, encoder);

        textOut.append("START\n");
        textOut.append("\n");
        textOut.flush();
    }

    private void writeCommand(AbortCommand abort) throws IOException, CharacterCodingException {
        OutputStream bytesOut = connection.getOutputStream();
        CharsetEncoder encoder = UTF_8.newEncoder();
        Writer textOut = new OutputStreamWriter(bytesOut, encoder);

        textOut.append("ABORT\n");
        textOut.append("\n");
        textOut.flush();
    }

    private PreparedEvent readPreparedEvent() throws IOException {
        PreparedEvent prepared = new PreparedEvent();
        String line;
        int length = -1;
        do {
            line = textIn.readLine();
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.matches()) {
                String headerName = matcher.group(1);
                String headerValue = matcher.group(2);
                switch (headerName) {
                case "content-length":
                    length = parseInt(headerValue);
                    break;
                case "name":
                    // compatibility
                    break;
                default:
                    throw new IllegalStateException("Unrecognized event header: " + headerName);
                }
            }
        } while (!line.isEmpty());

        // note: this assumes bytes-length == string-length (ASCII)
        // note: zero-length script should be non-null
        if (length >= 0) {
            prepared.setScript(readContent(length));
        }

        return prepared;
    }

    private StartedEvent readStartedEvent() throws IOException {
        StartedEvent started = new StartedEvent();
        String line;
        do {
            line = textIn.readLine();
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.matches()) {
                String headerName = matcher.group(1);
                switch (headerName) {
                case "name":
                    // compatibility
                    break;
                default:
                    throw new IllegalStateException("Unrecognized event header: " + headerName);
                }
            }
        } while (!line.isEmpty());

        return started;
    }

    private FinishedEvent readFinishedEvent() throws IOException {
        FinishedEvent finished = new FinishedEvent();
        String line;
        int length = -1;
        do {
            line = textIn.readLine();
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.matches()) {
                String headerName = matcher.group(1);
                String headerValue = matcher.group(2);
                switch (headerName) {
                case "content-length":
                    length = parseInt(headerValue);
                    break;
                case "name":
                    // compatibility
                    break;
                default:
                    throw new IllegalStateException("Unrecognized event header: " + headerName);
                }
            }
        } while (!line.isEmpty());

        // note: this assumes bytes-length == string-length (ASCII)
        // note: zero-length script should be non-null
        if (length >= 0) {
            finished.setScript(readContent(length));
        }

        return finished;
    }

    private ErrorEvent readErrorEvent() throws IOException {
        ErrorEvent error = new ErrorEvent();
        String line;
        int length = 0;
        do {
            line = textIn.readLine();
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.matches()) {
                String headerName = matcher.group(1);
                String headerValue = matcher.group(2);
                switch (headerName) {
                case "content-length":
                    length = parseInt(headerValue);
                    break;
                case "summary":
                    error.setSummary(headerValue);
                    break;
                case "name":
                    // compatibility
                    break;
                default:
                    throw new IllegalStateException("Unrecognized event header: " + headerName);
                }
            }
        } while (!line.isEmpty());

        // note: this assumes bytes-length == string-length (ASCII)
        if (length > 0) {
            error.setDescription(readContent(length));
        }

        return error;
    }

    private String readContent(final int length) throws IOException {
        final char[] content = new char[length];
        int bytesRead = 0;
        do {
            int result = textIn.read(content, bytesRead, length - bytesRead);
            if (result == -1) {
                throw new EOFException("EOF detected before all content read");
            }
            bytesRead += result;
        } while (bytesRead != length);
        return new String(content);
    }
}
