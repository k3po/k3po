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
package org.kaazing.k3po.maven.plugin.internal.logging;

import org.apache.maven.plugin.logging.Log;
import org.jboss.netty.logging.AbstractInternalLogger;

class MavenLogger extends AbstractInternalLogger {

    private final Log logger;

    public MavenLogger(Log logger) {
        this.logger = logger;
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String msg, Throwable cause) {
        logger.debug(msg, cause);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String msg, Throwable cause) {
        logger.error(msg, cause);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String msg, Throwable cause) {
        logger.info(msg, cause);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Throwable cause) {
        logger.warn(msg, cause);
    }
}
