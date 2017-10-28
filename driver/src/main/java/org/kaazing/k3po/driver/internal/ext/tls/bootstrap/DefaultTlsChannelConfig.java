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
package org.kaazing.k3po.driver.internal.ext.tls.bootstrap;

import java.io.File;

import javax.net.ssl.SSLParameters;

import org.kaazing.k3po.driver.internal.netty.bootstrap.channel.DefaultChannelConfig;

public class DefaultTlsChannelConfig extends DefaultChannelConfig implements TlsChannelConfig {

    private SSLParameters parameters;
    private File keyStoreFile;
    private char[] keyStorePassword;
    private File trustStoreFile;
    private char[] trustStorePassword;
    private String[] applicationProtocols;

    @Override
    public void setParameters(SSLParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SSLParameters getParameters() {
        return parameters;
    }

    @Override
    public void setKeyStoreFile(
        File keyStoreFile)
    {
        this.keyStoreFile = keyStoreFile;
    }

    @Override
    public File getKeyStoreFile()
    {
        return keyStoreFile;
    }

    @Override
    public void setKeyStorePassword(
        char[] keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public char[] getKeyStorePassword()
    {
        return keyStorePassword;
    }

    @Override
    public void setTrustStoreFile(
        File trustStoreFile)
    {
        this.trustStoreFile = trustStoreFile;
    }

    @Override
    public File getTrustStoreFile()
    {
        return trustStoreFile;
    }

    @Override
    public void setTrustStorePassword(
        char[] trustStorePassword)
    {
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public char[] getTrustStorePassword()
    {
        return trustStorePassword;
    }

    @Override
    public String[] getApplicationProtocols()
    {
        return applicationProtocols;
    }

    @Override
    public void setApplicationProtocols(
        String[] applicationProtocol)
    {
        this.applicationProtocols = applicationProtocol;
    }

    @Override
    protected boolean setOption0(
        String key,
        Object value)
    {
        if ("keyStoreFile".equals(key))
        {
            keyStoreFile = new File((String) value);
        }
        else if ("trustStoreFile".equals(key))
        {
            trustStoreFile = new File((String) value);
        }
        else if ("keyStorePassword".equals(key))
        {
            keyStorePassword = ((String) value).toCharArray();
        }
        else if ("trustStorePassword".equals(key))
        {
            trustStorePassword = ((String) value).toCharArray();
        }
        else if ("applicationProtocols".equals(key))
        {
            applicationProtocols = ((String) value).split(",");
        }
        else
        {
            return false;
        }

        return true;
    }
}
