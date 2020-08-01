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
package org.kaazing.k3po.driver.internal.behavior;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstRegion;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.ast.AstStreamNode;
import org.kaazing.k3po.lang.internal.parser.ScriptParser;
import org.kaazing.k3po.lang.internal.parser.ScriptParserImpl;

public class ScriptProgressTest {

    @Test
    public void observedScriptEqualsScriptOnSuccess() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void connectFailWriteWithWhiteSpaceOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "read \"M\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion readAST = connectAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(readAST.getRegionInfo(), "closed");

        String observedScript = progress.getObservedScript();
        // @formatter:off
        String expectedScript =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "closed\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void testWithTabsSuccess() throws Exception {

        // Test more than start,end,observed at column 0
        // @formatter:off
        String script =
                "\tconnect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "\t\tclosed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void testEOFWithNoNewLine() throws Exception {

        // Test no ending new line
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void trailingWhiteSpaceOk() throws Exception {

        // Test no ending new line
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'    \n" +
                "connected\t\n" +
                "close\n" +
                "closed\t    \n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void testFailOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion closedAST = connectAST.getStreamables().get(2);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closedAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        String expectedScript = "connect 'tcp://localhost:8080'\n" + "connected\n" + "close\n" + "OPEN\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void testFailWithTabOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "\tclose\t\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion closedAST = connectAST.getStreamables().get(2);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closedAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        String expectedScript = "connect 'tcp://localhost:8080'\n" + "connected\n" + "\tclose\t\n" + "OPEN\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void resultScriptEqualsOriginalScriptWithCommentsSuccess() throws Exception {
        // @formatter:off
        String script =
                "#Start #\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected #foo\n" +
                "#comment\n" +
                "close\n" +
                "#comment\n" +
                "closed\n" +
                "#End\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void failCaseWithComments() throws Exception {
        // @formatter:off
        String script =
                "#Start #\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected #foo\n" +
                "#comment\n" +
                "close\n" +
                "#comment\n" +
                "closed\n" +
                "#End\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion closedAST = connectAST.getStreamables().get(2);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closedAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "#Start #\n" + "connect 'tcp://localhost:8080'\n" + "connected #foo\n" + "#comment\n" + "close\n" + "#comment\n"
                        + "OPEN\n" + "#End\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void moreThanOneStream() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void moreThanOneStreamComments() throws Exception {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void moreThanOneStreamCommentsFirstFail() throws Exception {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion closeAST = connectAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tCLOSED\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void moreThanOneStreamCommentsBothFail() throws Exception {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectOneAST = scriptAST.getStreams().get(0);
        AstRegion closeOneAST = connectOneAST.getStreamables().get(1);
        AstStreamNode connectTwoAST = scriptAST.getStreams().get(1);
        AstRegion closeTwoAST = connectTwoAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeOneAST.getRegionInfo(), "CLOSED");
        progress.addScriptFailure(closeTwoAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tCLOSED\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tCLOSED\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void moreThanOneStreamCommentsSecondFail() throws Exception {
        // @formatter:off
        String script =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tclose\n" +
                "#comment 6\n" +
                "\tclosed\n" +
                "\t#comment 5\n";
        // @formatter:on
        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(1);
        AstRegion closeAST = connectAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "#Comment 1\n" +
                "\tconnect 'tcp://localhost:8080' #comment 2\n" +
                "connected\n" +
                "\t#comment 3\n" +
                "\tclose\n" +
                "#comment 8\n" +
                "\tclosed\n" +
                "\t#comment 4\n" +
                "\tconnect 'tcp://localhost:8081'\n" +
                "\tconnected\n" +
                "#comment 7\n" +
                "\tCLOSED\n" +
                "\t#comment 5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void acceptSuccess() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void acceptCommentsSuccess() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void acceptFailOk() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(1);
        AstRegion closeAST = acceptedAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "# Accept script\n" + "\taccept 'tcp://localhost:8080' #commentagain\n" + "\t#comment #1\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "CLOSED\n" + "#comment #5\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void acceptTwoSuccess() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void acceptFirstFailOk() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(1);
        AstRegion closeAST = acceptedAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "# Accept script\n" + "\taccept 'tcp://localhost:8080' #commentagain\n" + "\t#comment #1\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "CLOSED\n" + "#comment #5\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "close\n" + "#comment #4\n" + "closed\n"
                        + "#comment #5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void acceptSecondFailOk() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(2);
        AstRegion closeAST = acceptedAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "# Accept script\n" + "\taccept 'tcp://localhost:8080' #commentagain\n" + "\t#comment #1\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "close\n" + "#comment #4\n" + "closed\n"
                        + "#comment #5\n" + "accepted\n" + "#comment #2\n" + "connected\n" + "#comment #3\n" + "CLOSED\n"
                        + "#comment #5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void acceptAllFailOk() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedOneAST = scriptAST.getStreams().get(1);
        AstRegion closeOneAST = acceptedOneAST.getStreamables().get(1);
        AstStreamNode acceptedTwoAST = scriptAST.getStreams().get(2);
        AstRegion closeTwoAST = acceptedTwoAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closeOneAST.getRegionInfo(), "CLOSED");
        progress.addScriptFailure(closeTwoAST.getRegionInfo(), "CLOSED");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "# Accept script\n" + "\taccept 'tcp://localhost:8080' #commentagain\n" + "\t#comment #1\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "CLOSED\n" + "#comment #5\n" + "accepted\n"
                        + "#comment #2\n" + "connected\n" + "#comment #3\n" + "CLOSED\n" + "#comment #5\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void acceptAndConnectSuccess() throws Exception {
        // @formatter:off
        String script =
                "# Accept script\n" +
                "\taccept 'tcp://localhost:8080' #commentagain\n" +
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
                "connect 'foobar:///foo'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void middleStreamFailsOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect refused\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void firstStreamFailsOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "connect refused\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void lastStreamFailsOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(2);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect refused\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void middleStreamFailsWithCommentsOk() throws Exception {
        // @formatter:off
        String script =
                "#Start Stream 1\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#Stream 2\n" +
                "connect 'tcp://localhost:8082'\n" +
                "connected\n" +
                "#Mid stream 2\n" +
                "close\n" +
                "closed\n" +
                "#Stream 3\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#DONE\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        // The engine can't tell that #Stream 3 should be there.
        String expectedScript =
                "#Start Stream 1\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#Stream 2\n" +
                "connect refused\n" +
                "#Stream 3\n" +
                "connect 'tcp://localhost:8083'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "#DONE\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void skipStreamThenAcceptOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "accept 'tcp://localhost:8082'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect refused\n" +
                "accept 'tcp://localhost:8082'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void skipStreamThenAcceptWithCommentsOk() throws Exception {
        // @formatter:off
        String script =
                "#Stream #1\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #2\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #3\n" +
                "accept 'tcp://localhost:8082'\n" +
                "#Stream #4\n" +
                "\n" +
                "accepted\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo(), "connect refused");

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "#Stream #1\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n" +
                "#Stream #2\n" +
                "connect refused\n" +
                "#Stream #3\n" +
                "accept 'tcp://localhost:8082'\n" +
                "#Stream #4\n" +
                "\n" +
                "accepted\n" +
                "connected\n" +
                "#Middle Comment\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void acceptNoStreamsOk() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(acceptedAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "accept 'tcp://localhost:8080'\n" +
                "\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void twoAcceptNoStreamsOk() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedOneAST = scriptAST.getStreams().get(1);
        AstStreamNode acceptedTwoAST = scriptAST.getStreams().get(3);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(acceptedOneAST.getRegionInfo());
        progress.addScriptFailure(acceptedTwoAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        String expectedScript = "accept 'tcp://localhost:8080'\n" + "\n" + "accept 'tcp://localhost:8080'\n" + "\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void connectAndAcceptNoStreamsOk() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(acceptedAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        String expectedScript =
                "accept 'tcp://localhost:8080'\n" +
                "\n" +
                "connect 'tcp://localhost:8081'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void connectNoOneHomeOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion connectedAST = connectAST.getStreamables().get(0);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectedAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "connect 'tcp://localhost:8080'\n" +
                "OPEN\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void emptyScriptOK() throws Exception {
        // @formatter:off
        String script =
                "";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();
        ScriptProgress progress = new ScriptProgress(scriptInfo, script);

        String observedScript = progress.getObservedScript();

        assertEquals(script, observedScript);

    }

    @Test
    public void scriptWithAllFailedOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        String expectedScript = "\n";

        assertEquals(expectedScript, observedScript);

    }

    @Test
    public void canEchoWrongOK() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "read \"ello\"\n" +
                "closed\n" +
                "#Connect channel\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "write \"Hello\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(1);
        AstRegion readAST = acceptedAST.getStreamables().get(1);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(readAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "OPEN\n" +
                "#Connect channel\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "write \"Hello\"\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void canSkipFirstAcceptStreamAndSecondStream() throws Exception {
        // @formatter:off
        String script =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n" +
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectOneAST = scriptAST.getStreams().get(1);
        AstRegion connectedOneAST = connectOneAST.getStreamables().get(0);
        AstStreamNode connectTwoAST = scriptAST.getStreams().get(2);
        AstRegion connectedTwoAST = connectTwoAST.getStreamables().get(0);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectedOneAST.getRegionInfo());
        progress.addScriptFailure(connectedTwoAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "\n" +
                "connect 'tcp://localhost:8080'\n" +
                "\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void canSkipFirstConnectStreamAndSecondStream() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "closed\n" +
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on

        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion connectedOneAST = connectAST.getStreamables().get(0);
        AstStreamNode acceptedAST = scriptAST.getStreams().get(2);
        AstRegion connectedTwoAST = acceptedAST.getStreamables().get(0);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(connectedOneAST.getRegionInfo());
        progress.addScriptFailure(connectedTwoAST.getRegionInfo());

        String observedScript = progress.getObservedScript();

        // @formatter:off
        String expectedScript =
                "connect 'tcp://localhost:8080'\n" +
                "\n" +
                "accept 'tcp://localhost:8080'\n" +
                "accepted\n" +
                "\n";
        // @formatter:on

        assertEquals(expectedScript, observedScript);
    }

    @Test
    public void testCacheResultOk() throws Exception {
        // @formatter:off
        String script =
                "connect 'tcp://localhost:8080'\n" +
                "connected\n" +
                "close\n" +
                "closed\n";
        // @formatter:on
        ScriptParser parser = new ScriptParserImpl();
        AstScriptNode scriptAST = parser.parse(script);
        AstStreamNode connectAST = scriptAST.getStreams().get(0);
        AstRegion closedAST = connectAST.getStreamables().get(2);

        RegionInfo scriptInfo = scriptAST.getRegionInfo();

        ScriptProgress progress = new ScriptProgress(scriptInfo, script);
        progress.addScriptFailure(closedAST.getRegionInfo(), "OPEN");

        String observedScript = progress.getObservedScript();

        String expectedScript = "connect 'tcp://localhost:8080'\n" + "connected\n" + "close\n" + "OPEN\n";

        assertEquals(expectedScript, observedScript);
        // failed scripts aren't idempotent on internal implementation
        // but should return the same observed script
        observedScript = progress.getObservedScript();
        assertEquals(expectedScript, observedScript);

    }

}
