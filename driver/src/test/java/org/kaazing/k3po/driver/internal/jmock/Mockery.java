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
package org.kaazing.k3po.driver.internal.jmock;

import java.lang.reflect.Field;

public class Mockery extends org.jmock.Mockery {

    private boolean throwFirstErrorOnAssertIsSatisfied;

    public boolean isThrowFirstErrorOnAssertIsSatisfied() {
        return throwFirstErrorOnAssertIsSatisfied;
    }

    public void setThrowFirstErrorOnAssertIsSatisfied(boolean throwFirstErrorOnAssertIsSatisfied) {
        this.throwFirstErrorOnAssertIsSatisfied = throwFirstErrorOnAssertIsSatisfied;
    }

    @Override
    public void assertIsSatisfied() {
        if (isThrowFirstErrorOnAssertIsSatisfied()) {
            try {
                Field firstErrorField = org.jmock.Mockery.class.getDeclaredField("firstError");
                firstErrorField.setAccessible(true);
                Throwable firstError = Throwable.class.cast(firstErrorField.get(this));
                if (firstError != null) {
                    throw new RuntimeException(firstError);
                }

            }
            catch (SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        super.assertIsSatisfied();
    }
}
