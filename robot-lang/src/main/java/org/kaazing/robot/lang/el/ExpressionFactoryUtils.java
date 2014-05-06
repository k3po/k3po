/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.lang.el;

import java.util.Properties;

import javax.el.ExpressionFactory;

/**
 * Provides an expression factory utilizing the type converter the robot needs
 *
 * TODO: We use the SPI model to create the factory. However, we depend on the
 * JUEL specific implementation that will call our type converter. I think we
 * either need to decide to extend or use the SPI. We really can't use both I
 * don't think.
 */
public final class ExpressionFactoryUtils {

    public static ExpressionFactory newExpressionFactory() {

        // JRF: recommend moving this to an ExpressionFactory implementation of
        // our own that extends the JUEL ExpressionFactoryImpl
        // and register only our ExpressionFactory implementation class name in
        // META-INF/services/javax.el.ExpressionFactory
        // note: this requires changing the JUEL dependency to prevent it from
        // registering their ExpressionFactoryImpl in
        // META-INF/services/javax.el.ExpressionFactory automatically
        Properties props = new Properties();

        props.setProperty("de.odysseus.el.misc.TypeConverter", ByteArrayTypeConverter.class.getName());

        return ExpressionFactory.newInstance(props);
    }

    private ExpressionFactoryUtils() {
        // utility class
    }
}
