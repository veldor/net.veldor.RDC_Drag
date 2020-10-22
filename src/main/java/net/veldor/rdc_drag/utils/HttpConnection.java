package net.veldor.rdc_drag.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnection {

    private static final String USER_AGENT = "Mozilla/5.0";
    public static final String URL = "https://rdcnn.ru/api";

    // HTTP POST request
    public static String sendPost(String message) throws Exception {
        URL obj = new URL(URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(message);
        wr.flush();
        wr.close();

        //int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString();
    }

    public static boolean checkToken(String token) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("cmd", "check_access_token");
        requestBody.put("token", token);
        String message = requestBody.toString();
        try {
            String answer = sendPost(message);
            System.out.println(answer);
            if (!answer.isEmpty()) {
                JSONObject answerData = new JSONObject(answer);
                String status = answerData.getString("status");
                if (status != null) {
                    if (status.equals("success")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String login(String login, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("cmd", "login");
        requestBody.put("login", login);
        requestBody.put("pass", password);
        System.out.println("login: " + login + " pass: " + password);
        try {
            String answer = sendPost(requestBody.toString());
            System.out.println(answer);
            if (!answer.isEmpty()) {
                JSONObject answerData = new JSONObject(answer);
                String status = answerData.getString("status");
                if (status != null) {
                    if (status.equals("failed")) {
                        return answerData.getString("message");
                    } else if (status.equals("success")) {
                        String token = answerData.getString("token");
                        if (token != null) {
                            Properties.saveToken(token);
                            return "Успешный вход";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendFile(File f) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            HttpPost httppost = new HttpPost(URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            /* example for setting a HttpMultipartMode */
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            /* example for adding an image part */
            FileBody fileBody = new FileBody(f); //image should be a String
            JSONObject requestBody = new JSONObject();
            requestBody.put("cmd", "upload_file");
            requestBody.put("token", Properties.getToken());
            StringBody stringBody = new StringBody(requestBody.toString(), ContentType.APPLICATION_JSON);
            builder.addPart("my_file", fileBody);
            builder.addPart("json", stringBody);
            httppost.setEntity(builder.build());
            HttpResponse response = null;
            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(resEntity.getContent(), StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
                out.append(buffer, 0, charsRead);
            }
            if (!out.toString().isEmpty()) {
                JSONObject answerData = new JSONObject(out.toString());
                String status = answerData.getString("status");
                if (status != null) {
                    if (status.equals("success")) {
                        String path = answerData.getString("path");
                        return path.substring(path.lastIndexOf("\\"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
