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

package org.kaazing.robot.driver.behavior.parser;

import java.io.InputStream;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.robot.driver.behavior.visitor.AssociateStreamsVisitor;
import org.kaazing.robot.driver.behavior.visitor.InjectBarriersVisitor;
import org.kaazing.robot.driver.behavior.visitor.InjectEventsVisitor;
import org.kaazing.robot.driver.behavior.visitor.InjectHttpStreamsVisitor;
import org.kaazing.robot.driver.behavior.visitor.ValidateBarriersVisitor;
import org.kaazing.robot.lang.ast.AstScriptNode;
import org.kaazing.robot.lang.parser.ScriptParseException;
import org.kaazing.robot.lang.parser.ScriptParser;
import org.kaazing.robot.lang.parser.ScriptParserImpl;

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

            InjectEventsVisitor injectEvents = new InjectEventsVisitor();
            InjectEventsVisitor.State injectEventsState = new InjectEventsVisitor.State();
            script = script.accept(injectEvents, injectEventsState);

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

            InjectHttpStreamsVisitor injectHttpEvents = new InjectHttpStreamsVisitor();
            InjectHttpStreamsVisitor.State injectHttpEventsState = new InjectHttpStreamsVisitor.State();
            script = script.accept(injectHttpEvents, injectHttpEventsState);

            if (isDebugEnabled) {
                LOGGER.debug("validate http events visitor done. Result: " + script);
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
