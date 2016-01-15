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
package org.kaazing.k3po.driver.internal.jmock;

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
