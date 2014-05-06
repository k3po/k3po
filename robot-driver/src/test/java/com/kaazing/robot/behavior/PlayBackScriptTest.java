/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;

import static org.jboss.netty.channel.Channels.pipeline;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.junit.After;
import org.junit.Test;

import com.kaazing.netty.channel.UpstreamChannelStateEventTest;
import com.kaazing.robot.behavior.handler.LogLastEventHandler;
import com.kaazing.robot.behavior.visitor.GatherStreamsLocationVisitor.StreamResultLocationInfo;
import com.kaazing.robot.lang.LocationInfo;

public class PlayBackScriptTest {

    /* In order to test our ability to gather the unexpected event we mock up some events and make
     * sure they show up in the observed script.
     */
    private static void sendClosedEventUpstream(LocationInfo streamLocation) {
        sendEventUpStream(streamLocation, ChannelState.OPEN, false);
    }

    private static void sendOpenEventUpstream(LocationInfo streamLocation) {
        sendEventUpStream(streamLocation, ChannelState.OPEN, true);
    }

    private static void sendEventUpStream(LocationInfo streamLocation, ChannelState state, Object value) {

        LogLastEventHandler handler = new LogLastEventHandler(streamLocation);

        ChannelPipeline pipeline = pipeline(handler);

        ChannelFactory channelFactory = new DefaultLocalClientChannelFactory();
        Channel channel = channelFactory.newChannel(pipeline);

        pipeline.sendUpstream(new UpstreamChannelStateEventTest(channel, state, value));
    }

    @After
    public void tearDown() {
        //Not totally necessary but cleaner
        LogLastEventHandler.clear();
    }

    @Test
    public void resultScriptEqualsOriginalScriptOnSuccess() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                new LocationInfo(1, 0),
                new LocationInfo(4, 0),
                new LocationInfo(4, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void connectFailWriteWithWhiteSpaceOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "read \"M\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream1 = new LocationInfo(1, 0);
        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(5, 0),
                    new LocationInfo(2, 0)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(1, 0), new RuntimeException("Fake Failure"));

        //Send unexpected event upstream
        sendClosedEventUpstream(stream1);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "CLOSED\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);

    }



    @Test
    public void testWithTabsSuccess() {

        // Test more than start,end,observed at column 0
        // @formatter:off
        String script =
                "    connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "        closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 4),
                    new LocationInfo(4, 8),
                    new LocationInfo(4, 8)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void testEOFWithNoNewLine() {

       // Test no ending new line
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(4, 0),
                    new LocationInfo(4, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void trailingWhiteSpaceOk() {

       // Test no ending new line
        // @formatter:off
        String script =
                "connect tcp://localhost:8080    \n" +
                "connected\t\n" +
                "close\n" +
                "closed\t    \n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(4, 0),
                    new LocationInfo(4, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void testFailOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream1 = new LocationInfo(1, 0);
        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(4, 0),
                    new LocationInfo(3, 0)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(1, 0), new RuntimeException("Fake Failure"));

        //Send unexpected event upstream
        sendOpenEventUpstream(stream1);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "OPEN\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void testFailWithTabOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "\tclose\t\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream1 = new LocationInfo(1, 0);
        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(4, 0),
                    new LocationInfo(3, 1)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(1, 0), new RuntimeException("Fake Failure"));
        //Send unexpected event upstream
        sendOpenEventUpstream(stream1);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "\tclose\t\n" +
                "OPEN\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void resultScriptEqualsOriginalScriptWithCommentsSuccess() {
        // @formatter:off
        String script =
                "#Start #" +
                "connect tcp://localhost:8080\n" +
                "connected #foo\n" +
                "#comment\n" +
                "close\n" +
                "#comment\n" +
                "closed\n" +
                "#End\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 0),
                    new LocationInfo(7, 0),
                    new LocationInfo(7, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void failCaseWithComments() {
        // @formatter:off
        String script =
                "#Start #\n" +
                "connect tcp://localhost:8080\n" +
                "connected #foo\n" +
                "#comment\n" +
                "close\n" +
                "#comment\n" +
                "closed\n" +
                "#End\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream = new LocationInfo(2, 0);
        l.add(new StreamResultLocationInfo(
                    stream,
                    new LocationInfo(7, 0),
                    new LocationInfo(5, 0)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(2, 0), new RuntimeException("Fake Failure"));

        //Send an unexpected open event upstream
        sendOpenEventUpstream(stream);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "#Start #\n" +
                "connect tcp://localhost:8080\n" +
                "connected #foo\n" +
                "#comment\n" +
                "close\n" +
                "#comment\n" +
                "OPEN\n" +
                "#End\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void moreThanOneStream() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(4, 0),
                    new LocationInfo(4, 0)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(5, 0),
                new LocationInfo(8, 0),
                new LocationInfo(8, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void moreThanOneStreamComments() {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(7, 2),
                    new LocationInfo(7, 2)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(9, 1),
                new LocationInfo(14, 1),
                new LocationInfo(14, 1)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void moreThanOneStreamCommentsFirstFail() {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream1 = new LocationInfo(2, 1);
        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(7, 2),
                    new LocationInfo(3, 0)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(9, 1),
                new LocationInfo(14, 1),
                new LocationInfo(14, 1)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(2, 1), new RuntimeException("Fake Failure"));

        //Send unexpected closed upstream to both streams
        sendClosedEventUpstream(stream1);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tCLOSED\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void moreThanOneStreamCommentsBothFail() {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        LocationInfo stream1 = new LocationInfo(2, 1);
        LocationInfo stream2 = new LocationInfo(9, 1);

        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(7, 2),
                    new LocationInfo(3, 0)
));

        l.add(new StreamResultLocationInfo(
                stream2,
                new LocationInfo(14, 1),
                new LocationInfo(10, 2)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(2, 1), new RuntimeException("Fake Failure"));
        failedLocations.put(new LocationInfo(9, 1), new RuntimeException("Fake Failure"));

        //Send unexpected closed upstream to both streams
        sendClosedEventUpstream(stream1);
        sendClosedEventUpstream(stream2);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tCLOSED\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tCLOSED\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void moreThanOneStreamCommentsSecondFail() {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(7, 2),
                    new LocationInfo(7, 2)));

        LocationInfo stream2 = new LocationInfo(9, 1);

        l.add(new StreamResultLocationInfo(
                stream2,
                new LocationInfo(14, 1),
                new LocationInfo(10, 2)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(9, 1), new RuntimeException("Fake Failure"));

        //Send unexpected closed upstream
        sendClosedEventUpstream(stream2);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect tcp://localhost:8080 #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect tcp://localhost:8081\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tCLOSED\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void acceptSuccess() {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(5, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(2, 0),
                new LocationInfo(5, 0),
                new LocationInfo(5, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);
    }

    @Test
    public void acceptCommentsSuccess() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(10, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(4, 0),
                new LocationInfo(10, 0),
                new LocationInfo(10, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);
    }

    @Test
    public void acceptFailOk() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream2 = new LocationInfo(4, 0);
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(10, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                stream2,
                new LocationInfo(10, 0),
                new LocationInfo(6, 0)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(4, 0), new RuntimeException("Fake Failure"));
        //Send unexpected event upstream
        sendClosedEventUpstream(stream2);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "CLOSED\n" +
                "#comment #5\n";


        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void acceptTwoSuccess() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(18, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(4, 0),
                new LocationInfo(10, 0),
                new LocationInfo(10, 0)
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(12, 0),
                new LocationInfo(18, 0),
                new LocationInfo(18, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void acceptFirstFailOk() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();



        LocationInfo stream2Start = new LocationInfo(4, 0);

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(18, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                stream2Start,
                new LocationInfo(10, 0),
                new LocationInfo(6, 0)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(12, 0),
                new LocationInfo(18, 0),
                new LocationInfo(18, 0)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(4, 0), new RuntimeException("Fake Failure"));

        //Send an unexpected event upstream for stream 2.
        sendClosedEventUpstream(stream2Start);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "CLOSED\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void acceptSecondFailOk() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream3 = new LocationInfo(12, 0);

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(18, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(4, 0),
                new LocationInfo(10, 0),
                new LocationInfo(10, 0)
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(12, 0),
                new LocationInfo(18, 0),
                new LocationInfo(14, 0)
));
        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(12, 0), new RuntimeException("Fake Failure"));
        //Send unexpected event upstream
        sendClosedEventUpstream(stream3);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "CLOSED\n" +
                "#comment #5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void acceptAllFailOk() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        LocationInfo stream2 = new LocationInfo(4, 0);
        LocationInfo stream3 = new LocationInfo(12, 0);

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(18, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                stream2,
                new LocationInfo(10, 0),
                new LocationInfo(6, 0)
));
        l.add(new StreamResultLocationInfo(
                stream3,
                new LocationInfo(18, 0),
                new LocationInfo(14, 0)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(4, 0), new RuntimeException("Fake Failure"));
        failedLocations.put(new LocationInfo(12, 0), new RuntimeException("Fake Failure"));

        sendClosedEventUpstream(stream2);
        sendClosedEventUpstream(stream3);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "CLOSED\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "CLOSED\n" +
                "#comment #5\n";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void acceptAndConnectSuccess() {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept tcp://localhost:8080 #commentagain\n" +
                "\t#comment #1\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "accepted\n" +
                "#comment #2\n" +
                "connected\n" +
                "#comment #3\n" +
                "close\n" +
                "#comment #4\n" +
                "closed\n" +
                "#comment #5\n" +
                "connect foobar:///foo\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 1),
                    new LocationInfo(18, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(4, 0),
                new LocationInfo(10, 0),
                new LocationInfo(10, 0)
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(12, 0),
                new LocationInfo(18, 0),
                new LocationInfo(18, 0)
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(20, 0),
                new LocationInfo(23, 0),
                new LocationInfo(23, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void middleStreamFailsOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(4, 0),
                    new LocationInfo(4, 0)
));
        // Middle stream never ran. So no StreamResult for it
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(9, 0),
                    new LocationInfo(12, 0),
                    new LocationInfo(12, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();



        String expectedScript =
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void FirstStreamFailsOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(5, 0),
                    new LocationInfo(8, 0),
                    new LocationInfo(8, 0)
));

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(9, 0),
                    new LocationInfo(12, 0),
                    new LocationInfo(12, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();



        String expectedScript =
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void LastStreamFailsOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                new LocationInfo(1, 0),
                new LocationInfo(4, 0),
                new LocationInfo(4, 0)
));

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(5, 0),
                    new LocationInfo(8, 0),
                    new LocationInfo(8, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                " \n" +
                "\n" +
                "\n" +
                "\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void middleStreamFailsWithCommentsOk() {
        // @formatter:off
        String script =
                "#Start Stream 1\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#Stream 2\n" +
                "connect tcp://localhost:8082\n" +
                "connected\n" +
                "#Mid stream 2\n" +
                "close\n" +
                "closed\n" +
                "#Stream 3\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#DONE\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 0),
                    new LocationInfo(5, 0),
                    new LocationInfo(5, 0)
));

        // Middle stream never ran. So no StreamResult for it
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(13, 0),
                    new LocationInfo(16, 0),
                    new LocationInfo(16, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // The engine can't tell that #Stream 3 should be there.
        String expectedScript =
                "#Start Stream 1\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#Stream 2\n" +
                "connect tcp://localhost:8083\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#DONE\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void SkipStreamThenAcceptOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "accept tcp://localhost:8082\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(4, 0),
                    new LocationInfo(4, 0)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(9, 0),
                new LocationInfo(13, 0),
                null
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(10, 0),
                new LocationInfo(13, 0),
                new LocationInfo(13, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "accept tcp://localhost:8082\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void SkipStreamThenAcceptWithCommentsOk() {
        // @formatter:off
        String script =
                "#Stream #1\n" +
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #2\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #3\n" +
                "accept tcp://localhost:8082\n" +
                "#Stream #4\n" +
                "\n" +
                "accepted\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(2, 0),
                    new LocationInfo(6, 0),
                    new LocationInfo(6, 0)
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(15, 0),
                new LocationInfo(22, 0),
                null
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(18, 0),
                new LocationInfo(22, 0),
                new LocationInfo(22, 0)
));


        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // It would be nice if #Stream #3 occurred before the last accept. But since the entire middle connect
        // stream is skipped. There is no way to know that the comment belongs to the next stream or the stream that
        // was being skipped. This test case isn't really going to happen in reality though.
        String expectedScript =
                "#Stream #1\n" +
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #2\n" +
                "accept tcp://localhost:8082\n" +
                "#Stream #4\n" +
                "\n" +
                "accepted\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void acceptNoStreamsOk() {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(5, 0),
                    null
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "accept tcp://localhost:8080\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void TwoacceptNoStreamsOk() {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(5, 0),
                    null
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(6, 0),
                new LocationInfo(10, 0),
                null
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "accept tcp://localhost:8080\n" +
 "accept tcp://localhost:8080\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void connectAndAcceptNoStreamsOk() {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(5, 0),
                    null
));

        l.add(new StreamResultLocationInfo(
                new LocationInfo(6, 0),
                new LocationInfo(9, 0),
                new LocationInfo(9, 0)
));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        String expectedScript =
                "accept tcp://localhost:8080\n" +
                "connect tcp://localhost:8081\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void connectNoOneHomeOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        LocationInfo stream1 = new LocationInfo(1, 0);
        l.add(new StreamResultLocationInfo(
                    stream1,
                    new LocationInfo(4, 0),
                    new LocationInfo(1, 0)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(1, 0), new RuntimeException("Fake Failure"));

        sendOpenEventUpstream(stream1);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "OPEN\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void emptyScriptOK() {
        // @formatter:off
        String script =
                "";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>(0);

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        assertEquals(script, resultScript);

    }

    @Test
    public void scriptWithNoLocationsOk() {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>(0);

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        String expectedScript = "";

        assertEquals(expectedScript, resultScript);

    }

    @Test
    public void canEchoWrongOK() throws Exception {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "read \"ello\"\n" +
                "closed\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "write \"Hello\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();

        LocationInfo stream2 = new LocationInfo(2, 0);

        l.add(new StreamResultLocationInfo(
                    new LocationInfo(1, 0),
                    new LocationInfo(5, 0),
                    null
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(2, 0),
                new LocationInfo(5, 0),
                new LocationInfo(3, 0)
));
        l.add(new StreamResultLocationInfo(
                new LocationInfo(7, 0),
                new LocationInfo(11, 0),
                new LocationInfo(11, 0)
));

        Map<LocationInfo, Throwable> failedLocations = new HashMap<LocationInfo, Throwable>();
        failedLocations.put(new LocationInfo(2, 0), new RuntimeException("Fake Failure"));

        //Send unexpected event
        sendOpenEventUpstream(stream2);

        PlayBackScript o = new PlayBackScript(script, l, failedLocations);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "OPEN\n" +
                "#Connect channel\n" +
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "write \"Hello\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void canSkipFirstAcceptStreamAndSecondStream() throws Exception {
        // @formatter:off
        String script =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(new LocationInfo(1, 0), new LocationInfo(5, 0), null));
        l.add(new StreamResultLocationInfo(new LocationInfo(2, 0), new LocationInfo(5, 0), new LocationInfo(2, 0)));
        l.add(new StreamResultLocationInfo(new LocationInfo(6, 0), new LocationInfo(8, 0), new LocationInfo(6, 0)));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connect tcp://localhost:8080\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);
    }

    @Test
    public void canSkipFirstConnectStreamAndSecondStream() throws Exception {
        // @formatter:off
        String script =
                "connect tcp://localhost:8080\n" +
                "connected\n" +
                "closed\n" +
                "accept tcp://localhost:8080\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        List<StreamResultLocationInfo> l = new ArrayList<StreamResultLocationInfo>();
        l.add(new StreamResultLocationInfo(new LocationInfo(1, 0), new LocationInfo(3, 0), new LocationInfo(1, 0)));
        l.add(new StreamResultLocationInfo(new LocationInfo(4, 0), new LocationInfo(8, 0), null));
        l.add(new StreamResultLocationInfo(new LocationInfo(5, 0), new LocationInfo(8, 0), new LocationInfo(5, 0)));

        PlayBackScript o = new PlayBackScript(script, l);
        String resultScript = o.createPlayBackScript();

        // @formatter:off
        String expectedScript =
                "connect tcp://localhost:8080\n" +
                "accept tcp://localhost:8080\n" +
                "accepted\n";
        // @formatter:on

        assertEquals(expectedScript, resultScript);
    }

}
