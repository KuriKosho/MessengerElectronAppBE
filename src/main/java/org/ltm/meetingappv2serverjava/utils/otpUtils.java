package org.ltm.meetingappv2serverjava.utils;

public class otpUtils {
    public static String generateOTP() {
        int randomPin   =(int) (Math.random()*9000)+1000;
        String otp  = String.valueOf(randomPin);
        return otp;
    }
}
