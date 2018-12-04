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
package org.kaazing.k3po.driver.internal.netty.bootstrap.channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultChannelConfig extends org.jboss.netty.channel.DefaultChannelConfig
    implements ChannelConfig
{
    private final Map<String, Object> transportOptions = new HashMap<>();

    private String alignment;

    @Override
    public final boolean setOption(
        String key,
        Object value)
    {
        if (!setOption0(key, value) && !super.setOption(key, value))
        {
            transportOptions.put(key, value);
        }
        return true;
    }

    @Override
    public Map<String, Object> getTransportOptions()
    {
        return Collections.unmodifiableMap(transportOptions);
    }

    @Override
    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    @Override
    public String getAlignment()
    {
        return alignment;
    }

    protected boolean setOption0(
        String key,
        Object value)
    {
        if ("alignment".equals(key))
        {
            alignment = value.toString();
        }
        else
        {
            return false;
        }

        return true;
    }
}
