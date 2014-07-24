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

package org.kaazing.robot.driver.control.handler;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import org.kaazing.robot.driver.control.PrepareMessage;
import org.kaazing.robot.driver.control.StartMessage;

public class ControlDecoderCompatibility extends ControlUpstreamHandler {

    @Override
    public void prepareReceived(final ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        PrepareMessage prepare = (PrepareMessage) evt.getMessage();
        switch (prepare.getCompatibilityKind()) {
        case PREPARE:
            super.prepareReceived(ctx, evt);
            break;
        case START:
            // propagate PREPARE transformed from START
            super.prepareReceived(ctx, evt);

            // inject implicit START message
            StartMessage start = new StartMessage();
            start.setScriptName(prepare.getScriptName());
            fireMessageReceived(ctx, start);

            break;
        default:
            break;
        }
    }

}
