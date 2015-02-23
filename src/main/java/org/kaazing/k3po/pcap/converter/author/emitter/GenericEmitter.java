/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.k3po.pcap.converter.author.emitter;

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
