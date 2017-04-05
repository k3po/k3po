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
package org.kaazing.k3po.driver.internal.netty.channel.file;


import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class FileChannelAddress extends ChannelAddress {

    private static final long serialVersionUID = 1L;

    private final String mode;
    private final long size;

    public FileChannelAddress(URI location, Map<String, Object> options) {
        super(location);

        String mode = (String) options.get("mode");
        this.mode = mode == null ? "rw" : mode;

        Long size = (Long) options.get("size");
        this.size = size == null ? 0L : size;
    }

    public String mode() {
        return mode;
    }

    public long size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof FileChannelAddress) {
            FileChannelAddress that = (FileChannelAddress) o;
            return Objects.equals(mode, that.mode) && this.size == that.size && super.equals(o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (super.hashCode() + mode.hashCode() + size);
    }

}
