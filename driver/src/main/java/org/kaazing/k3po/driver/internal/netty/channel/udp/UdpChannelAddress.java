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
package org.kaazing.k3po.driver.internal.netty.channel.udp;


import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddress;

import java.net.URI;

public class UdpChannelAddress extends ChannelAddress {

    private static final long serialVersionUID = 1L;

    private final long timeout;

    UdpChannelAddress(URI location, ChannelAddress transport, long timeout) {
        super(location, transport);
        this.timeout = timeout;
    }

    public long timeout() {
        return timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof UdpChannelAddress) {
            UdpChannelAddress that = (UdpChannelAddress) o;
            return this.timeout == that.timeout && super.equals(o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (super.hashCode() + timeout);
    }

    @Override
    public String toString() {
        String str = super.toString();
        return str + "[timeout=" + timeout + ']';
    }

}
