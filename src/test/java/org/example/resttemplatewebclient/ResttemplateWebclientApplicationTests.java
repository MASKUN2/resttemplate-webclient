package org.example.resttemplatewebclient;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@SpringBootTest
class ResttemplateWebclientApplicationTests {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void contextLoads() throws URISyntaxException {
            restTemplate.postForEntity(new URI("https://naver.com"), Map.of("a","a","b","b"),String.class);

    }

}
