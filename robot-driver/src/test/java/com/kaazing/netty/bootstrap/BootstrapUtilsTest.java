/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.netty.bootstrap;

import static org.junit.Assert.assertSame;

import javax.annotation.Resource;

import org.junit.Test;

public class BootstrapUtilsTest {

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
