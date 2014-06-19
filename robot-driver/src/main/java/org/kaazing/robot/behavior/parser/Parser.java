/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior.parser;

import java.io.InputStream;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.behavior.visitor.AssociateStreamsVisitor;
import org.kaazing.robot.behavior.visitor.InjectBarriersVisitor;
import org.kaazing.robot.behavior.visitor.InjectEventsVisitor;
import org.kaazing.robot.behavior.visitor.InjectHttpEventsVisitor;
import org.kaazing.robot.behavior.visitor.ValidateBarriersVisitor;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.parser.ScriptParseException;
import org.kaazing.robot.lang.parser.ScriptParser;
import org.kaazing.robot.lang.parser.v2.ScriptParserImpl;

public class Parser implements ScriptParser {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Parser.class);

    private final ScriptParser parser;

    public Parser() {
        parser = new ScriptParserImpl();
    }

    @Override
    public AstScriptNode parse(InputStream input) throws ScriptParseException {
        boolean isDebugEnabled = LOGGER.isDebugEnabled();

        try {
            if (isDebugEnabled) {
                LOGGER.debug("parsing script");
            }

            AstScriptNode script = parser.parse(input);

            if (isDebugEnabled) {
                LOGGER.debug("done parsing script. Result: " + script);
            }

            InjectHttpEventsVisitor injectHttpEvents = new InjectHttpEventsVisitor();
            InjectHttpEventsVisitor.State injectHttpEventsState = new InjectHttpEventsVisitor.State();
            script = script.accept(injectHttpEvents, injectHttpEventsState);
            injectHttpEventsState.finish();

            if (isDebugEnabled) {
                LOGGER.debug("inject http events visitor done. Result: " + script);
            }

            InjectEventsVisitor injectEvents = new InjectEventsVisitor();
            InjectEventsVisitor.State injectEventsState = new InjectEventsVisitor.State();
            script = script.accept(injectEvents, injectEventsState);
            injectEventsState.finish();

            if (isDebugEnabled) {
                LOGGER.debug("inject events visitor done. Result: " + script);
            }

            InjectBarriersVisitor injectBarriers = new InjectBarriersVisitor();
            script = script.accept(injectBarriers, new InjectBarriersVisitor.State());

            if (isDebugEnabled) {
                LOGGER.debug("inject barriers visitor done. Result: " + script);
            }

            AssociateStreamsVisitor associateStreams = new AssociateStreamsVisitor();
            script = script.accept(associateStreams, new AssociateStreamsVisitor.State());

            if (isDebugEnabled) {
                LOGGER.debug("associate visitor done. Result: " + script);
            }

            ValidateBarriersVisitor validateBarriers = new ValidateBarriersVisitor();
            script.accept(validateBarriers, new ValidateBarriersVisitor.State());

            if (isDebugEnabled) {
                LOGGER.debug("validate barrier visitor done. Result: " + script);
            }

            return script;

        } catch (ScriptParseException e) {
            if (isDebugEnabled) {
                LOGGER.error("Script parse exception", e);
            } else {
                LOGGER.error("Script parse exception: " + e);
            }
            throw e;
        } catch (Exception e) {
            if (isDebugEnabled) {
                LOGGER.error("caught exception parsing script", e);
            } else {
                LOGGER.error("caught exception parsing script: " + e);
            }
            throw new ScriptParseException(e);
        }
    }
}
