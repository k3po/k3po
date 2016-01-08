/*
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kaazing.k3po.control.internal.event;

import static org.kaazing.k3po.control.internal.event.CommandEvent.Kind.DISPOSED;

import java.util.Objects;

/**
 * StartedEvent.
 *
 */
public final class DisposedEvent extends CommandEvent {

    @Override
    public Kind getKind() {
        return DISPOSED;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind());
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DisposedEvent && equalTo((DisposedEvent) o);
    }

}
