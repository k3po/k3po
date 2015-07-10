package org.kaazing.specification.httpx;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class OpeningHandshakeIT {

    private final K3poRule k3po = new K3poRule();

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "opening/fails.to.upgrade.without.handshake.protocol/request",
        "opening/fails.to.upgrade.without.handshake.protocol/response" })
    public void shouldFailWithoutHandshakeProtocol() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "opening/upgrades.with.handshake.protocol/request",
        "opening/upgrades.with.handshake.protocol/response" })
    public void shouldPassWithHandshakeProtocol() throws Exception {
        k3po.finish();
    }

}

