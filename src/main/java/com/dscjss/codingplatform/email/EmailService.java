package com.dscjss.codingplatform.email;

import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.users.model.VerificationToken;

public interface EmailService {

    void sendVerificationEmail(User user, VerificationToken verificationToken, String url);
}
