package com.user.management.repository;

import com.user.management.model.UserOtp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends MongoRepository<UserOtp, String> {
   UserOtp findByOtp(String otp);
}
