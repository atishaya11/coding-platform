package com.dscjss.codingplatform.users;


import com.dscjss.codingplatform.users.dto.UserBean;
import com.dscjss.codingplatform.users.dto.UserDto;
import com.dscjss.codingplatform.users.exception.UsernameExistsException;
import com.dscjss.codingplatform.users.model.User;
import com.dscjss.codingplatform.users.model.VerificationToken;
import com.dscjss.codingplatform.validation.EmailExistsException;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    User findUserByEmail(String email);

    User registerNewUserAccount(UserDto userDto) throws EmailExistsException, UsernameExistsException;

    User getUser(String verificationToken);

    VerificationToken getVerificationToken(String verificationToken);

    VerificationToken createVerificationToken(User user);

    VerificationToken generateNewVerificationToken(String existingToken);

    void saveRegisteredUser(User user);

    void createAdmin();

    UserBean getUserByUsername(String username);
}
