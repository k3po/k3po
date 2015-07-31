package org.kaazing.specification.httpxe;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.rules.RuleChain.outerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;

public class OriginSecurityIT {

    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/httpxe/origin");

    private final TestRule timeout = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public final TestRule chain = outerRule(k3po).around(timeout);

    @Test
    @Specification({
        "request.with.origin.header/request",
        "request.with.origin.header/response"})
    public void shouldPassWithOriginRequestHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.with.origin.header.and.x.origin.header/request",
        "request.with.origin.header.and.x.origin.header/response"})
    public void shouldPassWithOriginAndXoriginRequests() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "origin.request.using.ko.parameter/request",
        "origin.request.using.ko.parameter/response"})
    public void shouldPassWhenUsingKoParameter() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "origin.request.using.referer/request",
        "origin.request.using.referer/response"})
    public void shouldPassWithOnlyRefererAndXoriginRequest() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "x.origin.header.not.identical.to.origin.header/request",
        "x.origin.header.not.identical.to.origin.header/response"})
    public void shouldPassWhenXoriginHeaderDiffersFromOriginHeader() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "request.with.kac.parameter/request",
        "request.with.kac.parameter/response"})
    public void shouldPassWithAccessControlWithKacParameter() throws Exception {
        k3po.finish();
    }

    @Test
    @Specification({
        "x.origin.encoded.request.header/request",
        "x.origin.encoded.request.header/response"})
    public void shouldPassWithEncodedXoriginRequest() throws Exception {
        k3po.finish();
    }

}

