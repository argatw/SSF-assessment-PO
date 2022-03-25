package com.example.POassessment.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import com.example.POassessment.model.Quotation;
import com.example.POassessment.service.QuotationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@RestController
@RequestMapping(path="/api")
public class PurchaseOrderRestController {
    
    //task 6
    @Autowired
    private QuotationService quotaSvc;

    @PostMapping(path="/po", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postOrder(
        @RequestBody String payload) throws IOException {

        System.out.printf(">>> payload: %s\n", payload);

        // Task 3 and 4
   
        InputStream is = new ByteArrayInputStream(payload.getBytes());
        JsonReader r = Json.createReader(is);
        JsonObject req = r.readObject();
    
        String name = req.getString("name");
        JsonArray jsonArray = req.getJsonArray("lineItems");
        List<String> itemNames = new ArrayList<String>();

        for(int i=0; i<jsonArray.size(); i++) {
            JsonObject item = jsonArray.getJsonObject(i);
            itemNames.add(item.getString("item"));
        }

        Optional<Quotation> quotationOption = quotaSvc.getQuotations(itemNames);
        Quotation quota = quotationOption.get();

        Float total = 0.0f;
        for(int i=0; i<jsonArray.size(); i++) {
            JsonObject item = jsonArray.getJsonObject(i);
            int quantity = item.getInt("quantity");
            Float unitP = quota.getQuotation(item.getString("item"));
            total = total + (unitP * quantity);
        }

        JsonObject Obj = Json.createObjectBuilder()
            .add("name", name)
            .add("total", total)
            .add("invoiceId", quota.getQuoteId())
            .build();

        // try {
        //     return ResponseEntity.ok(Obj.toString()); 
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }

        return ResponseEntity.ok(Obj.toString());
        
    }
}
