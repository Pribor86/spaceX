package main;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class MatterAntimatter<K, V> {
    private final String authCode;
    private final URL matter =
            new URL("https://warp-regulator-bd7q33crqa-lz.a.run.app/api/adjust/matter");
    private final URL antiMatter =
            new URL("https://warp-regulator-bd7q33crqa-lz.a.run.app/api/adjust/antimatter");
    public MatterAntimatter(String name, String email) throws IOException {
        String jsonInputString = "{"
                + "\"name\": \"" + name + "\","
                + "\"email\": \"" + email + "\""
                + "}";

        URL apiStart = new URL("https://warp-regulator-bd7q33crqa-lz.a.run.app/api/start");

        this.authCode = (String) activate(jsonInputString, apiStart).get("authorizationCode");
    }

    private Map<K, V> activate(
            String inputJson, URL requestUrl) throws IOException {
        HashMap<K, V> resp;
        HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = inputJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Gson gson = new Gson();
            resp = gson.fromJson(String.valueOf(response), HashMap.class);
        }
        return resp;
    }

    private Map<K, V> getStatus() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://warp-regulator-bd7q33crqa-lz.a.run.app/api/"
                        + "status?authorizationCode=" + authCode)).build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        Gson gson = new Gson();
//        intermix = getStatus()
//                this.authCode = (String) activate(jsonInputString, apiStart).get("authorizationCode");
        return new HashMap<K, V>(
                gson.fromJson(String.valueOf(response.body()), HashMap.class));
    }

    private void jsonPost(String inputJson, URL requestUrl) throws IOException {
        HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");
        byte[] out = inputJson.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();
    }


    private String inject(float fuel) {
        String jsonString = "{"
                + "\"authorizationCode\": \"" + authCode + "\","
                + "\"value\": " + fuel
                + "}";

        //System.out.println(jsonString);
        return jsonString;
    }

    private void adjust(String flowrate, double intermix) throws IOException {
        float fuel = (float) ((0.2/intermix)/Math.PI);
        if (fuel > 0.2){
            fuel = 0.2f;
        }
        System.out.println("Fuel: " + fuel);
        switch (flowrate) {
            case "LOW":
                if (intermix < 0.5) {
                    jsonPost(inject(fuel), matter);
                    jsonPost(inject(fuel - 0.02f), antiMatter);
                } else if (intermix >= 0.5) {
                    jsonPost(inject(fuel - 0.02f), matter);
                    jsonPost(inject(fuel - 0.02f), antiMatter);
                }
                break;
            case "OPTIMAL":
                if (intermix < 0.5) {
                    jsonPost(inject(fuel - 0.02f), matter);
                    jsonPost(inject(fuel - 0.2f), antiMatter);
                }
                else if (intermix == 0.5) {
                    jsonPost(inject(fuel - 0.12f), matter);
                    jsonPost(inject(fuel - 0.12f), antiMatter);
                }
                else if (intermix > 0.5){
                    jsonPost(inject(fuel - 0.08f), antiMatter);
                    jsonPost(inject(fuel - 0.17f), matter);
                }
                break;
            case "HIGH":
                if (intermix < 0.5) {
                    jsonPost(inject((-fuel + 0.07f)), matter);
                    jsonPost(inject((-fuel + 0.04f)), antiMatter);
                } else if (intermix >= 0.5) {
                    jsonPost(inject((-fuel + 0.04f)), matter);
                    jsonPost(inject((-fuel + 0.08f)), antiMatter);

                }
                break;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MatterAntimatter<String, String> ma = new MatterAntimatter<>("Alex", "pribor86@gmail.com");

        for (int i = 0; i < 60; i++) {
            long startTime = System.currentTimeMillis();
            System.out.println(i + ".\n");
            HashMap<String, String> status = (HashMap<String, String>) ma.getStatus();
            String flowrate = status.get("flowRate");
            double intermix = Double.parseDouble(String.valueOf(status.get("intermix")));
            System.out.println("flowRate: " + flowrate);
            System.out.println("intermix: " + intermix);
            ma.adjust(flowrate, intermix);
            long duringTime = System.currentTimeMillis() - startTime;
            System.out.println("Delay: " + duringTime);
            System.out.println("\n");
            Thread.sleep(1000 - duringTime);
        }
    }
}