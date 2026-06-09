import 'help_scout_flutter_platform_interface.dart';
import 'model.dart';

/// Public API for Flutter apps
class HelpScoutFlutter {
  final String beaconId;
  final HSBeaconUser user;

  HelpScoutFlutter({required this.beaconId, required this.user});

  /// Initialize the beacon
  Future<String?> initialize() => HelpScoutFlutterPlatform.instance.initialize(user, beaconId);

  /// Open the beacon UI
  Future<String?> open() => HelpScoutFlutterPlatform.instance.open(beaconId);

  /// Clear beacon data
  Future<String?> clear() => HelpScoutFlutterPlatform.instance.clear();
}
