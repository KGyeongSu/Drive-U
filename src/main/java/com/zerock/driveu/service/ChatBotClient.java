package com.zerock.driveu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatBotClient {

    // API 호출 담당__HTTP 클라이언트
    private final RestTemplate restTemplate;
    // json 문자열 전환
    private final ObjectMapper objMapper;

    // 네이버 챗봇 호출 설정값들
    @Value("${naver.chatbot.invoke-url}")
    private String invokeUrl;

    @Value("${naver.chatbot.secret-key}")
    private String secretKey;

    public Map<String, Object> call(String question) {

        try {

            // 바디 구성 : 챗봇이 요구하는 JSON 형식
            Map<String, Object> request = new HashMap<>();

            // json 구성요소 넣기
            request.put("version", "v2");
            request.put("userId", UUID.randomUUID().toString());
            request.put("timestamp", System.currentTimeMillis());
            request.put("event", "send");

            // bubbles 생성
            Map <String, Object> bubble = new HashMap<>();
            bubble.put("type", "text");

            // data 생성
            Map <String, Object> data = new HashMap<>();
            data.put("description", question);

            // bubble에 data넣기
            bubble.put("data", data);

            // bubbles 배열 생성
            List<Map <String, Object>> bubbles = new ArrayList<>();
            bubbles.add(bubble);

            // request에 bubbles추가
            request.put("bubbles", bubbles);

            // Map list > json 문자열로 변환
            String requestBody = objMapper.writeValueAsString(request);

            // signature
            String signature = makeSignature(requestBody);

            // header 설정 : api가 이렇게 요청하라고 정함
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            headers.set("X-NCP-CHATBOT_SIGNATURE", signature);

            HttpEntity <String> entity = new HttpEntity<>(requestBody, headers);

            log.info("invokeUrl : {}", invokeUrl);
            log.info("requestBody : {}", requestBody);
            log.info("signature : {}", signature);

            // 외부 API 호출__요청주소, 요청내용, 응답받을형태 (key:value)
            ResponseEntity<Map> response = restTemplate.postForEntity(invokeUrl, entity, Map.class);

            return response.getBody();

        } catch (Exception e) {

            log.error("CLOVA API 호출 실패", e);

            return null;

        }

    }

    private String makeSignature (String message) {

        try {

            // secretKey 기반 HMAC SHA256
            SecretKeySpec signingKey = new SecretKeySpec(

                    secretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"

            );

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // message 암호화
            byte [] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            // Base64 문자열 반환
            return Base64.getEncoder().encodeToString(rawHmac);

        } catch (Exception e) {

            throw new RuntimeException(

                    "signature 생성 실패",
                    e

            );

        }

    }

}
