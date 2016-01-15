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
package org.kaazing.k3po.pcap.converter.internal.author.emitter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class GenericEmitter implements Emitter {
    private StringBuilder buffer = new StringBuilder();
    private File outputFile;
    
    protected GenericEmitter(File outputFilePath){
        this.outputFile = outputFilePath;
    }
    
    public void add(String str){
        buffer.append(str);
    }
    
    public void clearBuffer() {
        buffer = new StringBuilder(); 
    }
    
    public String getBuffer() {
        return buffer.toString();
    }

    public void commitToFile() {
        try {
            if(buffer.length() != 0){
                FileUtils.deleteQuietly(outputFile);        //Clean File
                FileUtils.write(outputFile, buffer);
            }else{
                FileUtils.deleteQuietly(outputFile);        //just to have no empty files
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            new RuntimeException("Failed to flush rupert script to file: " + outputFile
                    + e.getMessage());
        }
    }
}
