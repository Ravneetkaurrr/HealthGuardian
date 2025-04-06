package com.user.management.controller;

import com.user.management.exceptions.ProcessingException;
import com.user.management.exceptions.ValidationException;
import com.user.management.request.UserPasswordRequest;
import com.user.management.request.UserProfilePictureRequest;
import com.user.management.request.UserUpdateRequest;
import com.user.management.request.UserRequest;
import com.user.management.request.UserStatusRequest;
import com.user.management.response.UserResponse;
import com.user.management.service.OtpService;
import com.user.management.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class  UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private OtpService otpService;

  @PostMapping(value = "/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest)
      throws IOException {
      UserResponse userResponse = userService.registerUser(userRequest);
      return new ResponseEntity<>(userResponse, HttpStatus.OK);
  }

  /// verify-otp?otp=121323&emailId=ravneetkr28@gmail.com
  @PostMapping(value = "/verify-otp")
  public ResponseEntity<UserResponse> verifyUser(@RequestParam String otp,
      @RequestParam String emailId,
      @RequestHeader(value = "verificationType", required = false) String verificationType) {
    UserResponse userResponse = otpService.verifyOtp(otp, emailId, verificationType);
    return new ResponseEntity<>(otpService.verifyOtp(otp, emailId, verificationType),
        HttpStatus.OK);
  }

  @PostMapping(value = "/login")
  public ResponseEntity<UserResponse> loginUser(@RequestBody UserRequest userRequest) {
    return new ResponseEntity<>(userService.loginUser(userRequest), HttpStatus.OK);
  }

  /// login-by-otp/ravneetkr28@gmail.com
  @PostMapping(value = "/otp-by-emailId/{emailId}") //login with otp
  public ResponseEntity<UserResponse> generateOtpByEmailId( @PathVariable String emailId) {
    return new ResponseEntity<>(userService.generateOtpByEmailId(emailId), HttpStatus.OK);
  }

  @PostMapping(value = "/reset-password")
  public ResponseEntity<UserResponse> resetPassword(
      @Valid @RequestBody UserPasswordRequest passwordRequest) {
    return new ResponseEntity<>(userService.resetPassword(passwordRequest), HttpStatus.OK);
  }

  @PostMapping(value = "/forget-password")
  public ResponseEntity<UserResponse> forgetPassword(
      @Valid @RequestBody UserPasswordRequest passwordRequest) {
    return new ResponseEntity<>(userService.forgetPassword(passwordRequest), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest updateUserRequest) {
    return new ResponseEntity<>(userService.updateUser(updateUserRequest), HttpStatus.OK);
  }

  @PutMapping(value = "/change-status")
  public ResponseEntity<UserResponse> changeStatus(@Valid @ModelAttribute UserStatusRequest userStatusRequest) {
    return new ResponseEntity<UserResponse>(userService.changeStatus(userStatusRequest), HttpStatus.OK);
  }
  @PostMapping(value= "/profile-picture",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> uploadProfilePicture(@RequestParam String emailId, @RequestParam MultipartFile profilePicture)
      throws IOException {
    return new ResponseEntity<UserResponse>(userService.uploadProfilePicture(emailId,profilePicture),HttpStatus.OK);
  }
  @GetMapping(value = "/profile-picture/{emailId}")
  public ResponseEntity<byte[]> getProfilePicture(@PathVariable String emailId) {
    UserProfilePictureRequest pictureRequest = userService.getProfilePicture(emailId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(pictureRequest.getProfilePictureType())) // Set correct MIME type
        .body(pictureRequest.getProfilePicture());
  }
  @DeleteMapping(value="/profile-picture/{emailId}")
  public ResponseEntity<UserResponse>deleteProfilePicture(@PathVariable String emailId){
    return new ResponseEntity<UserResponse>(userService.deleteProfilePicture(emailId),HttpStatus.OK);
  }
//@GetMapping("/login-by-gmail")
//public ResponseEntity<UserResponse> loginByGmail(@AuthenticationPrincipal OAuth2User principal) {
//  if (principal == null) {
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//  }
//  return ResponseEntity.ok(userService.loginByGmail((OAuth2AuthenticationToken) principal));
//}

}

