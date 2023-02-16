import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateStopsBus {

    // Access Token for EMT Api
    private static String accessToken;

    // Http Client
    private static final HttpClient client = HttpClient.newHttpClient();

    // Stops
    private static final ConcurrentHashMap<Integer, Stop> stops = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        getAccessTokenEMT();
        updateEMTBusStops();
    }

    private static void getAccessTokenEMT() throws Exception {
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

    private static void updateEMTBusStops() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://openapi.emtmadrid.es/v1/transport/busemtmad/stops/list/"))
                .header("accessToken", accessToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return;
        ObjectNode responseBody = new ObjectMapper().readValue(response.body(), ObjectNode.class);

        int id = 1;
        Set<String> lines = new HashSet<>();
        // Get stops general info
        JsonNode stopsJsonNode = responseBody.get("data");
        for (JsonNode stopNode: stopsJsonNode) {
            if (stopNode instanceof ObjectNode stop) {
                int number = Integer.parseInt(stop.get("node").asText());
                String name = stop.get("name").asText();
                // Lines of stop
                List<Line> stopLines = new ArrayList<>();
                for (JsonNode line : stop.get("lines")) {
                    String[] lineSplit = line.asText().split("/");
                    String lineLabel = lineSplit[0];
                    String direction = lineSplit[1];
                    stopLines.add(new Line(lineLabel, direction));
                    lines.add(lineLabel);
                }
                // Coordinates of stop
                String type = stop.get("geometry").get("type").asText();
                double longitude = stop.get("geometry").get("coordinates").get(0).asDouble();
                double latitude = stop.get("geometry").get("coordinates").get(1).asDouble();
                Geometry geometry = new Geometry(type, new double[] { longitude, latitude });

                // Stop
                stops.put(number, new Stop(id++, name, number, geometry, stopLines));
            }
        }

        // Use threads
        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(lines.size() * 2);
        // Get for each stop address & order in lines
        for (String line : lines) {
            ex.execute(() -> {
                try {
                    updateEMTBusStops(line, 1, latch);
                    updateEMTBusStops(line, 2, latch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        ex.shutdown();

        // Await until threads are done
        latch.await();

        // Set Stops
        responseBody = new ObjectMapper().createObjectNode();
        responseBody.put("name", "Stops_Bus");
        ArrayNode arrayNode = new ObjectMapper().valueToTree(stops.values());
        responseBody.putArray("data").addAll(arrayNode);

        File file = new File(Paths.get("").toAbsolutePath() + "\\stops_bus.geojson");
        Writer myWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        myWriter.write(responseBody.toPrettyString());
        myWriter.flush();
        myWriter.close();
    }

    private static void updateEMTBusStops(String line, int direction, CountDownLatch latch) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://openapi.emtmadrid.es/v1/transport/busemtmad/lines/" + line + "/stops/" + direction + "/"))
                .header("accessToken", accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return;
        ObjectNode responseBody = new ObjectMapper().readValue(response.body(), ObjectNode.class);

        // Get stops detail info
        JsonNode stopsJsonNode = responseBody.get("data").get(0).get("stops");
        String lineLabel = responseBody.get("data").get(0).get("label").asText();
        if (lineLabel.equals("OO1")) lineLabel = "001"; // Error from EMT Api instead of line = 001, we get line = OO1
        int index = 1;
        for (JsonNode stopNode : stopsJsonNode) {
            int number = Integer.parseInt(stopNode.get("stop").asText());
            String address = stopNode.get("postalAddress").asText();
            stops.get(number).setAddress(address);
            String directionText = String.valueOf(direction);
            int finalIndex = index;
            String finalLineLabel = lineLabel;
            stops.get(number).getLines().stream().forEach(stopLine -> {
                if (stopLine.getLine().equals(line)
                        && stopLine.getDirection().equals(directionText)) {
                    stopLine.setLine(finalLineLabel);
                    stopLine.setOrder(finalIndex);
                }
            });
            index++;
        }
        latch.countDown();
    }

    /**
     * Classes
     */

    private static class Stop {
        int id;
        String name;
        int number;
        String address;
        List<Line> lines;
        Geometry geometry;

        Stop(int id, String name, int number, Geometry geometry, List<Line> lines) {
            this.setId(id);
            this.setName(name);
            this.setNumber(number);
            this.setGeometry(geometry);
            this.setLines(lines);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<Line> getLines() {
            return lines;
        }

        public void setLines(List<Line> lines) {
            this.lines = lines;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }

    private static class Geometry {
        String type;
        double[] coordinates;

        Geometry(String type, double[] coordinates) {
            this.setType(type);
            this.setCoordinates(coordinates);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }
    }

    private static class Line {
        String line;
        String direction;
        int order;
        Double distance_previous_segment;
        Double speed_previous_segment;
        List<Object> stop_times;
        Object geometry;

        Line(String line, String direction) {
            this.setLine(line);
            this.setDirection(direction);
            this.setDistance_previous_segment(null);
            this.setSpeed_previous_segment(null);
            this.setStop_times(null);
            this.setGeometry(null);
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public Double getDistance_previous_segment() {
            return distance_previous_segment;
        }

        public void setDistance_previous_segment(Double distance_previous_segment) {
            this.distance_previous_segment = distance_previous_segment;
        }

        public Double getSpeed_previous_segment() {
            return speed_previous_segment;
        }

        public void setSpeed_previous_segment(Double speed_previous_segment) {
            this.speed_previous_segment = speed_previous_segment;
        }

        public List<Object> getStop_times() {
            return stop_times;
        }

        public void setStop_times(List<Object> stop_times) {
            this.stop_times = stop_times;
        }

        public Object getGeometry() {
            return geometry;
        }

        public void setGeometry(Object geometry) {
            this.geometry = geometry;
        }
    }
}
