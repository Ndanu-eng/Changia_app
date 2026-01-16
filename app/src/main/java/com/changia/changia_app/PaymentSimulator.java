package com.changia.changia_app;

import android.content.Context;
import android.widget.Toast;

public class PaymentSimulator {

    public static void simulateMpesaPayment(Context context, String phone, double amount, PaymentCallback callback) {
        // Simulate M-Pesa API call
        Toast.makeText(context, "Sending STK Push to " + phone, Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            // Simulate successful payment
            boolean success = true; // In real app, check API response
            if (success) {
                callback.onSuccess("Payment of KES " + amount + " successful");
            } else {
                callback.onError("Payment failed. Please try again.");
            }
        }, 3000);
    }

    public static void simulateVisaPayment(Context context, String cardNumber, double amount, PaymentCallback callback) {
        // Simulate Visa payment
        Toast.makeText(context, "Processing Visa payment...", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            boolean success = true;
            if (success) {
                callback.onSuccess("Visa payment of KES " + amount + " successful");
            } else {
                callback.onError("Payment declined. Check card details.");
            }
        }, 3000);
    }

    public interface PaymentCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}