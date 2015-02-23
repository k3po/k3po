/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package org.kaazing.robot.tcpconverter.rptScriptsCreator.coordinator;

import org.kaazing.robot.tcpconverter.rptScriptsCreator.ConversationId;

public interface CoordinatorFactory {

    public Coordinator getCoordinator(ConversationId cID);

}
