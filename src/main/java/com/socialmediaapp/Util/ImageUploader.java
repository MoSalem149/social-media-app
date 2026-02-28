package com.socialmediaapp.Util;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;

public class ImageUploader {
    private static final String API_KEY = "cce94a162fead579aabf1be1111bd9eb"; // Ideally from config

    public static String uploadImage(File imageFile) throws IOException, InterruptedException, JSONException {
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        String encodedImage = Base64.getEncoder().encodeToString(fileContent);
        String uploadUrl = "https://api.imgbb.com/1/upload?key=" + API_KEY;
        String formData = "image=" + encodedImage;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());
            return json.getJSONObject("data").getString("url");
        } else {
            throw new RuntimeException("Image upload failed: " + response.body());
        }
    }
}