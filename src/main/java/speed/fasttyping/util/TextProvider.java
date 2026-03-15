package speed.fasttyping.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TextProvider {
    public static String fetchRandomQuote() {
        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://zenquotes.io/api/random"))
                    .GET()
                    .build();

            HttpResponse<String> responce = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = responce.body();
            String content = body.split("\"q\":\"")[1].split("\"")[0];
            return content;
        } catch (Exception e) {
            System.err.println("Не вдалося отримати текст: " + e.getMessage());
            return "Не вдалося отримати текст";
        }
    }

}
