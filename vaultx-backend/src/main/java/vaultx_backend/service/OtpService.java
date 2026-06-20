package vaultx_backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private static final String ACCOUNT_SID = "TWILIO_ACCOUNT_SID";
    private static final String AUTH_TOKEN = "TWILIO_AUTH_TOKEN";

    private final Map<String, String> otpStore = new HashMap<>();

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendOtp(String phone) {

        String otp =
                String.format("%06d",
                        new Random().nextInt(1000000));

        String messageBody =
                "Your VaultX verification code is: " + otp;

        Message message =
                Message.creator(
                        new PhoneNumber("whatsapp:+" + phone),
                        new PhoneNumber("whatsapp:+14155238886"),
                        messageBody
                ).create();

        otpStore.put(phone, otp);

        System.out.println("OTP = " + otp);
        System.out.println("SID = " + message.getSid());
    }

    public boolean verifyOtp(String phone, String otp) {

        String storedOtp = otpStore.get(phone);

        System.out.println("Entered OTP = " + otp);
        System.out.println("Stored OTP = " + storedOtp);

        return storedOtp != null &&
                storedOtp.equals(otp);
    }

    public void sendTransactionMessage(
            String phone,
            String type,
            double amount,
            double balance) {

        String messageBody =
                "🏦 VAULTX BANKING ALERT\n\n" +

                        "Dear Customer,\n\n" +

                        "A transaction has been successfully processed on your account.\n\n" +

                        "Transaction Type : " + type.toUpperCase() + "\n" +
                        "Amount           : ₹" + amount + "\n" +
                        "Available Balance: ₹" + balance + "\n\n" +

                        "If this transaction was not initiated by you, please contact VaultX Support immediately.\n\n" +

                        "Thank you for banking with VaultX.\n" +
                        "Secure • Reliable • Future Ready";

        Message.creator(
                new PhoneNumber("whatsapp:+" + phone),
                new PhoneNumber("whatsapp:+14155238886"),
                messageBody
        ).create();
    }
}