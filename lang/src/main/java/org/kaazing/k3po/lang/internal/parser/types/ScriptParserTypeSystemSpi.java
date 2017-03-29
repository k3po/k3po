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
package org.kaazing.k3po.lang.internal.parser.types;

import java.util.List;

public interface ScriptParserTypeSystemSpi {

    String getName();

    Class<?> acceptOptionType(String optionName);

    Class<?> connectOptionType(String optionName);

    Class<?> readOptionType(String optionName);

    Class<?> writeOptionType(String optionName);

    List<String> readConfigTypeNames(String configName);

    List<String> writeConfigTypeNames(String configName);
}