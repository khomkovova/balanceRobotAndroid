package ugvcontrol.client;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ugvcontrol.utils.Command;

public class CommandClient {
    OkHttpClient httpClient;
    String serverEndpoint;

    public CommandClient(String serverEndpoint) {
        httpClient = new OkHttpClient();
        this.serverEndpoint = serverEndpoint;
    }

    public List<Command> getCommands() {
        Response response;
        List<Command> commands = new ArrayList<>();

        try {
            Request request  = new Request.Builder()
                    .url(serverEndpoint + "/get-commands")
                    .build();

            response = httpClient.newCall(request).execute();
            JSONArray jsonArray = new JSONArray(response.body().string());

            for (int i = 0; i < jsonArray.length(); i++) {
                commands.add(new Command(
                        jsonArray.getJSONObject(i).getInt("type"),
                        jsonArray.getJSONObject(i).getInt("value")));
            }
        } catch (JSONException | IOException e) {
            return commands;
        }
        return commands;
    }

    public String getRawResponse() {
        String response;
        try {
            Request request  = new Request.Builder()
                    .url(serverEndpoint + "/get-commands")
                    .build();

            response = httpClient.newCall(request)
                    .execute().body().string();
        } catch (IOException e) {
            return null;
        }
        return response;
    }
}
