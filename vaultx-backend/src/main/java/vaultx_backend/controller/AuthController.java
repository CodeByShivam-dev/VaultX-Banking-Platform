package vaultx_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vaultx_backend.dto.LoginRequest;
import vaultx_backend.dto.RegisterRequest;
import vaultx_backend.entity.Account;
import vaultx_backend.entity.User;
import vaultx_backend.repository.AccountRepository;
import vaultx_backend.repository.UserRepository;
import vaultx_backend.service.OtpService;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OtpService otpService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            return "Phone number already registered";
        }

        // Generate account number
        long accountNumber = ThreadLocalRandom.current()
                .nextLong(1000000000L, 9999999999L);

        // Create User
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setAccountNumber(accountNumber);
        user.setName(request.getName());
        user.setFatherName(request.getFatherName());
        user.setMotherName(request.getMotherName());
        user.setEmail(request.getEmail());
        user.setPanCard(request.getPanCard());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        // Create Account
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountHolderName(request.getName());
        account.setBalance(0.0);

        accountRepository.save(account);
        otpService.sendOtp("91" + request.getPhone());

        return "OTP Sent Successfully";
    }



    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {

        return userRepository
                .findByPhone(request.getPhone())
                .orElse(null);
    }
    @GetMapping("/test")
    public String test() {
        return "Backend Running";
    }
    @GetMapping("/register-test")
    public String registerTest() {

        User user = new User();

        user.setUserId(UUID.randomUUID().toString());
        user.setAccountNumber(1234567890L);
        user.setName("Shivam");
        user.setFatherName("Father");
        user.setMotherName("Mother");
        user.setEmail("test@gmail.com");
        user.setPanCard("ABCDE1234F");
        user.setPhone("9999999999");

        userRepository.save(user);

        return "Saved";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody LoginRequest request) {

        User user = userRepository
                .findByPhone(request.getPhone())
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        otpService.sendOtp("91" + request.getPhone());

        return "OTP Sent Successfully";
    }

    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(
            @RequestBody Map<String, String> request) {

        String phone = request.get("phone");
        String otp = request.get("otp");

        boolean verified =
                otpService.verifyOtp("91" + phone, otp);

        Map<String, Object> response = new HashMap<>();

        response.put("success", verified);

        if (verified) {
            response.put("message", "OTP Verified");
        } else {
            response.put("message", "Invalid OTP");
        }

        return response;
    }


}