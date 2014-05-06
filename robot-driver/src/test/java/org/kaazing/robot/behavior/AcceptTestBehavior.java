/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
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
