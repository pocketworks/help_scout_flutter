import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'help_scout_flutter_method_channel.dart';
import 'model.dart';

/// The platform interface for HelpScoutFlutter
abstract class HelpScoutFlutterPlatform extends PlatformInterface {
  HelpScoutFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  /// Default instance using MethodChannel
  static HelpScoutFlutterPlatform _instance = MethodChannelHelpScoutFlutter();

  static HelpScoutFlutterPlatform get instance => _instance;

  static set instance(HelpScoutFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Initialize the beacon
  Future<String?> initialize(HSBeaconUser user, String beaconId);

  /// Open the beacon UI
  Future<String?> open(String beaconId);

  /// Clear beacon data
  Future<String?> clear();
}
