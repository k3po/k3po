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
package org.kaazing.k3po.driver.internal.netty.bootstrap;

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
