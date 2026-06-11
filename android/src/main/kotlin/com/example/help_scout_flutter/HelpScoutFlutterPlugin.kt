package com.example.help_scout_flutter

import android.app.Activity
import androidx.annotation.NonNull
import com.helpscout.beacon.Beacon
import com.helpscout.beacon.ui.BeaconActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** HelpScoutFlutterPlugin */
class HelpScoutFlutterPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

    private lateinit var channel : MethodChannel
    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "help_scout_flutter")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            when (call.method) {
                "initialize" -> {
                    val arguments = call.arguments as? Map<String, Any>
                    if (arguments == null) {
                        result.error("INVALID_ARGUMENTS", "Arguments were null", null)
                        return
                    }
                    val beaconId = arguments["beaconId"] as? String
                    if (beaconId.isNullOrBlank()) {
                        result.error("INVALID_ARGUMENTS", "beaconId is missing", null)
                        return
                    }
                    initializeBeacon(beaconId, arguments)
                    result.success("Beacon successfully initialized")
                }
                "openBeacon" -> {
                    val arguments = call.arguments as? Map<String, Any>
                    val beaconId = arguments?.get("beaconId") as? String
                    if (beaconId.isNullOrBlank()) {
                        result.error("INVALID_ARGUMENTS", "beaconId is missing", null)
                        return
                    }
                    val activity = this.activity
                    if (activity == null) {
                        result.error("NO_ACTIVITY", "Cannot open Beacon: plugin is not attached to an Activity", null)
                        return
                    }
                    openBeacon(beaconId, activity)
                    result.success("Beacon opened successfully")
                }
                "clearBeacon" -> {
                    clearBeacon()
                    result.success("Beacon cleared successfully")
                }
                else -> {
                    result.notImplemented()
                }
            }
        } catch (e: Exception) {
            result.error("BEACON_ERROR", e.message, null)
        }
    }

    private fun initializeBeacon(beaconId: String, arguments: Map<String, Any>) {
        // The SDK requires the beacon id to be set before identify/attribute
        // calls; without this, Beacon.identify throws SDKInitException.
        Beacon.Builder().withBeaconId(beaconId).build()

        val email = arguments["email"] as? String
        val name = arguments["name"] as? String
        val avatarString = arguments["avatar"] as? String
        val company = arguments["company"] as? String
        val jobTitle = arguments["jobTitle"] as? String

        // Beacon.identify throws on a null/blank email; treat that as an
        // anonymous user instead (the iOS SDK silently ignores invalid emails).
        if (!email.isNullOrBlank()) {
            Beacon.identify(email, name, company, jobTitle, avatarString)
        }

        @Suppress("UNCHECKED_CAST")
        val attributes = arguments["attributes"] as? Map<String, String>
        attributes?.forEach { (key, value) ->
            Beacon.addAttributeWithKey(key, value)
        }
    }

    private fun openBeacon(beaconId: String, activity: Activity) {
        Beacon.Builder().withBeaconId(beaconId).build()
        BeaconActivity.open(activity)
    }

    private fun clearBeacon() {
        Beacon.clear()
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}
