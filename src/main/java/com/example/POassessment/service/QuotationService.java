package com.example.POassessment.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.POassessment.model.Quotation;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class QuotationService {

    private static final String URL = "https://quotation.chuklee.com/quotation";
    

    public Optional<Quotation> getQuotations(List<String> items) {
        JsonArrayBuilder JaB = Json.createArrayBuilder();

        for (String i : items) {
            JaB.add(i);
        }
        JsonArray arr = JaB.build();
        String url = UriComponentsBuilder.fromUriString(URL).toUriString();

        RequestEntity<String> req = RequestEntity
            .post(url)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(arr.toString(), String.class);

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);

        String quoteStr = resp.getBody();
        InputStream is = new ByteArrayInputStream(quoteStr.getBytes());
        JsonReader r = Json.createReader(is);
        JsonObject json = r.readObject();
        
        Quotation quote = new Quotation();
        quote.setQuoteId(json.getString("quoteId"));
        JsonArray quotaArray = json.getJsonArray("quotations");

        Map<String, Float> quotaMap = new HashMap<String, Float>();
        for(int i=0; i< quotaArray.size(); i++) {
            JsonObject item = quotaArray.getJsonObject(i);
            String itemName = item.getString("item");
            JsonValue unit = item.get("unitPrice");
            Float unitP = Float.parseFloat(unit.toString());
            // quote.addQuotation(itemName, unitP.floatValue());
            quotaMap.put(itemName, unitP);
        }
        quote.setQuotations(quotaMap);

            return Optional.of(quote);
    
    }
}
