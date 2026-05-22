package com.zerock.driveu.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="portone")
@Getter
@Setter
public class PortoneProperties {

    private String storeId;
    private String channelKey;
    private String apiSecret;
}
