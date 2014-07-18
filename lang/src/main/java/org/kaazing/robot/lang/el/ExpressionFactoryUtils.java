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
