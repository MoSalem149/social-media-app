package com.socialmediaapp.Util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageUploader {

    public static String uploadImage(File imageFile) throws IOException, InterruptedException, JSONException {
        if (Boolean.getBoolean("app.imageUploader.localOnly")) {
            return imageFile.toURI().toString();
        }
        // Convert image to Base64
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        String encodedImage = Base64.getEncoder().encodeToString(fileContent);

        String apiKey = "cce94a162fead579aabf1be1111bd9eb";
        String uploadUrl = "https://api.imgbb.com/1/upload?key=" + apiKey;


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
            return imageFile.toURI().toString();
        }
    }
}
