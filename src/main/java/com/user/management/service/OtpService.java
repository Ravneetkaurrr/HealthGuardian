package com.user.management.service;

import com.user.management.constants.UserStatus;
import com.user.management.exceptions.VerificationException;
import com.user.management.model.EmailDetails;
import com.user.management.model.User;
import com.user.management.model.UserOtp;
import com.user.management.repository.EmailDetailsRepository;
import com.user.management.repository.OtpRepository;
import com.user.management.repository.UserRepository;
import com.user.management.response.UserResponse;
import jakarta.mail.MessagingException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

  @Autowired
  private OtpRepository otpRepository;

  @Autowired
  private EmailDetailsRepository emailDetailsRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private UserRepository userRepository;

  public String generateOtp() {
    SecureRandom random = new SecureRandom();
    return String.format("%06d", random.nextInt(1000000));
  }

  public void saveOtp(UserOtp userOtp) {
    otpRepository.save(userOtp);
  }

  public UserResponse verifyOtp(String otp, String emailId, String verificationType) {
    UserResponse userResponse = new UserResponse();
    UserOtp userOtp = otpRepository.findByOtp(otp);
    if (Objects.nonNull(userOtp)) {
      if (userOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
        throw new VerificationException("Time expired");
      }
      User user = userRepository.findByEmailId(emailId);
      if (Objects.nonNull(user)) {
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
      } else {
        throw new  VerificationException("Enter a valid email");
      }
      String emailType="VERIFICATION"; //BY DEFAULT EMAILTYPE IS VERIFICATION
      //BUT IF VERIFICATIONTYPE IS LOGINWITHOTP THEN LOGIN EMAILTYPE WILL BE THERE OTHEREWISE VERIFICATION
//      if(verificationType.equalsIgnoreCase("LOGIN_BY_OTP")){
//        emailType="LOGIN";
//      }
      if ("LOGIN_BY_OTP".equalsIgnoreCase(Objects.toString(verificationType, ""))) {
        emailType = "LOGIN";
      }
      EmailDetails emailDetails = emailDetailsRepository.findByEmailType(emailType);
      try {
        emailService.sendEmail(emailId, emailDetails.getEmailSubject(),
            emailDetails.getEmailBody().replace("${FIRST_NAME}", user.getFirstName())
                .replace("${OTP}", otp));
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }
      userResponse.setUserId(user.getUserId());
      if("LOGIN".equals(emailType)){
        userResponse.setMessage("User has been logged in");
      }
      else{
        userResponse.setMessage("User has been successfully verified");
      }
      return userResponse;
    }
    return userResponse;
  }

}
