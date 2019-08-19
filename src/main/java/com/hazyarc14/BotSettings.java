package com.hazyarc14;

import java.io.Serializable;

public class BotSettings implements Serializable {

    private boolean skipMode = true;
    private boolean voiceJoinActions = false;

    public boolean getSkipMode() {
        return skipMode;
    }

    public void setSkipMode(boolean skipMode) {
        this.skipMode = skipMode;
    }

    public boolean getVoiceJoinActions() {
        return voiceJoinActions;
    }

    public void setVoiceJoinActions(boolean voiceJoinActions) {
        this.voiceJoinActions = voiceJoinActions;
    }

}
