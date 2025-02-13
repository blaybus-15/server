package com.blaybus.server;

import com.blaybus.server.config.SMS.SMSProperties;
import com.blaybus.server.service.SMSService;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SMSServiceTest {

    @Autowired
    private SMSService smsService;

    @Autowired
    private SMSProperties smsProperties;

    @Autowired
    private DefaultMessageService messageClient;

    @Test
    void sendSMS() {
        // Given
        String phoneNumber = "01052215391";
        String messageBody = "정우가보낸테스트";

        // When
        smsService.sendSMS(phoneNumber, messageBody);

        // Then
        System.out.println("✅ 메시지가 정상적으로 전송되었습니다. 수신 번호: " + phoneNumber);
    }
}
