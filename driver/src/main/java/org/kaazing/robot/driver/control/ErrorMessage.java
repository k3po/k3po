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

import java.util.Objects;

public final class ErrorMessage extends ControlMessage {

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
        return Objects.hash(getKind(), description, summary);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof ErrorMessage) && equalTo((ErrorMessage) obj);
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    protected boolean equalTo(ErrorMessage that) {
        return super.equalTo(that) && Objects.equals(this.summary, that.summary)
                && Objects.equals(this.description, that.description);
    }
}
