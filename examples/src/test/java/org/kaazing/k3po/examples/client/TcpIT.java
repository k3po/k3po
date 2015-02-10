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

package org.kaazing.k3po.examples.client;

import org.junit.Rule;
import org.junit.Test;
import org.kaazing.k3po.examples.ListedEventClient;
import org.kaazing.k3po.examples.ListedEventClientBuilder;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class TcpIT {

    @Rule
    public K3poRule k3po = new K3poRule();

    private ListedEventClient helloWorldClient = new ListedEventClientBuilder()
                .connect("localhost", 8001)
                .write("hello world")
                .read("hello client")
                .close()
            .done();

    @Test
    @Specification("helloWorld")
    public void testHelloWorldDeprecatedJoin() throws Exception {
        helloWorldClient.run();
        k3po.join();
    }
    
    @Test
    @Specification("helloWorld")
    public void testHelloWorld() throws Exception {
        helloWorldClient.run();
        k3po.start();
        k3po.finish();
    }
}
