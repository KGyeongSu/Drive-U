package com.zerock.driveu.dto.portone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String status;              // 'PAID', 'FAILED', 'CANCELLED' 등등
    private String id;                  // paymentId (= 우리 merchantUid)
    private Amount amount;
    private Channel channel;
    private Method method;
    private OffsetDateTime paidAt;      // 결제완료시간


    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount{
        private int total;              // 핵심 검증
    }
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Channel{
        private String pgProvider;      // '카카오', '토스페이' 등등
    }
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Method{
        private String type;            // PaymentMethodCard, PaymentMethodEasyPay 메서드
    }
}
