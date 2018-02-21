package ugvcontrol.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ugvcontrol.utils.Command;
import ugvcontrol.utils.Commands;
import ugvcontrol.utils.UiLog;


public class CommandWebSocketClient extends WebSocketClient {
    public CommandWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        UiLog.log("onOpen");
    }

    @Override
    public void onMessage(String message) {
        UiLog.log("onMessage");
        UiLog.log("message: " + message);

        List<Command> commands = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(message);
            for (int i = 0; i < jsonArray.length(); i++) {
                commands.add(new Command(
                        jsonArray.getJSONObject(i).getInt("type"),
                        jsonArray.getJSONObject(i).getInt("value")));
            }
            Commands.getInstance().setActiveCommands(commands);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        UiLog.log("onMessage");
        UiLog.log("code: " + code);
        UiLog.log("reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        UiLog.log("onError");
        UiLog.log("message: " + ex.getMessage());
    }
}
