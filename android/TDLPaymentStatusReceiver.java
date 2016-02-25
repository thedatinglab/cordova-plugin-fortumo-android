package com.thedatinglab.fortumo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
 
import mp.MpUtils;
 
public class TDLPaymentStatusReceiver extends BroadcastReceiver {
  private static String TAG = "PaymentStatusReceiver";
 
  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle extras = intent.getExtras();   
    Log.i(TAG, "- billing_status:  " + extras.getInt("billing_status"));
    Log.i(TAG, "- credit_amount:   " + extras.getString("credit_amount"));
    Log.i(TAG, "- credit_name:     " + extras.getString("credit_name"));
    Log.i(TAG, "- message_id:      " + extras.getString("message_id") );
    Log.i(TAG, "- payment_code:    " + extras.getString("payment_code"));
    Log.i(TAG, "- price_amount:    " + extras.getString("price_amount"));
    Log.i(TAG, "- price_currency:  " + extras.getString("price_currency"));
    Log.i(TAG, "- product_name:    " + extras.getString("product_name"));
    Log.i(TAG, "- service_id:      " + extras.getString("service_id"));
    Log.i(TAG, "- user_id:         " + extras.getString("user_id"));  	
  }
}