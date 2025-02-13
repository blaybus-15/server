package com.blaybus.server.service;

import com.blaybus.server.config.SMS.SMSProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSService {
    private final DefaultMessageService messageClient;
    private final SMSProperties smsProperties;

    public void sendSMS(String phoneNumber, String messageBody) {
        Message message = new Message();
        message.setFrom(smsProperties.getFromPhoneNumber());
        message.setTo(phoneNumber);
        message.setText("[케어링크] " + messageBody);
        SingleMessageSentResponse response = messageClient.sendOne(new SingleMessageSendingRequest(message));
        log.info(String.valueOf(response));
    }
}
