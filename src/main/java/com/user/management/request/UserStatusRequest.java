package com.user.management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

//@Getter
//@Setter
public class UserStatusRequest {

  @NotBlank(message = "Email cannot be null")
  private String emailId;
  @NotBlank(message = "Status cannot be null")
  @Pattern(regexp = "ACTIVE|DELETED|INACTIVE", message = "Invalid Status")
  private String status;

  public @NotBlank(message = "Email cannot be null") String getEmailId() {
    return emailId;
  }

  public void setEmailId(@NotBlank(message = "Email cannot be null") String emailId) {
    this.emailId = emailId;
  }

  public @NotBlank(message = "Status cannot be null") @Pattern(regexp = "ACTIVE|DELETED|INACTIVE", message = "Invalid Status") String getStatus() {
    return status;
  }

  public void setStatus(
      @NotBlank(message = "Status cannot be null") @Pattern(regexp = "ACTIVE|DELETED|INACTIVE", message = "Invalid Status") String status) {
    this.status = status;
  }
}
