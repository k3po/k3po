/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.composer;

import org.kaazing.robot.tcpconverter.rptScriptsCreator.SupportedProtocol;

public interface ComposerFactory {

    Composer getComposer(SupportedProtocol sp, ComposerType et, String endpointIp, String identifier);

}
