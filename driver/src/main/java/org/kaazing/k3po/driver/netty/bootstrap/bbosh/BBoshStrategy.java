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

package org.kaazing.k3po.driver.netty.bootstrap.bbosh;

import static java.lang.Character.toLowerCase;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BBoshStrategy {

    public static enum Kind {
        POLLING,
        LONG_POLLING,
        STREAMING
    }

    public abstract Kind getKind();

    public abstract int getRequests();

    public abstract long getInterval(TimeUnit unit);

    public static BBoshStrategy valueOf(String strategy) {

        if (strategy != null && !strategy.isEmpty()) {
            switch (strategy.charAt(0)) {
            case 'p':
                Matcher pollingMatcher = Polling.PATTERN.matcher(strategy);
                if (pollingMatcher.matches()) {
                    int interval = parseInt(pollingMatcher.group(1));
                    TimeUnit intervalUnit = SECONDS;
                    return new Polling(interval, intervalUnit);
                }
                break;
            case 'l':
                Matcher longPollingMatcher = LongPolling.PATTERN.matcher(strategy);
                if (longPollingMatcher.matches()) {
                    int interval = parseInt(longPollingMatcher.group(1));
                    TimeUnit intervalUnit = SECONDS;
                    String requests = longPollingMatcher.group(2);
                    if (requests != null) {
                        return new LongPolling(interval, intervalUnit, parseInt(requests));
                    }
                    else {
                        return new LongPolling(interval, intervalUnit);
                    }
                }
                break;
            case 's':
                Matcher streamingMatcher = Streaming.PATTERN.matcher(strategy);
                if (streamingMatcher.matches()) {
                    return new Streaming();
                }
                break;
            default:
                break;
            }
        }

        return null;
    }

    public static final class Polling extends BBoshStrategy {

        private static final Pattern PATTERN = Pattern.compile("polling;interval=([0-9]+)s");

        private final int interval;
        private final TimeUnit intervalUnit;

        Polling(int interval, TimeUnit intervalUnit) {
            this.interval = interval;
            this.intervalUnit = intervalUnit;
        }

        @Override
        public Kind getKind() {
            return Kind.POLLING;
        }

        @Override
        public long getInterval(TimeUnit unit) {
            return unit.convert(interval, intervalUnit);
        }

        @Override
        public int getRequests() {
            return 1;
        }

        public String toString() {
            return format("polling;interval=%d%s", interval, toLowerCase(intervalUnit.name().charAt(0)));
        }
    }

    public static final class LongPolling extends BBoshStrategy {

        private static final Pattern PATTERN = Pattern.compile("long-polling;interval=([0-9]+)s(?:;requests=([0-9]+))");

        private final int interval;
        private final TimeUnit intervalUnit;
        private final int requests;

        LongPolling(int interval, TimeUnit intervalUnit) {
            this(interval, intervalUnit, 2);
        }

        LongPolling(int interval, TimeUnit intervalUnit, int requests) {
            this.interval = interval;
            this.intervalUnit = intervalUnit;
            this.requests = requests;
        }

        @Override
        public Kind getKind() {
            return Kind.LONG_POLLING;
        }

        @Override
        public long getInterval(TimeUnit unit) {
            return unit.convert(interval, intervalUnit);
        }

        @Override
        public int getRequests() {
            return requests;
        }

        public String toString() {
            return format("long-polling;interval=%d%s;requests=%d", interval, toLowerCase(intervalUnit.name().charAt(0)), requests);
        }
    }

    public static final class Streaming extends BBoshStrategy {

        private static final Pattern PATTERN = Pattern.compile("streaming;request=chunked");

        Streaming() {
        }

        @Override
        public Kind getKind() {
            return Kind.STREAMING;
        }

        @Override
        public long getInterval(TimeUnit unit) {
            return 0;
        }

        @Override
        public int getRequests() {
            return 1;
        }

        public String toString() {
            return "streaming;request=chunked";
        }
    }

}
