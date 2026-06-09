import 'package:flutter_test/flutter_test.dart';
import 'package:help_scout_flutter/help_scout_flutter.dart';
import 'package:help_scout_flutter/help_scout_flutter_method_channel.dart';
import 'package:help_scout_flutter/help_scout_flutter_platform_interface.dart';
import 'package:help_scout_flutter/model.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHelpScoutFlutterPlatform with MockPlatformInterfaceMixin implements HelpScoutFlutterPlatform {
  HSBeaconUser? lastUser;
  String? lastBeaconId;
  bool clearCalled = false;

  @override
  Future<String?> initialize(HSBeaconUser user, String beaconId) async {
    lastUser = user;
    lastBeaconId = beaconId;
    return null;
  }

  @override
  Future<String?> open(String beaconId) async {
    lastBeaconId = beaconId;
    return null;
  }

  @override
  Future<String?> clear() async {
    clearCalled = true;
    return null;
  }
}

void main() {
  final HelpScoutFlutterPlatform initialPlatform = HelpScoutFlutterPlatform.instance;

  test('$MethodChannelHelpScoutFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHelpScoutFlutter>());
  });

  group('HelpScoutFlutter facade', () {
    late MockHelpScoutFlutterPlatform fakePlatform;

    setUp(() {
      fakePlatform = MockHelpScoutFlutterPlatform();
      HelpScoutFlutterPlatform.instance = fakePlatform;
    });

    test('initialize delegates user (with attributes) and beaconId', () async {
      final user = HSBeaconUser(email: 'john@example.com', attributes: {'app_version': '1.0.0', 'platform': 'iOS'});
      final plugin = HelpScoutFlutter(beaconId: 'beacon-123', user: user);

      final result = await plugin.initialize();

      expect(result, isNull);
      expect(fakePlatform.lastBeaconId, 'beacon-123');
      expect(fakePlatform.lastUser?.email, 'john@example.com');
      expect(fakePlatform.lastUser?.attributes, {'app_version': '1.0.0', 'platform': 'iOS'});
    });

    test('open delegates beaconId', () async {
      final plugin = HelpScoutFlutter(
        beaconId: 'beacon-123',
        user: HSBeaconUser(email: 'john@example.com'),
      );

      await plugin.open();

      expect(fakePlatform.lastBeaconId, 'beacon-123');
    });

    test('clear delegates to the platform', () async {
      final plugin = HelpScoutFlutter(
        beaconId: 'beacon-123',
        user: HSBeaconUser(email: 'john@example.com'),
      );

      await plugin.clear();

      expect(fakePlatform.clearCalled, isTrue);
    });
  });

  group('HSBeaconUser.toMap', () {
    test('omits attributes when null', () {
      final map = HSBeaconUser(email: 'a@b.com').toMap();

      expect(map.containsKey('attributes'), isFalse);
    });

    test('omits attributes when empty', () {
      final map = HSBeaconUser(email: 'a@b.com', attributes: {}).toMap();

      expect(map.containsKey('attributes'), isFalse);
    });

    test('includes attributes verbatim when populated', () {
      final attributes = {'app_version': '1.0.0', 'plan': 'premium'};

      final map = HSBeaconUser(email: 'a@b.com', attributes: attributes).toMap();

      expect(map['attributes'], attributes);
    });
  });
}
