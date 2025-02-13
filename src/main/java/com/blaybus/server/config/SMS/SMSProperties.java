package com.blaybus.server.config.SMS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sms")
public class SMSProperties {
    private String apiKey;
    private String apiSecretKey;
    private String apiHost;
    private String fromPhoneNumber;
}
