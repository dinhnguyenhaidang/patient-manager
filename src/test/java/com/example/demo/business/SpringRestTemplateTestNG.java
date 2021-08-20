package com.example.demo.business;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SpringRestTemplateTestNG {

    public String responseBody;
    private RestTemplate restTemplate;
    private Long patientId;
    private ResponseEntity<String> response;

    @BeforeTest
    public void beforeTest() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void create_success() {
        String postURI = "http://localhost:12345/patient/";
        System.out.println("Post URI: " + postURI);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        String jsonBody = "{\"name\":\"Ayaya\",\"age\":1,\"address\":\"Earth\"}";
        System.out.println("JSON body: " + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        response = restTemplate.postForEntity(postURI, entity, String.class);
        responseBody = response.getBody();
        System.out.println("Post response body: " + responseBody);

        patientId = getIdFromResponse(responseBody);
        System.out.println("patientId: " + patientId);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(responseBody.contains(patientId.toString()));

        System.out.println("Created successfully patientId: " + patientId + "\n");
    }

    public static Long getIdFromResponse(String json) {
        JSONParser parser = new JSONParser();

        Object obj = new Object();
        try {
            obj = parser.parse(json);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        JSONObject jsonResponseObject = (JSONObject) obj;
        return Long.valueOf(jsonResponseObject.get("patientId").toString());
    }

    @Test(dependsOnMethods = "create_success")
    public void read_success() {
        String getURI = "http://localhost:12345/patient/" + this.patientId;
        System.out.println("Get URL :" + getURI);

        response = restTemplate.getForEntity(getURI, String.class);
        responseBody = response.getBody();
        System.out.println("Get response body: " + responseBody + "\n");

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(responseBody.contains("Ayaya"));
    }

    @Test(dependsOnMethods = "create_success")
    public void update_success() {
        String putURI = "http://localhost:12345/patient/";
        System.out.println("Put URI: " + putURI);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        String jsonBody = responseBody;
        jsonBody = jsonBody.replace("Ayaya", "Updated Ayaya");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        response = restTemplate.exchange(putURI, HttpMethod.PUT, entity, String.class);
        responseBody = response.getBody();
        System.out.println("Update response body: " + responseBody + "\n");

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(responseBody.contains("Updated Ayaya"));
    }

    @Test(dependsOnMethods = "read_success")
    public void delete_success() {
        String deleteURI = "http://localhost:12345/patient/" + this.patientId;
        System.out.println("Delete URI: " + deleteURI);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange(deleteURI, HttpMethod.DELETE, entity, String.class);
        responseBody = response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

}
