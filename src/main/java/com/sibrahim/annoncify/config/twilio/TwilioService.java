package com.sibrahim.annoncify.config.twilio;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.account.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public boolean sendVerificationMessage(String to, String code) {
        boolean isMessageSend = false;
        try {
            Message message = Message.creator(new PhoneNumber(to),
                            new PhoneNumber(fromPhoneNumber),
                            "Your verification code is " + code)
                    .create();
            log.info(message.getSid() + "message sending success fully");
            isMessageSend = true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return isMessageSend;

    }


}
