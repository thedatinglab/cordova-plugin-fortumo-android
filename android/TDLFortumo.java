//
//  TDLFortumo
//  The Dating Lab
//
//  Created by TheDatingLab on 06/07/15.
//  Copyright (c) 2015 TheDatingLab. All rights reserved.

package com.thedatinglab.fortumo;

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mp.MpUtils;
import mp.PaymentActivity;
import mp.PaymentRequest;
import mp.PaymentResponse;

import android.util.Log;
import java.util.List;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class TDLFortumo extends CordovaPlugin {
    private static final String TAG = "TDLFortumo";
    private static final String BUY_ACTION = "buy";
    private static final String GET_PRICE_POINT_ACTION = "getPricePoint";
    private static final String PRELOAD_PRICE_POINT_ACTION = "preLoadPricePoint";
    private static final int REQUEST_CODE = 1; // Can be anything 
    private CallbackContext callbackContext = null;

    BroadcastReceiver receiver;

    /**
     * Initializes the plugin
     *
     * @param cordova The context of the main Activity.
     * @param webView The associated CordovaWebView.
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mp.MpUtils.enablePaymentBroadcast(this.cordova.getActivity().getApplicationContext(), "com.thedatinglab.fortumo.PAYMENT_BROADCAST_PERMISSION");       
    }
    
    /**
     * Executes the request.
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return                Whether the action was valid.
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callback) throws JSONException {
        PluginResult result;
        this.callbackContext = callback;
        try {
            if (BUY_ACTION.equals(action)) {
                // Ensure that the correct plugin is used for the activityCallback
                this.cordova.setActivityResultCallback(this);

                // Build payment object
                Log.i(TAG, "Build payment object");
                PaymentRequest.PaymentRequestBuilder builder = new PaymentRequest.PaymentRequestBuilder();
                builder.setService(args.getString(0), args.getString(1));
                // builder.setDisplayString("500 Credits");                
                builder.setProductName(args.getString(2) + "_creditsBuy_app");
                builder.setType(MpUtils.PRODUCT_TYPE_CONSUMABLE);
                PaymentRequest pr = builder.build();
                makePayment(pr);

                // Return a result, with keepCallback true to allow for a return later in the code
                result = new PluginResult(PluginResult.Status.OK, "Fortumo Payment Started");
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
                return true;
            } else if (GET_PRICE_POINT_ACTION.equals(action)) {
                JSONArray pricePoint = getPricePoint(args.getString(0), args.getString(1));

                // Return a result, with keepCallback true to allow for a return later in the code
                result = new PluginResult(PluginResult.Status.OK, pricePoint);
                if (result == null) {
                    Log.i(TAG, "Get price point null result");
                } else {                    
                    Log.i(TAG, "Get price point success");
                }
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
                return true;
            } else if (PRELOAD_PRICE_POINT_ACTION.equals(action)) {
                preLoadPricePoint(args.getString(0), args.getString(1));

                // Return a result, with keepCallback true to allow for a return later in the code
                result = new PluginResult(PluginResult.Status.OK, "Price Point Pre-loaded");
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
                return true;
            }
        } catch (Exception e) {
            callback.error(e.getMessage());
        }
        return false;
    }

    private final void preLoadPricePoint(String serviceId, String inAppSecret) {
        Context context = this.cordova.getActivity().getApplicationContext();
        MpUtils.fetchPaymentData(context, serviceId, inAppSecret);
    }

    private final JSONArray getPricePoint(String serviceId, String inAppSecret) throws JSONException, ArrayStoreException {
        Context context = this.cordova.getActivity().getApplicationContext();
        List<String> pricePoint = MpUtils.getFetchedPriceData(context, serviceId, inAppSecret);
        if (pricePoint == null) {
            return null;
        }
        Log.i(TAG, pricePoint.toString());

        return new JSONArray(pricePoint);
    }

    protected final void makePayment(PaymentRequest payment) {
        Context context = this.cordova.getActivity().getApplicationContext(); 
        this.cordova.startActivityForResult(this, payment.toIntent(context), REQUEST_CODE);
    } 

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        PluginResult result;
        if (requestCode == REQUEST_CODE) {
            if (data == null) {
                return;
            }
            if (resultCode == android.app.Activity.RESULT_OK) {
                PaymentResponse response = new PaymentResponse(data);
                switch (response.getBillingStatus()) {
                    case MpUtils.MESSAGE_STATUS_BILLED:
                        try {
                            JSONObject responseJSON = new JSONObject();
                            responseJSON.put("billingStatus", response.getBillingStatus());
                            responseJSON.put("creditAmount", response.getCreditAmount());
                            responseJSON.put("creditName", response.getCreditName());
                            responseJSON.put("productName", response.getProductName());
                            responseJSON.put("date", response.getDate());
                            responseJSON.put("messageId", response.getMessageId());
                            responseJSON.put("paymentCode", response.getPaymentCode());
                            responseJSON.put("priceAmount", response.getPriceAmount());
                            responseJSON.put("priceCurrency", response.getPriceCurrency());
                            responseJSON.put("serviceId", response.getServiceId());
                            responseJSON.put("userId", response.getUserId());

                            result = new PluginResult(PluginResult.Status.OK, responseJSON);
                            this.callbackContext.sendPluginResult(result);
                        } catch (JSONException e) {
                            result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
                            this.callbackContext.sendPluginResult(result);                            
                            return;
                        }
                        break;
                    case MpUtils.MESSAGE_STATUS_FAILED:
                        result = new PluginResult(PluginResult.Status.ERROR);
                        this.callbackContext.sendPluginResult(result);                    
                        break;
                    case MpUtils.MESSAGE_STATUS_PENDING:
                        break;  
                }
            } else {
                result = new PluginResult(PluginResult.Status.ERROR);
                this.callbackContext.sendPluginResult(result);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
