/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.netty.jmock;

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
            catch (SecurityException e) {
                throw new RuntimeException(e);

            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);

            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException(e);

            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        super.assertIsSatisfied();
    }
}
