/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.junit.rules;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public final class UnBoundRule implements TestRule {

    private int shouldBeUnboundPort;

    public static UnBoundRule none() {
        return new UnBoundRule();
    }

    private UnBoundRule() {
    }

    public UnBoundRule expectPortUnbound(int port) {
        shouldBeUnboundPort = port;
        return this;
    }

    @Override
    public Statement apply(Statement base,
            org.junit.runner.Description description) {
        return new ExpectedPortUnBoundStatement(base);
    }

    private class ExpectedPortUnBoundStatement extends Statement {
        private final Statement fNext;

        public ExpectedPortUnBoundStatement(Statement base) {
            fNext = base;
        }

        @Override
        public void evaluate() throws Throwable {
            Throwable caughtException = null;

            // Evaluate the statement and remember if there was an exception
            try {
                fNext.evaluate();

            } catch (Throwable t) {
                caughtException = t;
            }

            // Test to make sure we unbound.
            try {
                expectUnBound();
            } catch (Throwable t) {
                if (caughtException != null) {
                    t.initCause(caughtException);
                }
                caughtException = t;
            }

            // Re-throw any caught exception
            if (caughtException != null) {
                throw caughtException;
            }
        }

        private void expectUnBound() {
            if (shouldBeUnboundPort != 0) {
                try {
                    // ServerSocket server = new ServerSocket(shouldBeUnboundPort);
                    ServerSocket server = new ServerSocket();
                    server.setReuseAddress(true);
                    server.bind(new InetSocketAddress("localhost", shouldBeUnboundPort));
                    server.close();
                } catch (IOException ioe) {
                    assertTrue(String.format("Could not bind to port %s: %s", shouldBeUnboundPort, ioe), false);
                }
            }
        }
    }
}

