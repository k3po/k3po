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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public final class ScriptParserTypeSystem {

    private final Map<String, ScriptParserTypeSystemSpi> typeSystemsByName;
    private final ScriptParserTypeSystemSpi emptyTypeSystem;
    private final ScriptParserTypeSystemSpi defaultTypeSystem;

    private ScriptParserTypeSystem(Map<String, ScriptParserTypeSystemSpi> typeSystemsByName) {
        this.typeSystemsByName = typeSystemsByName;
        this.emptyTypeSystem = new EmptyScriptParserTypeSystem();
        this.defaultTypeSystem = new DefaultScriptParserTypeSystem();
    }

    public Class<?> acceptOptionType(String optionName) {
        optionName = resolveOption(optionName);
        Class<?> optionType;
        if (optionName.indexOf(':') == -1) {
            optionType = defaultTypeSystem.acceptOptionType(optionName);
        }
        else {
            String[] nameParts = optionName.split(":");
            optionType = typeSystem(nameParts[0]).acceptOptionType(nameParts[1]);
        }
        return verifyOption(optionType, optionName);
    }

    public Class<?> connectOptionType(String optionName) {
        optionName = resolveOption(optionName);
        Class<?> optionType;
        if (optionName.indexOf(':') == -1) {
            optionType = defaultTypeSystem.connectOptionType(optionName);
        }
        else {
            String[] nameParts = optionName.split(":");
            optionType = typeSystem(nameParts[0]).connectOptionType(nameParts[1]);
        }
        return verifyOption(optionType, optionName);
    }

    public Class<?> readOptionType(String optionName) {
        optionName = resolveOption(optionName);
        Class<?> optionType;
        if (optionName.indexOf(':') == -1) {
            optionType = defaultTypeSystem.readOptionType(optionName);
        }
        else {
            String[] nameParts = optionName.split(":");
            optionType = typeSystem(nameParts[0]).readOptionType(nameParts[1]);
        }
        return verifyOption(optionType, optionName);
    }

    public Class<?> writeOptionType(String optionName) {
        optionName = resolveOption(optionName);
        Class<?> optionType;
        if (optionName.indexOf(':') == -1) {
            optionType = defaultTypeSystem.writeOptionType(optionName);
        }
        else {
            String[] nameParts = optionName.split(":");
            optionType = typeSystem(nameParts[0]).writeOptionType(nameParts[1]);
        }
        return verifyOption(optionType, optionName);
    }

    public List<String> readConfigTypeNames(String configName) {
        configName = resolveConfig(configName);
        List<String> typeNames;
        if (configName.indexOf(':') == -1) {
            typeNames = defaultTypeSystem.readConfigTypeNames(configName);
        }
        else {
            String[] nameParts = configName.split(":");
            typeNames = typeSystem(nameParts[0]).readConfigTypeNames(nameParts[1]);
        }
        return verifyConfig(typeNames, configName);
    }

    public List<String> writeConfigTypeNames(String configName) {
        configName = resolveConfig(configName);
        List<String> typeNames;
        if (configName.indexOf(':') == -1) {
            typeNames = defaultTypeSystem.writeConfigTypeNames(configName);
        }
        else {
            String[] nameParts = configName.split(":");
            typeNames = typeSystem(nameParts[0]).writeConfigTypeNames(nameParts[1]);
        }
        return verifyConfig(typeNames, configName);
    }

    private static String resolveConfig(
        String configName)
    {
        // backwards compatibility
        List<String> compatibles = asList("request", "host", "content-length", "method", "header", "parameter", "status", "version", "trailer");
        if (compatibles.contains(configName)) {
            configName = "http:" + configName;
        }

        return configName;
    }

    private static String resolveOption(
        String optionName)
    {
        // backwards compatibility
        if (asList("chunkExtension").contains(optionName)) {
            optionName = "http:" + optionName;
        }

        return optionName;
    }

    private static <T> T verifyOption(
        T value,
        String optionName)
    {
        if (value == null) {
            throw new IllegalArgumentException("Unrecognized option: " + optionName);
        }
        return value;
    }

    private static <T> T verifyConfig(
        T value,
        String optionName)
    {
        if (value == null) {
            throw new IllegalArgumentException("Unrecognized config: " + optionName);
        }
        return value;
    }

    private ScriptParserTypeSystemSpi typeSystem(String name) {
        return typeSystemsByName.getOrDefault(name, emptyTypeSystem);
    }

    public static final ScriptParserTypeSystem newInstance() {
        final Map<String, ScriptParserTypeSystemSpi> servicesByName = new LinkedHashMap<>();

        // TODO: move to http transport, discover via service loader
        ScriptParserTypeSystemSpi httpTypeSystem = new HttpScriptParserTypeSystem();
        servicesByName.put(httpTypeSystem.getName(), httpTypeSystem);

        // TODO: move to file transport, discover via service loader
        ScriptParserTypeSystemSpi fileTypeSystem = new FileScriptParserTypeSystem();
        servicesByName.put(fileTypeSystem.getName(), fileTypeSystem);

        for (ScriptParserTypeSystemSpi service : ServiceLoader.load(ScriptParserTypeSystemSpi.class)) {
            servicesByName.put(service.getName(), service);
        }
        return new ScriptParserTypeSystem(servicesByName);
    }
}
