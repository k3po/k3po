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


package org.kaazing.k3po.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class ListedEventRunnable implements Runnable {
    private final ArrayList<Step> steps;
    private final ConcurrentLinkedDeque<Exception> errors;

    public ListedEventRunnable() {
        steps = new ArrayList<Step>();
        errors = new ConcurrentLinkedDeque<Exception>();
    }

    @Override
    public void run() {
        Iterator<Step> iter = steps.iterator();
        while (iter.hasNext()) {
            Step step = iter.next();
            try {
                step.run();
            } catch (Exception e) {
                if (errors != null) {
                    errors.add(e);
                }
                break;
            }
        }
        cleanUp();
    }

    protected abstract void cleanUp();

    public ConcurrentLinkedDeque<Exception> getErrors() {
        return errors;
    }

    protected void addStep(Step step) {
        steps.add(step);
    }

    public abstract static class Step {
        public abstract void run() throws Exception;
    }

}
