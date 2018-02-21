package ugvcontrol.arduino;

public class ArduinoConnector implements Runnable {
    private static final int DEFAULT_SLEEP_TIME = 100;

    private boolean stop = false;
    private int sleepTime;

    CommandWriter commandWriter;

    public ArduinoConnector(CommandWriter commandWriter) {
        this.commandWriter = commandWriter;
        this.sleepTime = DEFAULT_SLEEP_TIME;
    }

    public ArduinoConnector(CommandWriter commandWriter, int sleepTime) {
        this.commandWriter = commandWriter;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (!isStoped()) {
            commandWriter.writeCommand();
            sleep();
        }
    }

    public void stop() {
        this.stop = true;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    protected boolean isStoped() {
        return stop;
    }

    protected void sleep() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
