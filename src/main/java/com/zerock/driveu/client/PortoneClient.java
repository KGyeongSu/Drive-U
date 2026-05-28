package com.zerock.driveu.client;

import com.zerock.driveu.config.PortoneProperties;
import com.zerock.driveu.dto.portone.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortoneClient {

    private static final String BASE_URL = "https://api.portone.io";

    private final RestTemplate restTemplate;
    private final PortoneProperties portoneProperties;

    public PaymentResponse getPayment(String paymentId) {

        String url = BASE_URL + "/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + portoneProperties.getApiSecret());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    PaymentResponse.class
            );
            return response.getBody();

        } catch (RestClientException e) {
            log.error("포트원 결제 조회 실패: paymentId={}", paymentId, e);
            throw new IllegalStateException("포트원 결제 조회 실패", e);
        }
    }
}