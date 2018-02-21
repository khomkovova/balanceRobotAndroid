package ugvcontrol.client;

import java.util.List;

import ugvcontrol.utils.Command;
import ugvcontrol.utils.Commands;

public class DefaultCommandHandler implements CommandHandler {

    @Override
    public void handle(List<Command> commands) {
        Commands.getInstance().setActiveCommands(commands);
    }
}

