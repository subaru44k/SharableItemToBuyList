package com.appsubaruod.sharabletobuylist.state;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by s-yamada on 2017/11/13.
 * Represents the state of ActionMode.
 */
public class ActionModeState {

    private boolean isActionMode = false;
    private Set<ActionModeChangedListener> mActionModeChangedListenerSet = new HashSet<>();

    public boolean isActionMode() {
        return isActionMode;
    }

    public void setActionMode(boolean actionMode) {
        if (isActionMode == actionMode) {
            return;
        }
        isActionMode = actionMode;
        mActionModeChangedListenerSet.forEach(listener -> listener.onActionModeChanged(isActionMode));
    }

    public void registerActionModeChangedListener(ActionModeChangedListener listener) {
        mActionModeChangedListenerSet.add(listener);
    }

    public void clearListener() {
        mActionModeChangedListenerSet.clear();
    }

    public interface ActionModeChangedListener {
        void onActionModeChanged(boolean isActionMode);
    }

}
