/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.k3po.driver.internal.netty.channel.agrona;

import java.net.URI;

import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

public class AgronaChannelAddress extends ChannelAddress {
    private static final long serialVersionUID = 1L;

    public static final String OPTION_READER = "reader";

    public static final String OPTION_WRITER = "writer";

    private final ChannelReader reader;
    private final ChannelWriter writer;

    public AgronaChannelAddress(
            URI location,
            ChannelReader reader,
            ChannelWriter writer) {
        this(location, reader, writer, false);
    }

    public AgronaChannelAddress(
            URI location,
            ChannelReader reader,
            ChannelWriter writer,
            boolean ephemeral) {
        super(location, null, ephemeral);
        this.reader = reader;
        this.writer = writer;
    }

    public ChannelReader getReader() {
        return reader;
    }

    public ChannelWriter getWriter() {
        return writer;
    }

}
