package com.bajajfinserv.health.test1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Test1Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Test1Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> request = new HashMap<>();
        request.put("name", "M Lalit Aditya");
        request.put("regNo", "22BCE3235");
        request.put("email", "workharderlalit@gmail.com");

        ResponseEntity<Map> generateResponse = restTemplate.postForEntity(url, request, Map.class);

        String webhookUrl = (String) generateResponse.getBody().get("webhook");
        String accessToken = (String) generateResponse.getBody().get("accessToken");

        // Safety check
        if (webhookUrl == null || accessToken == null) {
            System.out.println("Failed to get webhook or access token.");
            return;
        }

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        String query = "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                "ORDER BY p.AMOUNT DESC " +
                "LIMIT 1;";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", query);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity, String.class);
        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}
