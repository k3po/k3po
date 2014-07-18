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

package org.kaazing.robot.cli;

import java.io.File;

public abstract class AbstractInterpreter implements Interpreter {
    private File outputDir;

    public AbstractInterpreter() {
        this(new File("robot-cli-out"));
    }

    public AbstractInterpreter(File defaultOutputDir) {
        this.outputDir = defaultOutputDir;
    }

    @Override
    public File getOutputDir() {
        return outputDir;
    }

    @Override
    public void setOutputDir(String name) throws Exception {
        outputDir = new File(name);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new Exception("Failed to create output directory");
            }
        }
    }
}
