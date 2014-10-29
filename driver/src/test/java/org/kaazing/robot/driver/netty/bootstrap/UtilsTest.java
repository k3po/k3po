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

package org.kaazing.robot.driver.netty.bootstrap;

import static org.junit.Assert.assertSame;

import javax.annotation.Resource;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void shouldInjectExactTypeMatch() {
        Injectable injectable = new Injectable();
        InjectionTargetForExactTypeMatch injectionTarget = new InjectionTargetForExactTypeMatch();
        Utils.inject(injectionTarget, Injectable.class, injectable);

        assertSame(injectable, injectionTarget.injectable);
    }

    @Test
    public void shouldInjectSuperTypeMatch() {
        InjectableSubType injectable = new InjectableSubType();
        InjectionTargetForSuperTypeMatch injectionTarget = new InjectionTargetForSuperTypeMatch();
        Utils.inject(injectionTarget, InjectableSubType.class, injectable);

        assertSame(injectable, injectionTarget.injectable);
    }

    // Support/helper classes

    public static class Injectable {
    }

    public static class InjectableSubType extends Injectable {
    }

    public static class InjectionTargetForExactTypeMatch {
        Injectable injectable;

        @Resource
        public void setInjectable(Injectable injectable) {
            this.injectable = injectable;
        }
    }

    public static class InjectionTargetForSuperTypeMatch {
        Injectable injectable;

        @Resource(type = InjectableSubType.class)
        public void setInjectable(Injectable injectable) {
            this.injectable = injectable;
        }
    }
}
