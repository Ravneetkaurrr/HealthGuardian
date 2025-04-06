package com.user.management.service;

import com.user.management.request.UserPasswordRequest;
import com.user.management.request.UserProfilePictureRequest;
import com.user.management.request.UserUpdateRequest;
import com.user.management.request.UserRequest;
import com.user.management.request.UserStatusRequest;
import com.user.management.response.UserResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserResponse registerUser(UserRequest userRequest) throws IOException;

  UserResponse loginUser(UserRequest userRequest);

  UserResponse generateOtpByEmailId(String emailId);

  UserResponse resetPassword(UserPasswordRequest passwordRequest);

  UserResponse forgetPassword(UserPasswordRequest passwordRequest);

  UserResponse changeStatus(UserStatusRequest userStatusRequest);


  UserResponse updateUser(UserUpdateRequest updateUserRequest);

  UserResponse uploadProfilePicture(String emailId, MultipartFile profilePicture)
      throws IOException;

  UserProfilePictureRequest getProfilePicture(String emailId);

  UserResponse deleteProfilePicture(String emailId);

}
