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
package org.kaazing.k3po.lang.internal.el;

import java.util.Properties;
import java.util.function.Supplier;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

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

        props.setProperty("de.odysseus.el.misc.TypeConverter", TypeConverterImpl.class.getName());

        return ExpressionFactory.newInstance(props);
    }

    private ExpressionFactoryUtils() {
        // utility class
    }

    @SuppressWarnings("unchecked")
    public static <T> T synchronizedValue(ValueExpression expression, ExpressionContext environment, Class<T> expectedType) {
        // ValueExpression.getValue(...) is not thread-safe
        synchronized (environment) {
            return (T) expression.getValue(environment);
        }
    }

    public static <T> Supplier<T> synchronizedSupplier(ValueExpression expression, ExpressionContext environment, Class<T> expectedType) {
        return () -> synchronizedValue(expression, environment, expectedType);
    }
}
