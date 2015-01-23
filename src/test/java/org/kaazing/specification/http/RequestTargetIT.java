/**
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
package org.kaazing.specification.http;

import org.junit.Test;

/**
 * rfc7230#section-5.3
 *
 */
public class RequestTargetIT {

	@Test
	public void upstreamMustAcceptOriginForm() {
		// origin-form = absolute-path [ "?" query ]
		// GET /where?q=now HTTP/1.1
		// Host: www.example.org
	}

	@Test
	public void upstreamMustAcceptAbsoluteForm() {
		// GET http://www.example.org/pub/WWW/TheProject.html HTTP/1.1
	}

	@Test
	public void intermediaryMustAcceptAuthorityFormOnConnectRequest() {
		// CONNECT www.example.com:80 HTTP/1.1
	}

	@Test
	public void upstreamMustAcceptAsterickFormOnOptionsRequest() {
		// OPTIONS * HTTP/1.1
	}

	@Test
	public void lastProxyMustConvertOptionsInAbsoluteFormToAsterickForm() {
		// OPTIONS * HTTP/1.1
		// Host: www.example.org:8001
	}
}
