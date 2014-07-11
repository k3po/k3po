/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.el.ExpressionFactory;

import org.kaazing.netty.bootstrap.ClientBootstrap;
import org.kaazing.netty.bootstrap.ServerBootstrap;
import org.kaazing.robot.behavior.handler.CompletionHandler;
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
