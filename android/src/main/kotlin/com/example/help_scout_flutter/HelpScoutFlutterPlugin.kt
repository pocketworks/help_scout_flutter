package com.example.help_scout_flutter

import android.net.Uri
import androidx.annotation.NonNull
import com.helpscout.beacon.Beacon
import com.helpscout.beacon.ui.BeaconActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** HelpScoutFlutterPlugin */
class HelpScoutFlutterPlugin: FlutterPlugin, MethodCallHandler {

    private lateinit var channel : MethodChannel
    private lateinit var applicationContext: android.content.Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "help_scout_flutter")
        channel.setMethodCallHandler(this)
        applicationContext = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "initialize" -> {
                val arguments = call.arguments as? Map<String, Any>
                if (arguments != null) {
                    initializeBeacon(arguments)
                    result.success("Beacon successfully initialized")
                } else {
                    result.error("INVALID_ARGUMENTS", "Arguments were null", null)
                }
            }
            "openBeacon" -> {
                val arguments = call.arguments as? Map<String, Any>
                val beaconId = arguments?.get("beaconId") as? String
                if (beaconId != null) {
                    openBeacon(beaconId)
                    result.success("Beacon opened successfully")
                } else {
                    result.error("INVALID_ARGUMENTS", "beaconId is missing", null)
                }
            }
            "clearBeacon" -> {
                clearBeacon()
                result.success("Beacon cleared successfully")
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun initializeBeacon(arguments: Map<String, Any>) {
        val email = arguments["email"] as String
        val name = arguments["name"] as String?
        val avatarString = arguments["avatar"] as String?
        val avatar = avatarString?.let { Uri.parse(it) }
        val company = arguments["company"] as String?
        val jobTitle = arguments["jobTitle"] as String?

        Beacon.identify(email, name, company, jobTitle, avatarString)

        @Suppress("UNCHECKED_CAST")
        val attributes = arguments["attributes"] as? Map<String, String>
        attributes?.forEach { (key, value) ->
            Beacon.addAttributeWithKey(key, value)
        }
    }

    private fun openBeacon(beaconId: String) {
        Beacon.Builder().withBeaconId(beaconId).build()
        BeaconActivity.open(applicationContext)
    }

    private fun clearBeacon() {
        Beacon.clear()
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
