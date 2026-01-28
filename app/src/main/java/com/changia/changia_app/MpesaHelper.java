package com.changia.changia_app;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * M-Pesa Daraja API Integration
 * This class handles real M-Pesa STK Push payments.
 * <p>
 * <b>SETUP INSTRUCTIONS:</b>
 * <ol>
 *     <li>Go to <a href="https://developer.safaricom.co.ke/">Safaricom Developers Portal</a>.</li>
 *     <li>Create an account and register a new app to get your credentials.</li>
 *     <li>Replace the placeholder credentials (CONSUMER_KEY, CONSUMER_SECRET) below.</li>
 *     <li>Set up a public callback URL to receive responses from Safaricom's API.</li>
 *     <li>For production, switch to the production URLs and use live credentials.</li>
 * </ol>
 */
public class MpesaHelper {

    private static final String TAG = "MpesaHelper";

    // ============ SANDBOX CREDENTIALS (FOR TESTING) ============
    // IMPORTANT: Replace these with your actual Daraja API credentials.
    private static final String CONSUMER_KEY = "YOUR_CONSUMER_KEY_HERE";
    private static final String CONSUMER_SECRET = "YOUR_CONSUMER_SECRET_HERE";
    private static final String BUSINESS_SHORT_CODE = "174379"; // Safaricom's sandbox shortcode
    private static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"; // Safaricom's sandbox passkey
    // IMPORTANT: This URL must be a real, publicly accessible endpoint on a server you control.
    private static final String CALLBACK_URL = "https://your-server.com/mpesa/callback";

    // ============ API ENDPOINTS ============
    // Sandbox URLs (for testing)
    private static final String AUTH_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    private static final String STK_PUSH_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

    // Production URLs (uncomment and use these when going live)
    // private static final String AUTH_URL = "https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    // private static final String STK_PUSH_URL = "https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

    /**
     * Callback interface to handle the results of the M-Pesa API calls.
     */
    public interface MpesaCallback {
        void onSuccess(String checkoutRequestID, String message);
        void onError(String error);
    }

    /**
     * Initiates an M-Pesa STK Push transaction.
     * This method runs on a background thread to avoid blocking the main UI thread.
     *
     * @param phoneNumber      The customer's phone number in format 07xxxxxxxx or 2547xxxxxxxx.
     * @param amount           The amount to be paid.
     * @param accountReference A reference for the transaction (e.g., "Wallet Top Up").
     * @param transactionDesc  A short description of the transaction.
     * @param callback         The callback to be invoked with the result.
     */
    public static void initiateSTKPush(String phoneNumber, int amount, String accountReference, String transactionDesc, MpesaCallback callback) {
        // Network operations must be performed off the main thread.
        new Thread(() -> {
            try {
                // Step 1: Format the phone number to the required 254... format.
                String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
                if (formattedPhoneNumber == null) {
                    // This error will be sent back to the activity and shown to the user.
                    callback.onError("Invalid phone number format. Please use 07xxxxxxxx or 254xxxxxxxx.");
                    return;
                }

                // Step 2: Get the OAuth access token.
                String accessToken = getAccessToken();
                if (accessToken == null) {
                    callback.onError("Failed to get access token. Check credentials and internet connection.");
                    return;
                }

                // Step 3: Generate the timestamp and password required for the request.
                String timestamp = getTimestamp();
                String password = generatePassword(timestamp);

                // Step 4: Create the JSON payload for the STK Push request.
                JSONObject stkPushRequest = new JSONObject();
                stkPushRequest.put("BusinessShortCode", BUSINESS_SHORT_CODE);
                stkPushRequest.put("Password", password);
                stkPushRequest.put("Timestamp", timestamp);
                stkPushRequest.put("TransactionType", "CustomerPayBillOnline");
                stkPushRequest.put("Amount", String.valueOf(amount)); // Amount must be a string
                stkPushRequest.put("PartyA", formattedPhoneNumber); // Customer's phone
                stkPushRequest.put("PartyB", BUSINESS_SHORT_CODE); // Your paybill/till
                stkPushRequest.put("PhoneNumber", formattedPhoneNumber); // Customer's phone again
                stkPushRequest.put("CallBackURL", CALLBACK_URL);
                stkPushRequest.put("AccountReference", accountReference);
                stkPushRequest.put("TransactionDesc", transactionDesc);

                // Step 5: Send the request to the Safaricom API.
                String response = sendSTKPushRequest(accessToken, stkPushRequest.toString());

                // Step 6: Parse the API response and notify the callback.
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.has("ResponseCode") && jsonResponse.getString("ResponseCode").equals("0")) {
                    String checkoutRequestID = jsonResponse.getString("CheckoutRequestID");
                    callback.onSuccess(checkoutRequestID, "STK Push sent successfully. Please check your phone to complete the payment.");
                } else {
                    String errorMessage = jsonResponse.optString("errorMessage", "An unknown error occurred.");
                    callback.onError(errorMessage);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error initiating STK Push", e);
                callback.onError("A critical error occurred: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Gets an OAuth access token from the Safaricom API.
     */
    private static String getAccessToken() throws IOException, JSONException {
        URL url = new URL(AUTH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String credentials = CONSUMER_KEY + ":" + CONSUMER_SECRET;
        String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", basicAuth);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");
            }
        } else {
            Log.e(TAG, "Failed to get access token. Response code: " + responseCode);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = in.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                Log.e(TAG, "Error details: " + errorResponse.toString());
            }
            return null;
        }
    }

    /**
     * Sends the actual STK Push request to the M-Pesa API.
     * FIXED: Now compatible with Java 8
     */
    private static String sendSTKPushRequest(String accessToken, String jsonPayload) throws IOException {
        URL url = new URL(STK_PUSH_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        BufferedReader in;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        // FIXED: Java 8 compatible try-with-resources
        try (BufferedReader reader = in) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    /**
     * Generates the password required for the STK Push request.
     * It's a Base64 encoding of Shortcode + Passkey + Timestamp.
     */
    private static String generatePassword(String timestamp) {
        String str = BUSINESS_SHORT_CODE + PASSKEY + timestamp;
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    /**
     * Gets the current timestamp in the format yyyyMMddHHmmss.
     */
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    /**
     * Formats a phone number to the required 254XXXXXXXXX format.
     *
     * @param phoneNumber The phone number to format.
     * @return The formatted phone number, or null if invalid.
     */
    private static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        // Remove all non-digit characters (like spaces or +)
        String sanitized = phoneNumber.replaceAll("[^\\d]", "");

        if (sanitized.length() == 12 && sanitized.startsWith("254")) {
            return sanitized; // Already in the correct format 254...
        }
        if (sanitized.length() == 10 && sanitized.startsWith("0")) {
            return "254" + sanitized.substring(1); // Convert 07... to 2547...
        }
        if (sanitized.length() == 9) { // Assumes a number like 712345678 without the leading 0
            return "254" + sanitized;
        }
        return null; // The format is invalid
    }
}