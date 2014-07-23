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

package org.kaazing.robot.driver.jmock;

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
