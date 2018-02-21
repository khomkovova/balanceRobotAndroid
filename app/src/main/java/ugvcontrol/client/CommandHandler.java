package ugvcontrol.client;

import java.util.List;

import ugvcontrol.utils.Command;

public interface CommandHandler {
     void handle(List<Command> commands);
}
