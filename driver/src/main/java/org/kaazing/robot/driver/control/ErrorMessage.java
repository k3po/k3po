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

package org.kaazing.robot.driver.control;

public class ErrorMessage extends ControlMessage {

    private String summary;
    private String description;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashTo();

        hashCode <<= 8;
        hashCode ^= (description != null) ? description.hashCode() : 0;

        hashCode <<= 8;
        hashCode ^= (summary != null) ? summary.hashCode() : 0;

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof ErrorMessage) && equals((ErrorMessage) obj);
    }

    protected final boolean equals(ErrorMessage that) {
        return super.equalTo(that)
                && (this.summary == that.summary || (this.summary != null && this.summary.equals(that.summary)))
                && (this.description == that.description || (this.description != null && this.description
                        .equals(that.description)));
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }
}
