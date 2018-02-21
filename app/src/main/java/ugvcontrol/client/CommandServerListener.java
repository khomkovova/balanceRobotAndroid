package ugvcontrol.client;

import java.util.List;

import ugvcontrol.utils.Command;

/**
 * Created by Vasyl on 21.02.2018.
 */
public class CommandServerListener implements Runnable {
    private static final int DEFAULT_SLEEP_TIME = 1000;
    private boolean stop = false;
    private int sleepTime;
    protected String serverEndpoint;

    CommandClient commandClient;
    CommandHandler commandHandler;

    public CommandServerListener(CommandHandler commandHandler, String serverEndpoint) {
        this.commandHandler = commandHandler;
        this.serverEndpoint = serverEndpoint;
        this.sleepTime = DEFAULT_SLEEP_TIME;
        commandClient = new CommandClient(this.serverEndpoint);
    }

    public CommandServerListener(CommandHandler commandHandler, String serverEndpoint, int sleepTime) {
        this.commandHandler = commandHandler;
        this.serverEndpoint = serverEndpoint;
        this.sleepTime = sleepTime;
        commandClient = new CommandClient(this.serverEndpoint);
    }

    @Override
    public void run() {
        while (!stop) {
            List<Command> commands = commandClient.getCommands();
            commandHandler.handle(commands);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.stop = true;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
