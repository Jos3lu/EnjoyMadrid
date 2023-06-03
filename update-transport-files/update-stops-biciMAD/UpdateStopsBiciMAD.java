import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class UpdateStopsBiciMAD {

    private static String accessToken;

    public static void main(String[] args) throws Exception {
        // Creates a file in the same directory containing the updated BiciMAD stations
        HttpClient client = HttpClient.newHttpClient();
        getAccessTokenEMT(client);
        updateBiciMADStops(client);
    }

    private static void getAccessTokenEMT(HttpClient client) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/"))
                .header("email", "enjoymadridapp1@gmail.com")
                .header("password", "EnjoyMadrid123")
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return;
        ObjectNode objectNode = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        accessToken = objectNode.get("data").get(0).get("accessToken").asText();
    }

    private static void updateBiciMADStops(HttpClient client) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://openapi.emtmadrid.es/v1/transport/bicimad/stations/"))
                .header("accessToken", accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return;
        ObjectNode responseBody = new ObjectMapper().readValue(response.body(), ObjectNode.class);
        responseBody.put("name", "Stops_Bicycle");
        responseBody.remove("code");
        responseBody.remove("description");
        responseBody.remove("datetime");

        List<String> filter = Arrays.asList("code", "description", "datetime", "light", "activate", "no_available",
                "total_bases", "dock_bikes", "free_bases", "reservations_count", "virtualDelete");

        JsonNode stations = responseBody.get("data");
        for (JsonNode stationNode: stations) {
            if (stationNode instanceof ObjectNode) {
                ObjectNode station = (ObjectNode) stationNode;
                station.remove(filter);
                station.putNull("lines");

                String name = station.get("name").asText();
                station.put("name", name.split("-", 2)[1].strip());
            }
        }

        String responseString = responseBody.toPrettyString();

        File file = new File(Paths.get("").toAbsolutePath() + "\\stops_bicycle.geojson");
        Writer myWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        myWriter.write(responseString);
        myWriter.flush();
        myWriter.close();
    }
}
