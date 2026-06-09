import 'package:flutter/services.dart';
import 'help_scout_flutter_platform_interface.dart';
import 'model.dart';

/// The default MethodChannel implementation
class MethodChannelHelpScoutFlutter extends HelpScoutFlutterPlatform {
  final MethodChannel _channel = const MethodChannel('help_scout_flutter');

  @override
  Future<String?> initialize(HSBeaconUser user, String beaconId) async {
    final data = user.toMap()..['beaconId'] = beaconId;
    try {
      final result = await _channel.invokeMethod<String>('initialize', data);
      return result;
    } on PlatformException catch (e) {
      return 'Failed to initialize: ${e.message}';
    }
  }

  @override
  Future<String?> open(String beaconId) async {
    try {
      final result = await _channel.invokeMethod<String>('openBeacon', {'beaconId': beaconId});
      return result;
    } on PlatformException catch (e) {
      return 'Failed to open beacon: ${e.message}';
    }
  }

  @override
  Future<String?> clear() async {
    try {
      final result = await _channel.invokeMethod<String>('clearBeacon');
      return result;
    } on PlatformException catch (e) {
      return 'Failed to clear beacon: ${e.message}';
    }
  }
}
