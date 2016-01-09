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
import java.util.HashMap;
import java.util.Stack;

import org.kaazing.k3po.pcap.converter.internal.author.RptScriptsCreatorFailureException;

public class EmitterFactoryImpl implements EmitterFactory {

    private final String SEP = System.getProperty("file.separator");
    private final String CNT_SEP = "-";
    private final String DEFAULT_NOTE_FILENAME = "README";
    private boolean memSaver = false;

    private final String BASE_DIR = "target" + SEP + "rpt-scripts" + SEP;
    // Removed Because we are now just recording low level, but this structure maybe useful in the future if we
    // go back and want it to be more complicated
//    private final String CLIENT_DIR = "client" + SEP;
//    private final String SERVER_DIR = "server" + SEP;
//    private final String FRAGMENT_DIR = "fragment" + SEP;
//    private final String TCP_DIR = "tcp" + SEP;

    private final String RUPERT_SCRIPT_ENDING = ".rpt";

    private final HashMap<OutputType, Emitter> notes = new HashMap<>();
    
    public void setMemSaver(boolean memSaver){
    	this.memSaver = memSaver;
    }

    @Override
    public Emitter getRptScriptEmitter(OutputType ot, String name) {
        if(ot != OutputType.TCP_CLIENT_SCRIPT && ot != OutputType.TCP_SERVER_SCRIPT){
            return new NullEmitter(memSaver);
        }
        return getRptScriptEmitterWithUniqueOutputPath(getOutputFilePath(ot, name));
    }

    @Override
    public Emitter getNoteEmitter(OutputType ot, String noteHeader) {
        if(ot != OutputType.TCP_CLIENT_SCRIPT && ot != OutputType.TCP_SERVER_SCRIPT){
            return new NullEmitter(memSaver);
        }
        if ( !notes.containsKey(ot) ) {
            Emitter emitter = new GenericEmitter(new File(getOutputFilePath(ot, DEFAULT_NOTE_FILENAME)));
            emitter.add(noteHeader);
            emitter.add("\n");
            emitter.add("-----------------------------------------------------------------");
            emitter.add("\n\n");
            emitter.commitToFile();
            notes.put(ot, emitter);
        }
        return notes.get(ot);
    }

    final private static Stack<String> fileAlreadyCreated = new Stack<>();
    private GenericEmitter getRptScriptEmitterWithUniqueOutputPath(String suggestedPathName) {
        int cnt = 1;
        File outputFile = new File(suggestedPathName + CNT_SEP + cnt + RUPERT_SCRIPT_ENDING);
        while (fileAlreadyCreated.contains(outputFile.getAbsolutePath())) {
            outputFile = new File(suggestedPathName + CNT_SEP + cnt++ + RUPERT_SCRIPT_ENDING);
        }
        fileAlreadyCreated.add(outputFile.getAbsolutePath());
        return new GenericEmitter(outputFile);
    }
    
    private String getOutputFilePath(OutputType ot, String name){
        if ( name == null ){
            throw new RptScriptsCreatorFailureException("Attempting to create file with no name " + ot);
    	}
        return BASE_DIR + name;
    }
}
