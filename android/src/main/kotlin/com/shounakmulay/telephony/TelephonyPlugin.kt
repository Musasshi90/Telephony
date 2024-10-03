package com.shounakmulay.telephony

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.shounakmulay.telephony.sms.IncomingSmsReceiver
import com.shounakmulay.telephony.sms.SmsController
import com.shounakmulay.telephony.sms.SmsMethodCallHandler
import com.shounakmulay.telephony.utils.Constants.CHANNEL_SMS
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel


class TelephonyPlugin : FlutterPlugin, ActivityAware {

  private lateinit var smsChannel: MethodChannel

  private lateinit var smsMethodCallHandler: SmsMethodCallHandler

  private lateinit var smsController: SmsController

  private lateinit var binaryMessenger: BinaryMessenger

  private lateinit var permissionsController: PermissionsController

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(IncomingSmsReceiver.javaClass.name,"onAttachedToEngine")
    if (!this::binaryMessenger.isInitialized) {
      binaryMessenger = flutterPluginBinding.binaryMessenger
    }

    setupPlugin(flutterPluginBinding.applicationContext, binaryMessenger)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(IncomingSmsReceiver.javaClass.name,"onDetachedFromEngine")
    tearDownPlugin()
  }

  override fun onDetachedFromActivity() {
    Log.d(IncomingSmsReceiver.javaClass.name,"onDetachedFromActivity")
    tearDownPlugin()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(IncomingSmsReceiver.javaClass.name,"onReattachedToActivityForConfigChanges")
    onAttachedToActivity(binding)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(IncomingSmsReceiver.javaClass.name,"onAttachedToActivity")
    IncomingSmsReceiver.foregroundSmsChannel = smsChannel
    smsMethodCallHandler.setActivity(binding.activity)
    binding.addRequestPermissionsResultListener(smsMethodCallHandler)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(IncomingSmsReceiver.javaClass.name,"onDetachedFromActivityForConfigChanges")
    onDetachedFromActivity()
  }

  private fun setupPlugin(context: Context, messenger: BinaryMessenger) {
    Log.d(IncomingSmsReceiver.javaClass.name,"setupPlugin")
    smsController = SmsController(context)
    permissionsController = PermissionsController(context)
    smsMethodCallHandler = SmsMethodCallHandler(context, smsController, permissionsController)

    smsChannel = MethodChannel(messenger, CHANNEL_SMS)
    smsChannel.setMethodCallHandler(smsMethodCallHandler)
    smsMethodCallHandler.setForegroundChannel(smsChannel)
  }

  private fun tearDownPlugin() {
//    IncomingSmsReceiver.foregroundSmsChannel = null
//    smsChannel.setMethodCallHandler(null)
  }
}
