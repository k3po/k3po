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

import static java.util.Collections.emptyMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class FileScriptParserTypeSystem implements ScriptParserTypeSystemSpi
{
    private final Map<String, Class<?>> acceptOptionTypes;
    private final Map<String, Class<?>> connectOptionTypes;
    private final Map<String, Class<?>> readOptionTypes;
    private final Map<String, Class<?>> writeOptionTypes;
    private final Map<String, List<String>> readConfigTypeNames;
    private final Map<String, List<String>> writeConfigTypeNames;

    FileScriptParserTypeSystem()
    {
        Map<String, Class<?>> acceptOptionTypes = new LinkedHashMap<>();
        acceptOptionTypes.put("mode", String.class);
        acceptOptionTypes.put("size", long.class);
        this.acceptOptionTypes = acceptOptionTypes;

        Map<String, Class<?>> connectOptionTypes = new LinkedHashMap<>();
        connectOptionTypes.put("mode", String.class);
        connectOptionTypes.put("size", long.class);
        connectOptionTypes.put("timeout", long.class);
        this.connectOptionTypes = connectOptionTypes;

        this.readOptionTypes = emptyMap();
        this.writeOptionTypes = emptyMap();
        this.readConfigTypeNames = emptyMap();
        this.writeConfigTypeNames = emptyMap();
    }

    @Override
    public String getName()
    {
        return "file";
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
