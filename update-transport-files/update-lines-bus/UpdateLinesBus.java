import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.text.WordUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateLinesBus {

    // Access Token for EMT Api
    private static String accessToken;

    // Lines Bus
    private static final List<Line> lines = Collections.synchronizedList(new ArrayList<>());

    // Http Client
    private static final HttpClient client = HttpClient.newHttpClient();

    // Mapping day type
    private static final Map<String, String> dayType = Map.ofEntries(
            Map.entry("FE", "SUNDAY"),
            Map.entry("LA", "MONDAY-FRIDAY"),
            Map.entry("SA", "SATURDAY"),
            Map.entry("VI", "FRIDAY")
    );

    // Mapping name
    private static final List<String> upperCase = List.of("II", "XII", "XIII");
    private static final List<String> lowerCase = List.of("de", "del", "y");

    public static void main(String[] args) throws Exception {
        getAccessTokenEMT();
        updateEMTBusLines();
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

    private static void updateEMTBusLines() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://openapi.emtmadrid.es/v2/transport/busemtmad/lines/info/"))
                .header("accessToken", accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return;
        ObjectNode responseBody = new ObjectMapper().readValue(response.body(), ObjectNode.class);

        // List with lines ordered
        List<String> labels = new ArrayList<>();

        // Use threads
        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        JsonNode linesJsonNode = responseBody.get("data");
        CountDownLatch latch = new CountDownLatch(linesJsonNode.size());
        for (JsonNode lineNode: linesJsonNode) {
            labels.add(lineNode.get("label").asText());
            ex.execute(() -> {
                try {
                    updateEMTBusLine(lineNode, latch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        ex.shutdown();

        // Await until threads are done
        latch.await();

        // Set lines
        responseBody = new ObjectMapper().createObjectNode();
        responseBody.put("name", "Lines_Bus");
        lines.sort(Comparator.comparing(l -> labels.indexOf(l.getLine())));
        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).id = i + 1;
        }
        ArrayNode arrayNode = new ObjectMapper().valueToTree(lines);
        responseBody.putArray("data").addAll(arrayNode);

        File file = new File(Paths.get("").toAbsolutePath() + "\\lines_bus.json");
        Writer myWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        myWriter.write(responseBody.toPrettyString());
        myWriter.flush();
        myWriter.close();
    }

    private static void updateEMTBusLine(JsonNode lineNode, CountDownLatch latch) throws Exception {
        if (lineNode instanceof ObjectNode line) {
            String label = line.get("label").asText();
            String nameA = WordUtils.capitalizeFully(line.get("nameA").asText());
            nameA = analyzeString(nameA);
            String nameB = WordUtils.capitalizeFully(line.get("nameB").asText());
            nameB = analyzeString(nameB);
            String color = "#" + line.get("color").asText();

            Line line1 = new Line(nameA, label, "1", color);
            Line line2 = new Line(nameB, label, "2", color);

            String lineId = line.get("line").asText();
            // Get start/end time & frequencies
            String date = LocalDate.now().toString().replaceAll("-", "");
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://openapi.emtmadrid.es/v1/transport/busemtmad/lines/" + lineId + "/info/" + date + "/"))
                    .header("accessToken", accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return;
            ObjectNode responseLine = new ObjectMapper().readValue(response.body(), ObjectNode.class);

            // Go to the next line
            if (responseLine.get("data").get(0) == null) {
                lines.add(line1);
                lines.add(line2);
                latch.countDown();
                return;
            }

            Set<String> dayTypeLine = new HashSet<>();
            JsonNode timeTableJsonNode = responseLine.get("data").get(0).get("timeTable");
            for (JsonNode timeTableDayNode : timeTableJsonNode) {
                if (timeTableDayNode instanceof ObjectNode timeTableDay) {
                    String idDayType = timeTableDay.get("idDayType").asText();

                    // Direction 1 & 2
                    List<Line> linesDir = Arrays.asList(line1, line2);
                    int j = 1;
                    for (Line lineDir : linesDir) {
                        String startTime = timeTableDay.get("Direction" + j).get("StartTime").asText();
                        String endTime = timeTableDay.get("Direction" + j).get("StopTime").asText();
                        int maxFreq = timeTableDay.get("Direction" + j).get("MaximumFrequency").asInt() * 60;

                        // Add day -> frequencies
                        Frequency freq = new Frequency(startTime, endTime, maxFreq);
                        String day = dayType.get(idDayType);
                        if (lineDir.getLine().equals("N11")) {
                            System.out.println();
                        }
                        dayTypeLine.add(idDayType);
                        if (idDayType.equals("LA") && dayTypeLine.contains("VI")) day = "MONDAY-THURSDAY";
                        if (idDayType.equals("VI") && dayTypeLine.contains("LA")) {
                            lineDir.getWeek_frequencies().forEach(weekFrequency -> {
                                if (weekFrequency.getWeek_day().equals("MONDAY_FRIDAY"))
                                    weekFrequency.setWeek_day("MONDAY-THURSDAY");
                            });
                        }

                        WeekFrequency weekFrequency = new WeekFrequency(day, List.of(freq));
                        lineDir.getWeek_frequencies().addAll(List.of(weekFrequency));

                        // Add start/end time for each day of the week
                        String timeDay = startTime + " - " + endTime;
                        switch (idDayType) {
                            case "LA" -> {
                                int endDay = 5; // Monday - friday
                                if (!lineDir.getWeek_schedule()[4].equals("")) endDay = 4; // Monday - thursday
                                Arrays.fill(lineDir.getWeek_schedule(), 0, endDay, timeDay);
                            }
                            case "VI" -> lineDir.getWeek_schedule()[4] = timeDay;
                            case "SA" -> lineDir.getWeek_schedule()[5] = timeDay;
                            case "FE" -> lineDir.getWeek_schedule()[6] = timeDay;
                        }
                        j++;
                    }
                }
            }
            lines.add(line1);
            lines.add(line2);
        }
        latch.countDown();
    }

    private static String analyzeString(String name) {
        String[] words = Arrays.stream(name.split("\\s+")).map(UpdateLinesBus::apply).toArray(String[]::new);
        return String.join(" ", words);
    }

    private static String apply(String word) {
        if (upperCase.contains(word.toUpperCase())) return word.toUpperCase();
        else if (lowerCase.contains(word.toLowerCase(Locale.ROOT))) return word.toLowerCase(Locale.ROOT);
        else if (word.contains("/")) {
            String[] splitBySlash = word.split("/");
            String wordSlash = splitBySlash[0];
            for (int i = 1; i < splitBySlash.length; i++) {
                wordSlash += "/" + WordUtils.capitalize(splitBySlash[i]);
            }
            return wordSlash;
        }
        return word;
    }

    /**
     * Classes
     */

    private static class Line {
        int id;
        String line_headsign;
        String line;
        String direction;
        String line_color;
        String[] week_schedule;
        List<WeekFrequency> week_frequencies;

        Line(String line_headsign, String line, String direction, String line_color) {
            this.setLine_headsign(line_headsign);
            this.setLine(line);
            this.setDirection(direction);
            this.setLine_color(line_color);
            this.setWeek_schedule(new String[] { "", "", "", "", "", "", "" });
            this.setWeek_frequencies(new ArrayList<>());
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLine_headsign() {
            return line_headsign;
        }

        public void setLine_headsign(String line_headsign) {
            this.line_headsign = line_headsign;
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

        public String getLine_color() {
            return line_color;
        }

        public void setLine_color(String line_color) {
            this.line_color = line_color;
        }

        public String[] getWeek_schedule() {
            return week_schedule;
        }

        public void setWeek_schedule(String[] week_schedule) {
            this.week_schedule = week_schedule;
        }

        public List<WeekFrequency> getWeek_frequencies() {
            return week_frequencies;
        }

        public void setWeek_frequencies(List<WeekFrequency> week_frequencies) {
            this.week_frequencies = week_frequencies;
        }
    }

    private static class WeekFrequency {
        String week_day;
        List<Frequency> frequencies;

        WeekFrequency(String week_day, List<Frequency> frequencies) {
            this.setWeek_day(week_day);
            this.setFrequencies(frequencies);
        }

        public String getWeek_day() {
            return week_day;
        }

        public void setWeek_day(String week_day) {
            this.week_day = week_day;
        }

        public List<Frequency> getFrequencies() {
            return frequencies;
        }

        public void setFrequencies(List<Frequency> frequencies) {
            this.frequencies = frequencies;
        }
    }

    private static class Frequency {
        String start_time;
        String end_time;
        int frequency;

        Frequency(String start_time, String end_time, int frequency) {
            this.setStart_time(LocalTime.parse(start_time).toString());
            this.setEnd_time(LocalTime.parse(end_time).toString());
            this.setFrequency(frequency);
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }
}
