package speed.fasttyping.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TextProvider {
    private static final HttpClient client = HttpClient.newBuilder().build();

    public static String fetchRandomQuote() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://zenquotes.io/api/random"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return (response.body().split("\"q\":\"")[1].split("\"")[0]).trim();

        } catch (Exception e) {
            System.err.println("Не вдалося отримати текст: " + e.getMessage());
            return "Не вдалося отримати текст";
        }
    }

    public static String fetchUkrainianQuote() {
        try {
            String english = fetchRandomQuote();
            return translate(english);
        } catch (Exception e) {
            System.err.println("Не вдалося отримати текст: " + e.getMessage());
            return "Не вдалося отримати текст";
        }
    }

    public static String fetchMultipleQuotes(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(fetchRandomQuote());
            if (i < count - 1)
                sb.append(" ");
        }
        return sb.toString();
    }

    public static String fetchMultipleUkrainianQuotes(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(fetchUkrainianQuote());
            if (i < count - 1)
                sb.append(" ");
        }
        return sb.toString();
    }

    private static String translate(String text) throws Exception {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = "https://api.mymemory.translated.net/get?q="
                + encodedText + "&langpair=en%7Cuk";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        if (responseBody.contains("\"translatedText\"")) {
            return decodeUnicode(responseBody.split("\"translatedText\":\"")[1].split("\"")[0]);
        }

        System.err.println("Переклад не вдався: " + responseBody);
        return text;
    }

    private static String decodeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        while(i < input.length()) {
            if(i + 5 < input.length() && input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                sb.append((char) Integer.parseInt(hex, 16));
                i+=6;
            }
            else {
                sb.append(input.charAt(i));
                i++;
            }
        }

        return sb.toString();
    }
}
