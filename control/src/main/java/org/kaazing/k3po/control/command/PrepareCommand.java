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

package org.kaazing.k3po.control.command;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class PrepareCommand extends Command {

    private final List<String> names;

    public PrepareCommand() {
        this.names = new LinkedList<>();
    }

    public void setName(String name) {
        this.names.clear();
        this.names.add(name);
    }

    public void setNames(List<String> names) {
        this.names.clear();
        this.names.addAll(names);
    }

    public List<String> getNames() {
        return names;
    }

    public Kind getKind() {
        return Kind.PREPARE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKind(), names);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof PrepareCommand && equalTo((PrepareCommand) o);
    }

    protected boolean equalTo(PrepareCommand that) {
        return super.equalTo(that) && Objects.equals(this.names, that.names);
    }
}
