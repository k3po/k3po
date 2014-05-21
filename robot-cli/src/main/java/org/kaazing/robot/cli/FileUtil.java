/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.cli;

import java.io.File;
import java.security.InvalidParameterException;

public final class FileUtil {

    private FileUtil() {

    }

    public static void deleteFile(File file) throws InvalidParameterException {
        if (file == null) {
            throw new InvalidParameterException();
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    deleteFile(child);
                }
            }
            file.delete();
        }
    }
}
