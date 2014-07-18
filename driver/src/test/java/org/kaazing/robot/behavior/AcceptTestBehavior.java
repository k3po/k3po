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

package org.kaazing.robot.behavior;

public class AcceptTestBehavior {
    // extends BehaviorContext {
    //
    // private static final Logger logger =
    // Logger.getLogger(AcceptTestBehavior.class);
    //
    // private static final String DEFAULT_NAME = "AcceptTestBehavior";
    //
    // // XXX Fix the handling of ChannelGroups
    // private static final ChannelGroup allChannels = new
    // DefaultChannelGroup("test.accept-behavior");
    //
    // private String name = DEFAULT_NAME;
    // private boolean prepared = false;
    //
    // public AcceptTestBehavior(UUID guid,
    // URI uri)
    // throws Exception {
    //
    // super(guid, uri);
    // }
    //
    // @Override
    // public String getName() {
    // return name;
    // }
    //
    // @Override
    // public void setName(String name) {
    // this.name = name;
    // }
    //
    // @Override
    // public void setUp()
    // throws Exception {
    //
    // // If already prepared, do nothing
    // if (prepared == true) {
    // return;
    // }
    //
    // Class<? extends ChannelFactory> channelFactoryClass = null;
    //
    // String className = transport.getAcceptSocketFactoryClassName();
    // Class<?> acceptFactoryClass = Class.forName(className);
    // channelFactoryClass = (Class<? extends ChannelFactory>)
    // acceptFactoryClass;
    //
    // ChannelFactory factory = null;
    //
    // try {
    // Constructor constructor = acceptFactoryClass.getConstructor(new Class[] {
    // Executor.class, Executor.class });
    //
    // Executor bossExecutor = Executors.newCachedThreadPool();
    // Executor workerExecutor = Executors.newCachedThreadPool();
    // factory = (ChannelFactory) constructor.newInstance(bossExecutor,
    // workerExecutor);
    //
    // } catch (NoSuchMethodException nsme) {
    // // Try obtaining an instance using the no-arg constructor
    // factory = channelFactoryClass.newInstance();
    // }
    //
    // setBootstrap(new ServerBootstrap(factory));
    // setTransportAcceptOptions();
    //
    // if (transport.isLocalTransport()) {
    // // The 'local' transport uses in-VM pipes, and thus Netty has
    // // a special LocalAddress class to represent this "address".
    // LocalAddress localAddr = new LocalAddress(uri.getSchemeSpecificPart());
    // getBootstrap().setOption("localAddress", localAddr);
    //
    // } else {
    // // Server sockets only need to set the local address.
    //
    // InetSocketAddress localAddr = new InetSocketAddress(uri.getHost(),
    // uri.getPort());
    // getBootstrap().setOption("localAddress", localAddr);
    // }
    //
    // getBootstrap().setPipelineFactory(getDefaultPipelineFactory());
    // prepared = true;
    // }
    //
    // @Override
    // public void tearDown()
    // throws Exception {
    //
    // // XXX Fix handling of ChannelGroups. Do NOT use awaitUninterruptibly
    // // without a timeout.
    // //ChannelGroupFuture future = allChannels.close();
    // //future.awaitUninterruptibly();
    //
    // Bootstrap boot = getBootstrap();
    // if (boot != null) {
    // boot.releaseExternalResources();
    // }
    // }
    //
    // @Override
    // public ChannelFuture start()
    // throws Exception {
    //
    // started = System.currentTimeMillis();
    //
    // ServerBootstrap server = (ServerBootstrap) getBootstrap();
    // return Channels.succeededFuture(server.bind());
    // }
}
