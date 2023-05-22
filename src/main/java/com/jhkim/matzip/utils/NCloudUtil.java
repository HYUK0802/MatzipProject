package com.jhkim.matzip.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class NCloudUtil {
    //
    private static final String URL_PREFIX = "https://sens.apigw.ntruss.com";
    private static final String ACCESS_KEY = "vszupSMLwNVNPbb0Dd0Q";
    private static final String SECRET_KEY = "YKbsCMocf8SD09PzRNK7pcR3aVBPU0kd6FUKtqHI";
    private static final String SERVICE_ID ="ncp:sms:kr:307474089176:portfolio";

    private static String generateSignature(String requestMethod, String requestUrl, long timestamp) {
        String signature = String.format("%s %s\n%d\n%s",
                requestMethod,
                requestUrl.replace(NCloudUtil.URL_PREFIX,""),
                timestamp,
                NCloudUtil.ACCESS_KEY);
        SecretKeySpec spec = new SecretKeySpec(NCloudUtil.SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(spec);
            byte[] hmacBytes = mac.doFinal(signature.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);

        }
    }
    public static void sendSms(String to, String content) {
        final String requestMethod = "POST";
        final String requestUrl = String.format("%s/sms/v2/services/%s/messages",NCloudUtil.URL_PREFIX, NCloudUtil.SERVICE_ID);
        final JSONObject requestBody = new JSONObject() {{
           put("type","SMS");
           put("contentType", "COMM");
           put("countryCode", "82");
           put("from", "01035243757");
           put("content", content);
           put("messages", new JSONArray(){{
               put(new JSONObject(){{
                   put("to", to);
               }});
           }});
        }};
        try {
            long timestamp = new Date().getTime();
            String signature = NCloudUtil.generateSignature(requestMethod,requestUrl,timestamp);
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
            connection.setRequestProperty("x-ncp-apigw-timestamp", String.valueOf(timestamp));
            connection.setRequestProperty("x-ncp-iam-access-key", NCloudUtil.ACCESS_KEY);
            connection.setRequestProperty("x-ncp-apigw-signature-v2", signature);
            connection.setDoOutput(true);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))){
                writer.write(requestBody.toString());
                writer.flush();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String response = reader.readLine();
                System.out.println(response);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
    private NCloudUtil() {
        super();
    }
}
