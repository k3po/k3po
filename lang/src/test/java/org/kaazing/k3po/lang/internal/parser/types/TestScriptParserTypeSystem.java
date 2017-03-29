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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TestScriptParserTypeSystem implements ScriptParserTypeSystemSpi
{
    private final Map<String, Class<?>> acceptOptionTypes;
    private final Map<String, Class<?>> connectOptionTypes;
    private final Map<String, Class<?>> readOptionTypes;
    private final Map<String, Class<?>> writeOptionTypes;
    private final Map<String, List<String>> readConfigTypeNames;
    private final Map<String, List<String>> writeConfigTypeNames;

    public TestScriptParserTypeSystem()
    {
        Map<String, Class<?>> acceptOptionTypes = new LinkedHashMap<>();
        acceptOptionTypes.put("transport", URI.class);
        acceptOptionTypes.put("string", String.class);
        acceptOptionTypes.put("bytes", byte[].class);
        acceptOptionTypes.put("number", int.class);
        acceptOptionTypes.put("expression", Object.class);
        this.acceptOptionTypes = acceptOptionTypes;

        Map<String, Class<?>> connectOptionTypes = new LinkedHashMap<>();
        connectOptionTypes.put("transport", URI.class);
        connectOptionTypes.put("string", String.class);
        connectOptionTypes.put("bytes", byte[].class);
        connectOptionTypes.put("number", int.class);
        connectOptionTypes.put("expression", Object.class);
        this.connectOptionTypes = connectOptionTypes;

        this.readOptionTypes = singletonMap("option", byte[].class);
        this.writeOptionTypes = singletonMap("option", byte[].class);
        this.readConfigTypeNames = singletonMap("config", asList(null, null));
        this.writeConfigTypeNames = singletonMap("config", asList(null, null));
    }

    @Override
    public String getName()
    {
        return "test";
    }

    @Override
    public Class<?> acceptOptionType(
        String optionName)
    {
        return acceptOptionTypes.get(optionName);
    }

    @Override
    public Class<?> connectOptionType(
        String optionName)
    {
        return connectOptionTypes.get(optionName);
    }

    @Override
    public Class<?> readOptionType(
        String optionName)
    {
        return readOptionTypes.get(optionName);
    }

    @Override
    public Class<?> writeOptionType(
        String optionName)
    {
        return writeOptionTypes.get(optionName);
    }

    @Override
    public List<String> readConfigTypeNames(
        String configName)
    {
        return readConfigTypeNames.get(configName);
    }

    @Override
    public List<String> writeConfigTypeNames(
        String configName)
    {
        return writeConfigTypeNames.get(configName);
    }
}
