## Unreleased

* Added support for custom user attributes (diagnostics) via `HSBeaconUser.attributes`,
  forwarded to the Beacon as custom attributes on iOS and Android.
* Fixed `initialize` failing on Android with `SDKInitException`: the beacon id sent by
  the Dart layer is now applied (via `Beacon.Builder`) before `Beacon.identify` /
  `Beacon.addAttributeWithKey`, which the Android SDK requires.
* Fixed `initialize` throwing on Android when the user email is null or blank; the user
  is now treated as anonymous (matching iOS behaviour) and attributes are still applied.
* The Android plugin now implements `ActivityAware` and opens the Beacon from the host
  `Activity` instead of the application context. `openBeacon` returns a `NO_ACTIVITY`
  error when no Activity is attached.
* Android SDK exceptions are now surfaced as structured `BEACON_ERROR` method-channel
  errors instead of crashing the channel handler.

## 0.0.1

* This release includes:
* * The HelpScout Beacon support for iOS

## 0.0.2

* This release includes:
* * Added the HelpScout Beacon support for Android
