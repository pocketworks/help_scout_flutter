package com.example.help_scout_flutter

import android.app.Activity
import com.helpscout.beacon.Beacon
import com.helpscout.beacon.ui.BeaconActivity
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockedConstruction
import org.mockito.MockedStatic
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class HelpScoutFlutterPluginTest {

    private lateinit var plugin: HelpScoutFlutterPlugin
    private lateinit var result: MethodChannel.Result
    private lateinit var beaconStatic: MockedStatic<Beacon>
    private lateinit var beaconActivityStatic: MockedStatic<BeaconActivity>
    private lateinit var builderConstruction: MockedConstruction<Beacon.Builder>

    @BeforeEach
    fun setUp() {
        plugin = HelpScoutFlutterPlugin()
        result = mock(MethodChannel.Result::class.java)
        beaconStatic = mockStatic(Beacon::class.java)
        beaconActivityStatic = mockStatic(BeaconActivity::class.java)
        builderConstruction = mockConstruction(Beacon.Builder::class.java) { builder, _ ->
            `when`(builder.withBeaconId(anyString())).thenReturn(builder)
        }
    }

    @AfterEach
    fun tearDown() {
        builderConstruction.close()
        beaconActivityStatic.close()
        beaconStatic.close()
    }

    private fun attachActivity(): Activity {
        val activity = mock(Activity::class.java)
        val binding = mock(ActivityPluginBinding::class.java)
        `when`(binding.activity).thenReturn(activity)
        plugin.onAttachedToActivity(binding)
        return activity
    }

    // --- initialize ---

    @Test
    fun `initialize sets beacon id before identifying`() {
        val arguments = mapOf("beaconId" to "beacon-123", "email" to "user@example.com")

        plugin.onMethodCall(MethodCall("initialize", arguments), result)

        val builder = builderConstruction.constructed().single()
        verify(builder).withBeaconId("beacon-123")
        verify(builder).build()
        beaconStatic.verify { Beacon.identify("user@example.com", null, null, null, null) }
        verify(result).success("Beacon successfully initialized")
    }

    @Test
    fun `initialize with blank email skips identify but still succeeds`() {
        val arguments = mapOf(
            "beaconId" to "beacon-123",
            "email" to "",
            "attributes" to mapOf("app_version" to "1.2.3"),
        )

        plugin.onMethodCall(MethodCall("initialize", arguments), result)

        beaconStatic.verify({ Beacon.identify(anyString(), any(), any(), any(), any()) }, never())
        beaconStatic.verify { Beacon.addAttributeWithKey("app_version", "1.2.3") }
        verify(result).success("Beacon successfully initialized")
    }

    @Test
    fun `initialize forwards attributes to the beacon`() {
        val arguments = mapOf(
            "beaconId" to "beacon-123",
            "email" to "user@example.com",
            "attributes" to mapOf("plan" to "Premium", "platform" to "Android"),
        )

        plugin.onMethodCall(MethodCall("initialize", arguments), result)

        beaconStatic.verify { Beacon.addAttributeWithKey("plan", "Premium") }
        beaconStatic.verify { Beacon.addAttributeWithKey("platform", "Android") }
        verify(result).success("Beacon successfully initialized")
    }

    @Test
    fun `initialize without beaconId returns invalid arguments error`() {
        val arguments = mapOf("email" to "user@example.com")

        plugin.onMethodCall(MethodCall("initialize", arguments), result)

        verify(result).error(eq("INVALID_ARGUMENTS"), eq("beaconId is missing"), isNull())
        beaconStatic.verify({ Beacon.identify(anyString(), any(), any(), any(), any()) }, never())
    }

    @Test
    fun `initialize with null arguments returns invalid arguments error`() {
        plugin.onMethodCall(MethodCall("initialize", null), result)

        verify(result).error(eq("INVALID_ARGUMENTS"), eq("Arguments were null"), isNull())
    }

    @Test
    fun `initialize surfaces sdk exceptions as beacon error`() {
        beaconStatic.`when`<Unit> { Beacon.identify(anyString(), any(), any(), any(), any()) }
            .thenThrow(RuntimeException("identify() called with null or empty email"))
        val arguments = mapOf("beaconId" to "beacon-123", "email" to "user@example.com")

        plugin.onMethodCall(MethodCall("initialize", arguments), result)

        verify(result).error(eq("BEACON_ERROR"), eq("identify() called with null or empty email"), isNull())
    }

    // --- openBeacon ---

    @Test
    fun `openBeacon with attached activity opens from the activity context`() {
        val activity = attachActivity()
        val arguments = mapOf("beaconId" to "beacon-123")

        plugin.onMethodCall(MethodCall("openBeacon", arguments), result)

        beaconActivityStatic.verify { BeaconActivity.open(activity) }
        verify(result).success("Beacon opened successfully")
    }

    @Test
    fun `openBeacon without activity returns no activity error`() {
        val arguments = mapOf("beaconId" to "beacon-123")

        plugin.onMethodCall(MethodCall("openBeacon", arguments), result)

        verify(result).error(
            eq("NO_ACTIVITY"),
            eq("Cannot open Beacon: plugin is not attached to an Activity"),
            isNull(),
        )
        beaconActivityStatic.verify({ BeaconActivity.open(any()) }, never())
    }

    @Test
    fun `openBeacon after activity detached returns no activity error`() {
        attachActivity()
        plugin.onDetachedFromActivity()
        val arguments = mapOf("beaconId" to "beacon-123")

        plugin.onMethodCall(MethodCall("openBeacon", arguments), result)

        verify(result).error(
            eq("NO_ACTIVITY"),
            eq("Cannot open Beacon: plugin is not attached to an Activity"),
            isNull(),
        )
    }

    @Test
    fun `openBeacon without beaconId returns invalid arguments error`() {
        attachActivity()

        plugin.onMethodCall(MethodCall("openBeacon", emptyMap<String, Any>()), result)

        verify(result).error(eq("INVALID_ARGUMENTS"), eq("beaconId is missing"), isNull())
    }

    // --- clearBeacon ---

    @Test
    fun `clearBeacon clears the beacon`() {
        plugin.onMethodCall(MethodCall("clearBeacon", null), result)

        beaconStatic.verify { Beacon.clear() }
        verify(result).success("Beacon cleared successfully")
    }

    // --- unknown method ---

    @Test
    fun `unknown method reports not implemented`() {
        plugin.onMethodCall(MethodCall("unknown", null), result)

        verify(result).notImplemented()
    }
}
