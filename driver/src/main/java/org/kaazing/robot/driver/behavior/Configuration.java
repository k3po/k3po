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

package org.kaazing.robot.driver.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.el.ExpressionFactory;

import org.kaazing.robot.driver.netty.bootstrap.ClientBootstrap;
import org.kaazing.robot.driver.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.driver.behavior.handler.CompletionHandler;
import org.kaazing.robot.lang.el.ExpressionContext;
import org.kaazing.robot.lang.el.SetExpressionValueContext;

public class Configuration {

    private List<ServerBootstrap> serverBootstraps;
    private List<ClientBootstrap> clientBootstraps;
    private List<CompletionHandler> completionHandlers;
    private Set<Barrier> barriers;
    private org.kaazing.robot.lang.el.SetExpressionValueContext setExpresionContext;
    private org.kaazing.robot.lang.el.ExpressionContext expresionContext;
    private ExpressionFactory factory;

    public List<CompletionHandler> getCompletionHandlers() {
        if (completionHandlers == null) {
            completionHandlers = new ArrayList<CompletionHandler>();
        }
        return completionHandlers;
    }

    public SetExpressionValueContext getSetExpressionContext() {
        if (setExpresionContext == null) {
            setExpresionContext = new SetExpressionValueContext();
        }
        return setExpresionContext;
    }

    public ExpressionContext getIntExpressionContext() {
        return getSetExpressionContext().getIntegerContext();
    }

    public ExpressionContext getByteArrayExpressionContext() {
        return getSetExpressionContext().getByteArrayContext();
    }

    public ExpressionContext getExpressionContext() {
        if (expresionContext == null) {
            expresionContext = new ExpressionContext();
        }
        return expresionContext;
    }

    public ExpressionFactory getExpressionFactory() {
        if (factory == null) {
            factory = ExpressionFactory.newInstance();
        }

        return factory;
    }

    public List<ServerBootstrap> getServerBootstraps() {
        if (serverBootstraps == null) {
            serverBootstraps = new ArrayList<ServerBootstrap>();
        }

        return serverBootstraps;
    }

    public List<ClientBootstrap> getClientBootstraps() {
        if (clientBootstraps == null) {
            clientBootstraps = new ArrayList<ClientBootstrap>();
        }

        return clientBootstraps;
    }

    public Set<Barrier> getBarriers() {
        if (barriers == null) {
            barriers = new HashSet<Barrier>();
        }
        return barriers;
    }

    public void cancel() {

    }

}
