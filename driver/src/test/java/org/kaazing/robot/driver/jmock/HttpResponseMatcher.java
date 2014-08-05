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

package org.kaazing.robot.driver.jmock;

import java.util.List;
import java.util.Map.Entry;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpResponseMatcher extends BaseMatcher<MessageEvent> {

    private final Object expected;

    HttpResponseMatcher(Object expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object obj) {
        return (obj instanceof MessageEvent) && matches((MessageEvent) obj);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("response ").appendValue(expected);
    }

    private boolean matches(MessageEvent evt) {
        HttpResponse response = (HttpResponse) evt.getMessage();
        HttpResponse expected = (HttpResponse) this.expected;
        List<Entry<String, String>> responseEntries = response.headers().entries();
        List<Entry<String, String>> expectedEntries = expected.headers().entries();
        if (responseEntries.size() != expectedEntries.size()) {
            return false;
        }
        for (int i = 0; i < responseEntries.size(); i++) {
            if (!responseEntries.get(i).getKey().equals(expectedEntries.get(i).getKey())
                    || !responseEntries.get(i).getValue().equals(expectedEntries.get(i).getValue())) {
                return false;
            }
        }
        return expected.getProtocolVersion().equals(response.getProtocolVersion())
                && expected.getStatus().equals(response.getStatus())
                && expected.getContent().equals(response.getContent());
    }
}
