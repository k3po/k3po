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

package org.kaazing.robot.lang.ast;

import static org.kaazing.robot.lang.ast.util.AstUtil.equivalent;

import org.kaazing.robot.lang.LocationInfo;

public abstract class AstNode {

    private LocationInfo locationInfo;

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(int line, int column) {
        this.locationInfo = new LocationInfo(line, column);
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public abstract <R, P> R accept(Visitor<R, P> visitor, P parameter) throws Exception;

    public abstract int hashCode();

    public abstract boolean equals(Object obj);

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        formatNode(sb);
        return sb.toString();
    }

    protected int hashTo() {
        return (locationInfo != null) ? locationInfo.hashCode() : 0;
    }

    protected final boolean equalTo(AstNode that) {
        return equivalent(this.locationInfo, that.locationInfo);
    }

    protected void formatNode(StringBuilder sb) {
        if (locationInfo != null) {
            sb.append(String.format("[%03d:%02d] ", locationInfo.line, locationInfo.column));
        }
        else {
            sb.append("         ");
        }
    }

    public interface Visitor<R, P> {
        R visit(AstScriptNode node, P parameter) throws Exception;
        R visit(AstAcceptNode node, P parameter) throws Exception;
        R visit(AstAcceptableNode node, P parameter) throws Exception;
        R visit(AstConnectNode node, P parameter) throws Exception;

        R visit(AstFlushNode node, P parameter) throws Exception;
        R visit(AstWriteValueNode node, P parameter) throws Exception;
        R visit(AstWriteCloseNode node, P parameter) throws Exception;
        R visit(AstDisconnectNode node, P parameter) throws Exception;
        R visit(AstUnbindNode node, P parameter) throws Exception;
        R visit(AstCloseNode node, P parameter) throws Exception;

        R visit(AstChildOpenedNode node, P parameter) throws Exception;
        R visit(AstChildClosedNode node, P parameter) throws Exception;
        R visit(AstOpenedNode node, P parameter) throws Exception;
        R visit(AstBoundNode node, P parameter) throws Exception;
        R visit(AstConnectedNode node, P parameter) throws Exception;
        R visit(AstReadValueNode node, P parameter) throws Exception;
        R visit(AstDisconnectedNode node, P parameter) throws Exception;
        R visit(AstUnboundNode node, P parameter) throws Exception;
        R visit(AstReadClosedNode node, P parameter) throws Exception;
        R visit(AstClosedNode node, P parameter) throws Exception;

        R visit(AstReadAwaitNode node, P parameter) throws Exception;
        R visit(AstWriteAwaitNode node, P parameter) throws Exception;
        R visit(AstReadNotifyNode node, P parameter) throws Exception;
        R visit(AstWriteNotifyNode node, P parameter) throws Exception;

        R visit(AstReadConfigNode node, P parameter) throws Exception;
        R visit(AstWriteConfigNode node, P parameter) throws Exception;

        R visit(AstReadOptionNode node, P parameter) throws Exception;
        R visit(AstWriteOptionNode node, P parameter) throws Exception;
    }
}
