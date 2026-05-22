package com.zerock.driveu.service;

import com.zerock.driveu.dto.ChatBotAnswerButtonDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ChatBotParser {

        public String extractAnswer (Map<String, Object> body) {

        try {

            Object bubbleObj = body.get("bubbles");

            // bubbleObj가 List 객체이면 bubbles라고 쓰겠다 > 패턴 매칭 ( 타입검사, 캐스팅 )
            if (!(bubbleObj instanceof List<?> bubbles)) {

                log.warn("bubbles 없음");

                return "응답이 없습니다.";

            }

            for (Object bubble : bubbles) {

                if (!(bubble instanceof Map<?, ?> bubbleMap)) {

                    continue;

                }

                String type = String.valueOf(bubbleMap.get("type"));

                // 일반 텍스트 응답
                if ("text".equals(type)) {

                    Object dataObj = bubbleMap.get("data");

                    if (dataObj instanceof Map <?, ?> dataMap) {

                        Object desc = dataMap.get("description");

                        if (desc != null) {

                            return String.valueOf(desc);

                        }

                    }

                }

                // template 응답 대응
                if ("template".equals(type)) {

                    Object dataObj = bubbleMap.get("data");

                    if (dataObj instanceof Map <?, ?> dataMap) {

                        Object coverObj = dataMap.get("cover");

                        if (coverObj instanceof Map <?, ?> coverMap) {

                            Object coverDataObj = coverMap.get("data");

                            if (coverDataObj instanceof Map<?, ?> coverDataMap) {

                                Object desc = coverDataMap.get("description");

                                if (desc != null) {

                                    return String.valueOf(desc);

                                }

                            }

                        }

                    }

                }

            }

        } catch (Exception e) {

            log.error("answer parsing 실패");

        }

        return "응답이 없습니다.";

    }

    public List<ChatBotAnswerButtonDTO> extractButtons (Map <String, Object> body) {

        List <ChatBotAnswerButtonDTO> result = new ArrayList<>();

        try {

            Object bubbleObj = body.get("bubbles");

            if (!(bubbleObj instanceof List <?> bubbles)) {

                return result;

            }

            for (Object bubble : bubbles) {

                if (!(bubble instanceof Map <?, ?> bubbleMap)) {

                    continue;

                }

                String type = String.valueOf(bubbleMap.get("type"));

                // 버튼은 template에서만 처리
                if (!"template".equals(type)) {

                    continue;

                }

                Object dataObj = bubbleMap.get("data");

                if (!(dataObj instanceof Map<?,?> dataMap)) {

                    continue;

                }

                Object tableObj = dataMap.get("contentTable");

                if (!(tableObj instanceof List <?> rows)) {

                    continue;

                }

                // contentTable 반복
                for (Object rowObj : rows) {

                    if(!(rowObj instanceof List<?> cols)) {

                        continue;

                    }

                    for (Object colObj : cols) {

                        if (!(colObj instanceof Map<?,?> colMap)) {

                            continue;

                        }

                        Object buttonDataObj = colMap.get("data");

                        if (!(buttonDataObj instanceof Map<?,?> buttonData)) {

                            continue;

                        }

                        // 버튼 title
                        String title = String.valueOf(buttonData.get("title"));
                        String url = "";

                        // 내부 data
                        Object inDataObj = buttonData.get("data");

                        if (inDataObj instanceof Map<?,?> inData) {

                            Object actionObj = inData.get("action");

                            if (actionObj instanceof Map<?,?> actionMap) {

                                Object actionDataObj = actionMap.get("data");

                                if (actionDataObj instanceof Map<?,?> actionDataMap) {

                                    Object urlObj = actionDataMap.get("url");

                                    if (urlObj != null) {

                                        url = String.valueOf(urlObj);

                                    }

                                }

                            }

                        }

                        result.add(

                                ChatBotAnswerButtonDTO.builder()
                                        .title(title)
                                        .url(url)
                                        .build()

                        );

                    }

                }

            }

        } catch (Exception e) {

            log.error("버튼 파싱 실패", e);

        }

        return result;

    }

}
