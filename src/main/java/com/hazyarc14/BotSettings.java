package com.hazyarc14;

import java.io.Serializable;

public class BotSettings implements Serializable {

    private boolean skipMode = true;
    private boolean voiceJoinActions = false;
    private boolean voiceFollowMode = false;
    private Long voideFollowUserId = 0L;

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

    public boolean getVoiceFollowMode() {
        return voiceFollowMode;
    }

    public void setVoiceFollowMode(boolean voiceFollowMode) {
        this.voiceFollowMode = voiceFollowMode;
    }

    public Long getVoiceFollowUserId() {
        return voideFollowUserId;
    }

    public void setVoiceFollowUserId(Long voideFollowUserId) {
        this.voideFollowUserId = voideFollowUserId;
    }

}
