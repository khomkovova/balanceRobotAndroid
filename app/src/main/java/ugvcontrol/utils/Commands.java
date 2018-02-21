package ugvcontrol.utils;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    private static class Wrapper {
        static Commands INSTANCE = new Commands();
    }

    private List<Command> activeCommands;

    private Commands() {
        activeCommands = new ArrayList<>();
    }

    public static Commands getInstance() {
        return Wrapper.INSTANCE;
    }

    public List<Command> getActiveCommands() {
        return activeCommands;
    }

    public void setActiveCommands(List<Command> activeCommands) {
        this.activeCommands = activeCommands;
    }
}
