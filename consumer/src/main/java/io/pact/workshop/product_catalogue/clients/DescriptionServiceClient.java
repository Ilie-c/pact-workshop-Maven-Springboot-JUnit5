package io.pact.workshop.product_catalogue.clients;

import io.pact.workshop.product_catalogue.models.Description;
import io.pact.workshop.product_catalogue.models.Product;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.ByteBuffer;
import java.util.Base64;

@Service
public class DescriptionServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Getter
    @Value("${serviceClients.products.baseUrl}")
    private String baseUrl;

    public DescriptionServiceResponse fetchDescriptions() {
        return callApi("/descriptions", DescriptionServiceResponse.class);
    }

    public Description getDescriptionByProductId(long id) {
        return callApi("/description/" + id, Description.class);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    private <T> T callApi(String path, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(System.currentTimeMillis());
        headers.setBearerAuth(Base64.getEncoder().encodeToString(buffer.array()));
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, requestEntity, responseType).getBody();
    }

}
