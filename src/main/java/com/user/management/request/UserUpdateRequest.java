package com.user.management.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequest {

  private String firstName;

  private String lastName;
  @Email
  @NotBlank(message = "Email cannot be null")
  private String emailId;

  @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
  private String phoneNo;

  private static String dob;

  private String address;
  private MultipartFile profilePicture;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public @Email @NotBlank(message = "Email cannot be null") String getEmailId() {
    return emailId;
  }

  public void setEmailId(@Email @NotBlank(message = "Email cannot be null") String emailId) {
    this.emailId = emailId;
  }

  public @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits") String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(
      @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits") String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public static String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public MultipartFile getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(MultipartFile profilePicture) {
    this.profilePicture = profilePicture;
  }
}



