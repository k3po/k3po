/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.kaazing.robot.lang.LocationInfo;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.ast.builder.AstScriptNodeBuilder;

public class GatherStreamsLocationVisitorTest {

    @Test
    public void canLocateOneAcceptedStream() throws Exception {

        AstScriptNode scriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode actualScriptNode = scriptNode.accept(new AssociateStreamsVisitor(), new AssociateStreamsVisitor.State());

        /* First make check the success case */
        List<LocationInfo> observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));

        Map<LocationInfo, Object> boundedServerLocations = new HashMap<LocationInfo, Object>(1, 100);
        boundedServerLocations.put(new LocationInfo(1, 0), null);

        GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Two location results for accept and accepted", observedLocs.size() + 1, state.results.size());

        GatherStreamsLocationVisitor.StreamResultLocationInfo observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

        /* Check a failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Two location results for accept and accepted", observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        /* Check Only AcceptCase */
        observedLocs = new ArrayList<LocationInfo>();

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        /* Check the nothing worked case */
        observedLocs = new ArrayList<LocationInfo>(0);
        boundedServerLocations = new HashMap<LocationInfo, Object>(0, 100);

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size(), state.results.size());
    }

    @Test
    public void canLocateTwoAcceptedStreams()
        throws Exception {

        AstScriptNode scriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world 2")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world 2")
                    .done()
                .done()
            .done();

        AstScriptNode actualScriptNode = scriptNode.accept(new AssociateStreamsVisitor(), new AssociateStreamsVisitor.State());

        /* First make check the success case */
        List<LocationInfo> observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));
        observedLocs.add(new LocationInfo(9, 0));

        final Map<LocationInfo, Object> boundedServerLocations = new HashMap<LocationInfo, Object>(1, 100);
        boundedServerLocations.put(new LocationInfo(1, 0), null);

        GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Three location results", observedLocs.size() + 1, state.results.size());

        GatherStreamsLocationVisitor.StreamResultLocationInfo observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);

        /* Check a failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(9, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Three location results", observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);

        /* Both failed case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(7, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Two location results for accept and accepted", observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(7, 0), observed.observed);

        /* One didn't accept case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(9, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals("Three location results", observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);
    }

    @Test
    public void canLocateTwoAcceptStreams() throws Exception {

        AstScriptNode scriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
        .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8001"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world 2")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world 2")
                    .done()
                .done()
            .done();

        AstScriptNode actualScriptNode = scriptNode.accept(new AssociateStreamsVisitor(), new AssociateStreamsVisitor.State());

        /* First make check the success case */
        List<LocationInfo> observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));
        observedLocs.add(new LocationInfo(10, 0));

        final Map<LocationInfo, Object> boundedServerLocations = new HashMap<LocationInfo, Object>(2, 100);
        boundedServerLocations.put(new LocationInfo(1, 0), null);
        boundedServerLocations.put(new LocationInfo(6, 0), null);

        GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 2, state.results.size());

        GatherStreamsLocationVisitor.StreamResultLocationInfo observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(3);
        assertEquals(new LocationInfo(7, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertEquals(new LocationInfo(10, 0), observed.observed);

        /* Check a failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(10, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 2, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(3);
        assertEquals(new LocationInfo(7, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertEquals(new LocationInfo(10, 0), observed.observed);

        /* Both failed case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(7, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 2, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(3);
        assertEquals(new LocationInfo(7, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertEquals(new LocationInfo(7, 0), observed.observed);

        /* Missing accept */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(10, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 2, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(7, 0), observed.start);
        assertEquals(new LocationInfo(10, 0), observed.end);
        assertEquals(new LocationInfo(10, 0), observed.observed);
    }

    @Test
    public void canLocateConnectStream() throws Exception {

        AstScriptNode scriptNode = new AstScriptNodeBuilder()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode actualScriptNode = scriptNode.accept(new AssociateStreamsVisitor(), new AssociateStreamsVisitor.State());

        /* First make check the success case */
        List<LocationInfo> observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(4, 0));

        GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(observedLocs);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size(), state.results.size());

        GatherStreamsLocationVisitor.StreamResultLocationInfo observed = state.results.get(0);

        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(4, 0), observed.end);
        assertEquals(new LocationInfo(4, 0), observed.observed);

        /* Check a failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size(), state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(4, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);
    }

    @Test
    public void canLocateConnectAndAcceptedStream() throws Exception {

        AstScriptNode scriptNode = new AstScriptNodeBuilder()
            .addAcceptStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .done()
            .addAcceptedStream()
                .setNextLineInfo(1, 0)
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .addConnectStream()
                .setNextLineInfo(1, 0)
                .setLocation(URI.create("tcp://localhost:8000"))
                .addConnectedEvent()
                    .setNextLineInfo(1, 0)
                    .done()
                .addReadEvent()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .addWriteCommand()
                    .setNextLineInfo(1, 0)
                    .addExactText("Hello, world")
                    .done()
                .done()
            .done();

        AstScriptNode actualScriptNode = scriptNode.accept(new AssociateStreamsVisitor(), new AssociateStreamsVisitor.State());

        /* First make check the success case */
        List<LocationInfo> observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));
        observedLocs.add(new LocationInfo(9, 0));

        final Map<LocationInfo, Object> boundedServerLocations = new HashMap<LocationInfo, Object>(1, 100);
        boundedServerLocations.put(new LocationInfo(1, 0), null);

        GatherStreamsLocationVisitor.State state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        GatherStreamsLocationVisitor.StreamResultLocationInfo observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);

        /* Check accepted failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(9, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);

        /* Check connected failure case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));
        observedLocs.add(new LocationInfo(7, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(7, 0), observed.observed);

        /* Both fail case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(3, 0));
        observedLocs.add(new LocationInfo(7, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(3, 0), observed.observed);

        observed = state.results.get(2);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(7, 0), observed.observed);

        /* Missing accept case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(9, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(6, 0), observed.start);
        assertEquals(new LocationInfo(9, 0), observed.end);
        assertEquals(new LocationInfo(9, 0), observed.observed);

        /* Missing connect case */
        observedLocs = new ArrayList<LocationInfo>();
        observedLocs.add(new LocationInfo(5, 0));

        state = new GatherStreamsLocationVisitor.State(observedLocs, boundedServerLocations);
        actualScriptNode.accept(new GatherStreamsLocationVisitor(), state);

        assertEquals(observedLocs.size() + 1, state.results.size());

        observed = state.results.get(0);
        assertEquals(new LocationInfo(1, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertNull(observed.observed);

        observed = state.results.get(1);
        assertEquals(new LocationInfo(2, 0), observed.start);
        assertEquals(new LocationInfo(5, 0), observed.end);
        assertEquals(new LocationInfo(5, 0), observed.observed);

    }

}
