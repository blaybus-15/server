package com.blaybus.server.config.SMS;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SMSConfig {
    private final SMSProperties smsProperties;

    @Bean
    public DefaultMessageService messageClient() {
        return NurigoApp.INSTANCE.initialize(smsProperties.getApiKey(), smsProperties.getApiSecretKey(), smsProperties.getApiHost());
    }
}
