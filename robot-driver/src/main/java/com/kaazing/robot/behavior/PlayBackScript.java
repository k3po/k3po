/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */

package com.kaazing.robot.behavior;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.kaazing.robot.behavior.handler.LogLastEventHandler;
import com.kaazing.robot.behavior.visitor.GatherStreamsLocationVisitor.StreamResultLocationInfo;
import com.kaazing.robot.lang.LocationInfo;

// TODO: Re-write this monstrosity! :)

// @formatter:off
/**
 * Given an original script and a list of StreamResultLocationInfo's
 * PlayBackScript will construct the 'observed' script.
 *
 * PlayBackScript pbs = new PlayBackScript(
 * "connect tcp:...\nconnected\nclose\nclosed\n", streamResultLocInfoList );
 * String observedScript = pbs.createPlayBackScript;
 *
 * A StreamResultLocation info provides the locations of a stream in the
 * original script as well as the last location successfully executed, in the
 * form of start, end, and observed end LocationInfos. A StreamResultLocation
 * info with an observed point of null is special. It represents an accept
 * stream.
 *
 * The StreamResultLocationInfo list is expected to be in order from the first
 * stream to the last stream in the script. If not an assertion error will
 * occur.
 *
 * When observed.equals(end) the stream executed to completion and the stream is
 * copied verbatim into the observed script (start to end).
 *
 * When observed < end the stream only partially executed. The script is copied
 * verbatim from start to observed inclusive. Lines from observed + 1 to end are
 * omitted. However, the first line non-comment line will be replaced with the observed
 * deviated behavior: So for example given the script:
 *

 *
 * Assume that the last thing we did successfully was connected and a CLOSED
 * event was received before the write. There will be only 1
 * StreamResultLocationInfo for this script (since there is only one stream), it
 * would contain:
 *
 * start = 1:0 end = 6:0 observed = 2:0
 *
 * The resulting script will look like this
 *
 * connect tcp://localhost:8080\n
 * connected\n
 * CLOSED\n
 *
 *
 * In between streams and at the beginning and end of the script, white space
 * and comments are copied verbatim.
 *
 * When a stream fails entirely (say bind for an accept fails). We should not
 * have a StreamResultLocationInfo for the stream. The stream will be 'skipped'
 * in the same manner as described above when a stream was partially executed
 * (white space and comment lines are copied only).
 *
 * Today there is only support for Unix line ending in the script.
 */
/*
 * We apply a state pattern to do this work. We have four states BlankState,
 * CopyScriptState, SkipScriptState, and FinishState. Each state has a
 * transition method. The transition method will read the inputScript starting
 * at the last read location until it determines a transition is needed. It will
 * return a result string (observed) during that state transition. The
 * PlayBackScript simply acts as a state context and the createPlayBackScript
 * transitions states one at a time until the finish state is reached.
 *
 * BlankState -> This is the state we will be 'in between' streams and before
 * the first one. It simply copies the script verbatim until it sees a non-white
 * space character that isn't inside of a comment.
 *
 * CopyScriptState -> This is the state we will be in when we have found an
 * executed stream. We copy bytes verbatim in this state.
 *
 * SkipScriptState -> This is the state we will be in when a stream or some
 * portion of a stream was not observed. In this state ignore the original script characters
 *
 * FinishState -> In this state we have seen all observed streams. In some cases
 * there will be text at the stream that we have not read. Comments, whitespace,
 * and perhaps not executed streams. In this state we copy white space and
 * comment lines, ignoring other characters up to the end of the starting
 * script.
 */
// @formatter:on
public class PlayBackScript {

    public static final char COMMENT = '#';

    private final String startScript;
    private State my_state;
    private boolean inComment;
    private int atLine;
    private int lastColumnAt;
    private final Iterator<StreamResultLocationInfo> currentLocationIterator;
    private final Map<LocationInfo, Throwable> failedLocations;

    public PlayBackScript(String startScript, Iterable<StreamResultLocationInfo> results) {
        this(startScript, results, new HashMap<LocationInfo, Throwable>(0, 100));
    }

    public PlayBackScript(String startScript, Iterable<StreamResultLocationInfo> results,
            Map<LocationInfo, Throwable> failedLocations) {
        this.startScript = startScript;
        this.failedLocations = failedLocations;
        atLine = 0;
        currentLocationIterator = results.iterator();
        lastColumnAt = -1;
        setInComment(false);
        if (!currentLocationIterator.hasNext()) {
            /* If we have no locations then we are already done */
            setState(new FinishState(this.startScript, true));
        } else {
            StreamResultLocationInfo currentLocation = currentLocationIterator.next();
            setState(new BlankState(this.startScript, currentLocation, currentLocation.start));
        }
    }

    public String createPlayBackScript() {

        StringBuilder result = new StringBuilder();

        /* Transition until we find ourselves in the finish state */
        while (!(my_state instanceof FinishState)) {
            result.append(my_state.transition());
        }

        // Need to finish with the FinishState
        return result.append(my_state.transition()).toString();
    }

    private void setState(final State s) {
        my_state = s;
    }

    private boolean isInComment() {
        return inComment;
    }

    private void setInComment(boolean val) {
        inComment = val;
    }

    private interface State {
        String transition();
    }

    private abstract class AbstractState implements State {

        protected String originalScriptFragment;
        protected final StringBuilder observedStream = new StringBuilder();
        protected LocationInfo lookingForLocation;
        protected StreamResultLocationInfo currentStream;
        protected LocationInfo deviatedStreamStartLoc;
        protected boolean switchStates;
        protected boolean lineJustWhiteSpace;

        /*
         * Currently we always create this with a null originalScript. This is
         * because today we don't transition states immediately. We actually
         * read the remainder of the line before setting the new state.
         */
        public AbstractState(String originalScript, StreamResultLocationInfo loc, LocationInfo changeStateInfo) {
            this.originalScriptFragment = originalScript;
            this.lookingForLocation = changeStateInfo;
            this.currentStream = loc;
        }

        public void setOriginalScript(String s) {
            originalScriptFragment = s;
        }

        /**
         * Implement transitionIfNeeded, it should create a new State object
         * should a transition be needed.
         *
         * @return the new state if a transition is needed otherwise null.
         */
        protected abstract AbstractState transitionIfNeeded();

        protected boolean wantAppend(char c) {
            return true;
        }

        protected boolean appendIfWanted(char c) {
            if (wantAppend(c)) {
                observedStream.append(c);
                return true;
            }
            return false;
        }

        @Override
        public String transition() {

            assert originalScriptFragment != null : "NullPointer. originalScript not set";

            final int strLen = originalScriptFragment.length();

            AbstractState newState = null;
            lineJustWhiteSpace = true;

            // Loop through the originalScript starting at the beginning
            for (int currentIndex = 0; currentIndex < strLen; currentIndex++) {

                lastColumnAt++;
                final char c = originalScriptFragment.charAt(currentIndex);

                // Only support unix line endings for now.
                if (c == '\n') {

                    atLine++;
                    lastColumnAt = -1;

                    /*
                     * If this state transitioned due to a deviation, we must emit the deviation on the first non comment
                     * line.
                     */
                    if (deviatedStreamStartLoc != null && !isInComment() && !lineJustWhiteSpace) {
                        /*
                         * Add any deviation if needed. The purpose here is to replace the first command/event line with the
                         * deviation
                         */
                        Throwable failure = failedLocations.get(deviatedStreamStartLoc);
                        if (failure != null) {
                            // TODO: Eventually instead of LogLastEventHandler ... the thing we want to display will be in
                            // the failure we just retrieved
                            String lastEvent = LogLastEventHandler.getLastEvent(deviatedStreamStartLoc);
                            if (lastEvent != null) {
                                observedStream.append(lastEvent + "\n");
                            }
                        }
                        deviatedStreamStartLoc = null;
                    }

                    appendIfWanted(c);
                    // No multi-line comments.
                    setInComment(false);

                    // We don't set the newState until we get to the end of the
                    // line because we want to copy the last line seen to the
                    // result buffer
                    if (newState != null) {
                        currentIndex++;

                        // If we have reached the end of the originalScript. We
                        // probably have the finish state and there is no more
                        // script left.
                        newState.setOriginalScript(currentIndex < strLen ? originalScriptFragment.substring(currentIndex)
                                : null);
                        setState(newState);
                        return observedStream.toString();
                    }
                    lineJustWhiteSpace = true;
                } else if (!isInComment()) {
                    if (c == COMMENT) {
                        lineJustWhiteSpace = false;
                        setInComment(true);
                    } else if (!Character.isWhitespace(c)) {
                        lineJustWhiteSpace = false;
                        if (newState == null) {
                            newState = transitionIfNeeded();
                        }
                    }
                    appendIfWanted(c);
                } else { // it isn't a new line and we are in a comment ...
                    appendIfWanted(c);
                }
            }
            // If we go through the entire originalScript we have to be at the
            // finish state. Otherwise things are screwy.
            assert newState == null || newState instanceof FinishState : "End of input script before last end location";
            setState(new FinishState());
            return observedStream.toString();
        }
    }

    private final class BlankState extends AbstractState {

        private boolean appendAll;
        private StringBuilder skipedCharsOnLine = new StringBuilder();

        public BlankState(String originalScript, StreamResultLocationInfo loc, LocationInfo changeStateInfo) {
            super(originalScript, loc, changeStateInfo);
        }

        /*
         * Normally in the BlankState we are copying comments and whitespace only. Except when we are switching states
         */
         @Override
        protected boolean wantAppend(char c) {
            return appendAll || (!switchStates && (isInComment() || Character.isWhitespace(c)));
         }

        @Override
        protected boolean appendIfWanted(char c) {
            boolean appended = super.appendIfWanted(c);
            if (!appended) {
                skipedCharsOnLine.append(c);
            }
            if (c == '\n') {
                skipedCharsOnLine = new StringBuilder();
            }
            return appended;
        }

        @Override
        protected AbstractState transitionIfNeeded() {

            AbstractState newState = null;
            final LocationInfo currentLoc = new LocationInfo(atLine + 1, lastColumnAt);

            /* Ok. By definition, We are ready for a state transition. */
            switchStates = true;
            if (lookingForLocation.equals(currentLoc)) {
                appendAll = true;

                // We need to start copying text now. But we have to go grab any leading white space!
                if (lastColumnAt == 0) {
                    observedStream.append(skipedCharsOnLine);
                }
                skipedCharsOnLine = new StringBuilder();

                /*
                 * So if observed is not null we copy the stream up to the
                 * observed point. If observed equals current then there is just
                 * a single line in this stream. We will need to go look for the
                 * next stream or finish
                 */
                if (currentStream.observed != null && !currentStream.observed.equals(currentLoc)) {
                    newState = new CopyScriptState(null, currentStream, currentStream.observed);
                } else {
                    LocationInfo deviate = null;
                    if (currentStream.observed != null) {
                        deviate = currentStream.start;
                    }
                    /*
                     * If observed is null or observed=start then we transition
                     * back to the BlankState but with a new start location. In
                     * both cases we are either at a start of a stream (accept
                     * or connect). The observed location for an accept stream
                     * is really the set of observed locations of all its
                     * streams (accepted). So we treat this case for what it is
                     * between streams. So we go to the BlankState until the
                     * start of the next stream. Note that the next stream may
                     * not be for this accept. In this case no streams for the
                     * accept executed. Same for the connect case we will be
                     * moving to the next stream.
                     */

                    if (currentLocationIterator.hasNext()) {
                        currentStream = currentLocationIterator.next();
                        newState = new BlankState(null, currentStream, currentStream.start);
                    } else {
                        /*
                         * Then we are done. The last stream was an accept or connect and it didn't execute
                         */
                        newState = new FinishState(true);
                    }
                    if (deviate != null) {
                        newState.deviatedStreamStartLoc = deviate;
                    }

                }
            } else {
                /*
                 * So in this case. We have encountered a stream which we need
                 * to skip. Oh and the current location better not be past what
                 * we are looking for
                 */
                assert currentLoc.compareTo(lookingForLocation) < 0 : String.format(
                        "Current location %s is greater than changeStateInfo %s,", currentLoc, lookingForLocation);
                appendAll = false;
                newState = new SkipScriptState(null, currentStream, lookingForLocation);
            }
            return newState;
        }
    }

    private final class CopyScriptState extends AbstractState {

        public CopyScriptState(String originalScript, StreamResultLocationInfo loc, LocationInfo changeStateInfo) {
            super(originalScript, loc, changeStateInfo);
        }

        @Override
        protected AbstractState transitionIfNeeded() {

            AbstractState newState = null;
            final LocationInfo currentLoc = new LocationInfo(atLine + 1, lastColumnAt);

            if (lookingForLocation.equals(currentLoc)) {
                /* Ok the successful stream case */
                if (currentStream.observed.equals(currentStream.end)) {
                    switchStates = true;
                    /* Either we have more streams or not. */
                    if (currentLocationIterator.hasNext()) {
                        currentStream = currentLocationIterator.next();
                        newState = new BlankState(null, currentStream, currentStream.start);
                    } else {
                        newState = new FinishState();
                    }
                } else {
                    switchStates = true;
                    /* The unsuccessful stream. Skip the remainder of the stream */
                    newState = new SkipScriptState(null, currentStream, currentStream.end);
                    newState.deviatedStreamStartLoc = currentStream.start;
                }
            } else {
                /* Ok. So no transition is needed. */
                assert currentLoc.compareTo(lookingForLocation) < 0 : String.format(
                        "Current location %s is greater than changeStateInfo %s,", currentLoc, lookingForLocation);
            }
            return newState;
        }
    }

    private final class SkipScriptState extends AbstractState {

        private boolean appendAll;
        private StringBuilder skipedCharsOnLine = new StringBuilder();

        public SkipScriptState(String original, StreamResultLocationInfo loc, LocationInfo changeStateInfo) {
            super(original, loc, changeStateInfo);
        }

        /*
         * Normally in the SkipScriptState we are skipping everything ... except: At the start we emit comments and leading
         * white space until we have emitted our deviation script and we want to append everything when we are transitioning
         * states.
         */
        @Override
        protected boolean wantAppend(char c) {
            return appendAll
                    || (deviatedStreamStartLoc != null && (isInComment() || (lineJustWhiteSpace && Character.isWhitespace(c))));
        }

        @Override
        protected boolean appendIfWanted(char c) {
            boolean appended = super.appendIfWanted(c);
            if (!appended) {
                skipedCharsOnLine.append(c);
            }
            if (c == '\n') {
                skipedCharsOnLine = new StringBuilder();
            }
            return appended;
        }

        @Override
        protected AbstractState transitionIfNeeded() {

            final LocationInfo currentLoc = new LocationInfo(atLine + 1, lastColumnAt);
            AbstractState newState = null;

            /*
             * If we have found the location we are skipping to. We need to know
             * if we were skipping to the start or end of a stream so we know
             * which state to transition to.
             */
            if (lookingForLocation.equals(currentLoc)) {
                switchStates = true;
                // We need to start copying text now. But we have to go grab any leading white space!
                if (lastColumnAt == 0) {
                    observedStream.append(skipedCharsOnLine);
                }
                skipedCharsOnLine = new StringBuilder();
                /*
                 * In this case we were skipping an entire stream until the
                 * start of the next one
                 */
                if (lookingForLocation.equals(currentStream.start)) {
                    appendAll = true;

                    /*
                     * We are transitioning to copying. Or we are transitioning to the BlankState because we encountered an
                     * accept stream. See comment in BlankState class.
                     */
                    if (currentStream.observed != null && !currentStream.observed.equals(currentStream.start)) {
                        newState = new CopyScriptState(null, currentStream, currentStream.observed);
                    } else {
                        if (currentLocationIterator.hasNext()) {
                            currentStream = currentLocationIterator.next();
                            newState = new BlankState(null, currentStream, currentStream.start);
                        } else {
                            /*
                             * Then we are done. The last stream was an accept
                             * and none of its streams executed
                             */
                            newState = new FinishState(true);
                        }
                    }
                    /* Ok we were skipping to the end of a stream */
                } else if (lookingForLocation.equals(currentStream.end)) {
                    switchStates = true;
                    appendAll = false;
                    /* Either we are done or we are in between streams */
                    if (currentLocationIterator.hasNext()) {
                        currentStream = currentLocationIterator.next();
                        newState = new BlankState(null, currentStream, currentStream.start);
                    } else {
                        newState = new FinishState();
                    }

                } else {
                    /* We should never be skipping to the observed location */
                    assert false : "Invalid state. Can not skip up to observed location.";
                }
            } else {
                /* Otherwise we are till in skip mode */
                assert currentLoc.compareTo(lookingForLocation) < 0 : String.format(
                        "Current location |%s| greater than changeStateInfo |%s|", currentLoc, lookingForLocation);
            }
            return newState;
        }
    }

    private final class FinishState extends AbstractState {

        private boolean skipAll;

        public FinishState() {
            super(null, null, null);
        }

        public FinishState(String script) {
            super(script, null, null);
        }

        public FinishState(String script, boolean skipAll) {
            this(script);
            this.skipAll = skipAll;
        }

        public FinishState(boolean skipAll) {
            super(null, null, null);
            this.skipAll = skipAll;
        }

        @Override
        protected boolean wantAppend(char c) {
            return !skipAll && (isInComment() || Character.isWhitespace(c));
            // return false || isInComment();
        }

        // I suppose I should have FinishState have a different subclass
        @Override
        protected AbstractState transitionIfNeeded() {
            throw new RuntimeException("Invalid method call. transitionIfNeeded should not be called");
        }

        @Override
        public String transition() {

            /* Then we hit the end of the script. So there is nothing more to do */
            if (originalScriptFragment == null || originalScriptFragment.equals("")) {
                return "";
            }

            /*
             * Otherwise we are a the end and we want to copy just the
             * whitespace and comment lines. Note we could be skipping the last
             * stream(s)
             */
            final int strLen = originalScriptFragment.length();
            int currentIndex = 0;
            boolean seenNonCommentLine = false;

            while (currentIndex < strLen) {
                char c = originalScriptFragment.charAt(currentIndex);
                lastColumnAt++;
                if (c == '\n') {
                    // Only support unix new lines for now
                    atLine++;
                    lastColumnAt = -1;

                    if (!seenNonCommentLine && !isInComment()) {
                        seenNonCommentLine = true;
                        /*
                         * Add any deviation if needed. The purpose here is to
                         * replace the first command/event line with the
                         * deviation
                         */
                        if (deviatedStreamStartLoc != null) {
                            Throwable failure = failedLocations.get(deviatedStreamStartLoc);
                            if (failure != null) {
                                // TODO: Eventually instead of LogLastEventHandler ... the thing we want to display will be
                                // in the failure we just retrieved
                                String lastEvent = LogLastEventHandler.getLastEvent(deviatedStreamStartLoc);
                                if (lastEvent != null) {
                                    observedStream.append(lastEvent + "\n");
                                }
                            }
                            deviatedStreamStartLoc = null;
                        }
                    }

                    setInComment(false);
                } else if (c == COMMENT) {
                    setInComment(true);
                } else {
                    /*
                     * Actually this could be false because we may be skipping
                     * the last stream. In this case we transitioned to finish
                     * rather than SkipStreamState
                     */
                    // assert isInComment() || Character.isWhitespace(c) :
                    // String.format(
                    // "Found script text at end with no matching Location info at %d:%d",
                    // atLine, lastColumnAt );
                }
                currentIndex++;
                appendIfWanted(c);
            }
            return observedStream.toString();
        }
    }
}
