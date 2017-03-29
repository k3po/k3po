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

final class EmptyScriptParserTypeSystem implements ScriptParserTypeSystemSpi
{
    @Override
    public String getName()
    {
        return "$empty";
    }

    @Override
    public Class<?> acceptOptionType(
        String optionName)
    {
        return null;
    }

    @Override
    public Class<?> connectOptionType(
        String optionName)
    {
        return null;
    }

    @Override
    public Class<?> readOptionType(
        String optionName)
    {
        return null;
    }

    @Override
    public Class<?> writeOptionType(
        String optionName)
    {
        return null;
    }

    @Override
    public List<String> readConfigTypeNames(
        String configName)
    {
        return null;
    }

    @Override
    public List<String> writeConfigTypeNames(
        String configName)
    {
        return null;
    }
}
