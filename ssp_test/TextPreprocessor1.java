package com.lgcns.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class TextPreprocessor {
    private Map<String, String> dictionary;
    private Map<String, String> stopwords;

    public TextPreprocessor() {
        dictionary = new HashMap<>();
        stopwords = new HashMap<>();
        loadDictionary();
        loadStopwords();
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

    public String processText(String text) {
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
        
        return result.toString();
    }
} 