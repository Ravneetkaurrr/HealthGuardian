package com.user.management.utils;

import com.user.management.constants.UserConstants;
import com.user.management.constants.UserStatus;
import com.user.management.exceptions.ProcessingException;
import com.user.management.model.User;
import com.user.management.model.UserOtp;
import com.user.management.request.UserRequest;

import jakarta.mail.Multipart;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.web.multipart.MultipartFile;

public class UserUtils {

  public static UserOtp createUserOtp(String otp, String email) {
    UserOtp userOtp = new UserOtp();
    userOtp.setOtp(otp);
    userOtp.setEmail(email);
    LocalDateTime currentDateTime = LocalDateTime.now();
    userOtp.setCreatedDate(currentDateTime);
    userOtp.setExpiryDate(currentDateTime.plusMinutes(5));
    return userOtp;
  }

  public static User convertUserRequestToUser(UserRequest userRequest) throws IOException {
    User user = new User();
    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    user.setAddress(userRequest.getAddress());
    user.setEmail(userRequest.getEmail());
    user.setPhoneNo(userRequest.getPhoneNo());
    user.setStatus(UserStatus.PENDING);
    try {
      user.setDob(
          LocalDate.parse(userRequest.getDob(), UserConstants.DATE_TIME_FORMATTER_DD_MM_YYYY));
    } catch (DateTimeParseException de) {
      throw new ProcessingException("Date format must be dd/MM/yyyy");
    }
    user.setCreatedDate(LocalDateTime.now());
    user.setPassword(userRequest.getPassword());
    return user;
  }
//  public static byte[] convertProfilePictureToBytes(MultipartFile profilePicture) throws IOException {
//    try(InputStream inputStream= profilePicture.getInputStream()) {
//      return inputStream.readAllBytes();
//      }
//  }
}
