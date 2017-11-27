package com.appsubaruod.sharabletobuylist.state;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by s-yamada on 2017/11/24.
 */

public class ApplicationStateMediator {
    private Set<ApplicationStateChangedListener> mListener;
    private ApplicationState mState;

    public ApplicationStateMediator() {
        mListener = new HashSet();
        mState = ApplicationState.NONE;
    }

    public void setOnApplicationStateChangedListener(ApplicationStateChangedListener listener) {
        mListener.add(listener);
    }

    public void setState(ApplicationState state) {
        if (mState == state) {
            return;
        }
        mState = state;
        notifyApplicationStateChanged(mState);
    }

    private void notifyApplicationStateChanged(ApplicationState state) {
        mListener.forEach(listener -> listener.onApplicationStateChanged(state));
    }

    public enum ApplicationState {
        NONE,
        STARTED,
        RESUMED,
        PAUSED,
        STOPPED,
    }

    public interface ApplicationStateChangedListener {
        void onApplicationStateChanged(ApplicationState state);
    }
}
