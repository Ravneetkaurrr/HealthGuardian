package com.user.management.service.impl;

//import static com.user.management.utils.UserUtils.convertProfilePictureToBytes;

import com.user.management.constants.UserConstants;
import com.user.management.constants.UserStatus;
import com.user.management.exceptions.ValidationException;
import com.user.management.model.EmailDetails;
import com.user.management.model.User;
import com.user.management.model.UserOtp;
import com.user.management.repository.EmailDetailsRepository;
import com.user.management.repository.OtpRepository;
import com.user.management.repository.UserRepository;
import com.user.management.request.UserPasswordRequest;
import com.user.management.request.UserProfilePictureRequest;
import com.user.management.request.UserUpdateRequest;
import com.user.management.request.UserRequest;
import com.user.management.request.UserStatusRequest;
import com.user.management.response.UserResponse;
import com.user.management.service.EmailService;
import com.user.management.service.OtpService;
import com.user.management.service.UserService;
import com.user.management.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private OtpService otpService;

  @Autowired
  private OtpRepository otpRepository;

  @Autowired
  private EmailDetailsRepository emailDetailsRepository;

  @Override
  public UserResponse registerUser(UserRequest userRequest) throws IOException {
    UserResponse userResponse = new UserResponse();
    //validateUser(userRequest);
    if (Objects.nonNull(userRepository.findByEmail(userRequest.getEmail()))
        || Objects.nonNull(userRepository.findByPhoneNo(userRequest.getPhoneNo()))) {
      throw new ValidationException("Email or Phone number already in use");
    }
    User user = UserUtils.convertUserRequestToUser(userRequest);
    user.setUserId(UUID.randomUUID().toString());
    user = userRepository.save(user);
    String otp = otpService.generateOtp();
    UserOtp userOtp = UserUtils.createUserOtp(otp, user.getEmail());
    otpService.saveOtp(userOtp);
    EmailDetails emailDetails = emailDetailsRepository.findByEmailType("REGISTRATION");
    try {
      emailService.sendEmail(user.getEmail(), emailDetails.getEmailSubject(),
          emailDetails.getEmailBody().replace("${FIRST_NAME}", user.getFirstName())
              .replace("${OTP}", otp));
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
    userResponse.setMessage("User has been successfully registered");
    userResponse.setUserId(user.getUserId());
    return userResponse;
  }

  @Override
  public UserResponse loginUser(UserRequest userRequest) {

    if (Objects.isNull(userRequest.getEmail()) || Objects.isNull(userRequest.getPassword())) {
      throw new IllegalArgumentException("Please enter valid emailId or password");
    }

    UserResponse userResponse = new UserResponse();
    User user = userRepository.findByEmail(userRequest.getEmail());
    if (Objects.nonNull(user) && userRequest.getPassword().equals(user.getPassword())) {
      EmailDetails emailDetails = emailDetailsRepository.findByEmailType("LOGIN");
      try {
        emailService.sendEmail(user.getEmail(), emailDetails.getEmailSubject(),
            emailDetails.getEmailBody().replace("${FIRST_NAME}", user.getFirstName()));
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }
      userResponse.setMessage("User has been successfully logged in !!!");
      userResponse.setUserId(user.getUserId());
      return userResponse;
    } else {
      throw new RuntimeException("Please enter valid emailId or password");
    }

  }
//  @Override
//  public UserResponse loginByGmail(OAuth2AuthenticationToken token) {
//    UserResponse userResponse=new UserResponse();
//    OAuth2User auth2User=token.getPrincipal();
//    String name=auth2User.getAttribute("name");
//    String emailId=auth2User.getAttribute("emailId");
//    User user =userRepository.findByEmail(emailId);
//    if(Objects.isNull(user)){
//      throw new RuntimeException("Invalid emailId");
//    }
//    EmailDetails emailDetails = emailDetailsRepository.findByEmailType("LOGIN");
//    try {
//      emailService.sendEmail(user.getEmail(), emailDetails.getEmailSubject(),
//          emailDetails.getEmailBody().replace("${FIRST_NAME}", user.getFirstName()));
//    } catch (MessagingException e) {
//      throw new RuntimeException(e);
//    }
//    userResponse.setMessage("User has been successfully logged in !!!");
//    userResponse.setUserId(user.getUserId());
//    return userResponse;
//  }


  @Override
  public UserResponse generateOtpByEmailId(String emailId) {
    UserResponse userResponse = new UserResponse();
    if (Objects.isNull(emailId)) {
      throw new IllegalArgumentException("Please enter valid emailId");
    }
    User user = userRepository.findByEmail(emailId);
    if (Objects.nonNull(user)) {
      String otp = otpService.generateOtp();
      UserOtp userOtp = UserUtils.createUserOtp(otp, user.getEmail());
      otpService.saveOtp(userOtp);
      EmailDetails emailDetails = emailDetailsRepository.findByEmailType("GENERATE_OTP");
      try {
        emailService.sendEmail(emailId, emailDetails.getEmailSubject(),
            emailDetails.getEmailBody().replace("${FIRST_NAME}", user.getFirstName())
                .replace("${OTP}", otp));
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }
      userResponse.setMessage("OTP has been sent to registered email: " + emailId);
      userResponse.setUserId(user.getUserId());
    }
    return userResponse;
  }

  @Override
  public UserResponse resetPassword(@Valid UserPasswordRequest passwordRequest) {
    UserResponse userResponse = new UserResponse();
    if (Objects.isNull(passwordRequest.getOldPassword()) || Objects.isNull(
        passwordRequest.getNewPassword()) || Objects.isNull(
        passwordRequest.getEmailId())) {
      throw new IllegalArgumentException("Invalid emailId or password");
    }

    User user = userRepository.findByEmail(passwordRequest.getEmailId());
    if (Objects.nonNull(user)) {
      if (passwordRequest.getOldPassword().equals(user.getPassword())
          && !passwordRequest.getOldPassword().equals(passwordRequest.getNewPassword())) {
        user.setPassword(passwordRequest.getNewPassword());
        userRepository.save(user);
        userResponse.setMessage("Password updated successfully");
        userResponse.setUserId(user.getUserId());
      } else {
        throw new IllegalArgumentException("Please enter a valid password");
      }
    }
    return userResponse;
  }

  @Override
  public UserResponse forgetPassword(UserPasswordRequest passwordRequest) {
    if (Objects.isNull(passwordRequest)) {
      throw new IllegalArgumentException("invalid password or emailId");
    }
    UserResponse userResponse = new UserResponse();
    User user = userRepository.findByEmail(passwordRequest.getEmailId());
    if (Objects.nonNull(user)) {
      user.setPassword(passwordRequest.getNewPassword());
      userRepository.save(user);
      userResponse.setMessage("Password has been changed");
      userResponse.setUserId(user.getUserId());
      return userResponse;
    } else {
      throw new IllegalArgumentException("Please enter a valid password or emailId");
    }
  }

  @Override
  public UserResponse changeStatus(UserStatusRequest userStatusRequest) {

    UserResponse userResponse = new UserResponse();

    User user = userRepository.findByEmail(userStatusRequest.getEmailId());
    if (user.getStatus().name().equals(userStatusRequest.getStatus())) {
      userResponse.setMessage(
          "Status can't be updated as status is already " + user.getStatus().name());
    } else {
      user.setStatus(UserStatus.valueOf(userStatusRequest.getStatus()));
      userRepository.save(user);
      userResponse.setMessage("Status has been updated successfully");
    }
    userResponse.setUserId(user.getUserId());
    return userResponse;
  }

  @Override
  public UserResponse updateUser(UserUpdateRequest updateUserRequest) {
    UserResponse userResponse = new UserResponse();
    User user = userRepository.findByEmail(updateUserRequest.getEmailId());//can we use find by id?
    if (Objects.isNull(user)) {
      throw new IllegalArgumentException("Invalid emailId");
    }
    Optional.ofNullable(updateUserRequest.getFirstName()).ifPresent(
        user::setFirstName); //The :: (method reference operator) is a shortcut to refer to methods without writing a lambda expression.
    Optional.ofNullable(updateUserRequest.getLastName()).ifPresent(user::setLastName);
    Optional.ofNullable(updateUserRequest.getEmailId()).ifPresent(user::setEmail);
    Optional.ofNullable(updateUserRequest.getPhoneNo()).ifPresent(user::setPhoneNo);
    Optional.ofNullable(UserUpdateRequest.getDob())
        .map(dob -> LocalDate.parse(dob,
            UserConstants.DATE_TIME_FORMATTER_DD_MM_YYYY)) // Convert String to LocalDate //The .map() method in Java transforms (or "maps") the value inside an Optional from one type to another.
        .ifPresent(user::setDob); // Set only if non-null
    Optional.ofNullable((updateUserRequest.getAddress())).ifPresent(user::setAddress);
    //Optional.ofNullable(updateUserRequest.getProfilePicture()).ifPresent(user::setProfilePicture(convertProfilePictureToBytes(updateUserRequest.getProfilePicture()));
    userRepository.save(user);
    userResponse.setUserId(user.getUserId());
    userResponse.setMessage("Details updated successfully");
    return userResponse;
  }

  @Override
  public UserResponse uploadProfilePicture(String emailId, MultipartFile profilePicture) //form data
      throws IOException {
    //issue yeh print hi nhi ho rha even if something is null
    if(ObjectUtils.isEmpty(emailId) || ObjectUtils.isEmpty(profilePicture)){
      throw new IllegalArgumentException("emailId or profile picture cannot be null");
    }
    UserResponse userResponse= new UserResponse();
    User user = userRepository.findByEmail(emailId);
    if(Objects.isNull(user)){
      throw new IllegalArgumentException("Invalid emailId or profile picture");
    }
    user.setProfilePicture(profilePicture.getBytes());
    user.setProfilePictureType(profilePicture.getContentType());
    userRepository.save(user);
    userResponse.setMessage("Profile Picture has been updated successfully");
    userResponse.setUserId(user.getUserId());
    return userResponse;
  }
  public UserProfilePictureRequest getProfilePicture(String emailId) {
    User user = userRepository.findByEmail(emailId);
    if (Objects.isNull(user)) {
      throw new RuntimeException("User not found");
    }
    if (user.getProfilePicture() == null) {
      throw new RuntimeException("Profile picture not found");
    }

    // Return an instance of UserProfilePictureRequest
    return new UserProfilePictureRequest(user.getProfilePicture(), user.getProfilePictureType());
  }



//  @Override
//  public byte[] getProfilePicture(String emailId) {
//    if(Objects.isNull(emailId)){
//      throw new IllegalArgumentException("Invalid emailId");
//    }
//    User user = userRepository.findByEmail(emailId);
//    if(Objects.nonNull(user) && Objects.nonNull(user.getProfilePicture())){
//      return user.getProfilePicture();
//    }
//    else{
//      throw new IllegalArgumentException("Invalid emailId Profile Picture not found");
//    }
//  }

  @Override
  public UserResponse deleteProfilePicture(String emailId) {
    if (Objects.isNull(emailId)) {
      throw new IllegalArgumentException("Please enter a valid emailId");
    }

    User user = userRepository.findByEmail(emailId);
    if (Objects.nonNull(user)) {
      user.setProfilePicture(null);
      user.setProfilePictureType(null); // Clear MIME type

      userRepository.save(user);

      UserResponse userResponse = new UserResponse();
      userResponse.setMessage("Profile picture is deleted successfully");
      userResponse.setUserId(user.getUserId());

      return userResponse;
    }

    throw new RuntimeException("User not found");
  }


//  private void validateUser(UserRequest userRequest) {
//    List<String> errors = new ArrayList<>();
//    if (Objects.isNull(userRequest.getFirstName())) {
//      errors.add("First name cannot be empty");
//    }
//    if (Objects.isNull(userRequest.getDob())) {
//      errors.add("DOB cannot be empty");
//    }
//    if (Objects.isNull(userRequest.getLastName())) {
//      errors.add("Last name cannot be empty");
//    }
//    if (ObjectUtils.isEmpty(userRequest.getAddress())) {
//      errors.add("Address cannot be empty");
//    }
//    if (Objects.isNull(userRequest.getEmail())) {
//      errors.add("Email cannot be empty");
//    }
//    if (Objects.isNull(userRequest.getPhoneNo())) {
//      errors.add("Phone number cannot be empty");
//    }
//    if (Objects.isNull(userRequest.getPassword())) {
//      errors.add("Password cannot be empty");
//    }
////    if (ObjectUtils.isEmpty(userRequest.getProfilePicture())) {
////      errors.add("Profile picture cannot be empty");
////    }
//    if (!errors.isEmpty()) {
//      throw new ValidationException(errors);
//    }
//  }
}