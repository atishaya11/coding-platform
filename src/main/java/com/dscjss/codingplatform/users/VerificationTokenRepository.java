package com.dscjss.codingplatform.users;


import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.users.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}