package com.skillbox.auth.service;

import com.skillbox.auth.dto.MailBody;

public interface EmailService {

    void sendSimpleMessage(MailBody mailBody);

}
