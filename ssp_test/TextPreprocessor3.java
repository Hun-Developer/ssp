package com.lgcns.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONObject;

public class TextPreprocessor {
    private Map<String, String> dictionary;
    private Map<String, String> stopwords;
    private Map<String, String> modelUrls;
    private Map<String, Map<String, String>> modelClasses;

    public TextPreprocessor() {
        dictionary = new HashMap<>();
        stopwords = new HashMap<>();
        modelUrls = new HashMap<>();
        modelClasses = new HashMap<>();
        loadDictionary();
        loadStopwords();
        loadModelInfo();
    }

    private void loadDictionary() {
        try (BufferedReader br = new BufferedReader(new FileReader("DICTIONARY.TXT"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length == 2) {
                    dictionary.put(parts[0].toLowerCase(), parts[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStopwords() {
        try (BufferedReader br = new BufferedReader(new FileReader("STOPWORD.TXT"))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopwords.put(line, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadModelInfo() {
        try (BufferedReader br = new BufferedReader(new FileReader("MODELS.JSON"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONArray models = jsonObject.getJSONArray("models");

            for (int i = 0; i < models.length(); i++) {
                JSONObject model = models.getJSONObject(i);
                String modelName = model.getString("modelname");
                String url = model.getString("url");
                modelUrls.put(modelName, url);

                Map<String, String> classes = new HashMap<>();
                JSONArray classArray = model.getJSONArray("classes");
                for (int j = 0; j < classArray.length(); j++) {
                    JSONObject classObj = classArray.getJSONObject(j);
                    classes.put(classObj.getString("code"), classObj.getString("value"));
                }
                modelClasses.put(modelName, classes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String processText(String text, String modelName) {
        StringBuilder result = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(text);
        
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().toLowerCase();
            String embedding = dictionary.get(token);
            
            if (embedding != null) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(embedding);
            }
        }

        // 선택된 모델의 URL로 HTTP 요청 전송
        String modelUrl = modelUrls.get(modelName);
        if (modelUrl == null) {
            return "Error: Model not found";
        }

        try {
            URL url = new URL(modelUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("text", result.toString());
            requestBody.put("model", modelName);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                // 응답 코드를 실제 클래스 값으로 변환
                JSONObject responseJson = new JSONObject(response.toString());
                String code = responseJson.getString("code");
                Map<String, String> classes = modelClasses.get(modelName);
                String value = classes.get(code);
                
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
} 