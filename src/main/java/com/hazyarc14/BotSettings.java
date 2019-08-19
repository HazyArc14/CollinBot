package com.hazyarc14;

import java.io.Serializable;

public class BotSettings implements Serializable {

    private boolean skipMode = true;

    public boolean getSkipMode() {
        return skipMode;
    }

    public void setSkipMode(boolean skipMode) {
        this.skipMode = skipMode;
    }

}
