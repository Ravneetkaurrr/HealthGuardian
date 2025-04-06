package com.user.management.request;
public class UserProfilePictureRequest {
    private byte[] profilePicture;
    private String profilePictureType;

    // Add constructor for easy initialization
    public UserProfilePictureRequest(byte[] profilePicture, String profilePictureType) {
      this.profilePicture = profilePicture;
      this.profilePictureType = profilePictureType;
    }
    public byte[] getProfilePicture() {
      return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
      this.profilePicture = profilePicture;
    }

    public String getProfilePictureType() {
      return profilePictureType;
    }

    public void setProfilePictureType(String profilePictureType) {
      this.profilePictureType = profilePictureType;
    }
}
