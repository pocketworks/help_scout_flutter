import 'package:flutter_test/flutter_test.dart';
import 'package:help_scout_flutter/help_scout_flutter.dart';
import 'package:help_scout_flutter/help_scout_flutter_platform_interface.dart';
import 'package:help_scout_flutter/help_scout_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockHelpScoutFlutterPlatform
    with MockPlatformInterfaceMixin
    implements HelpScoutFlutterPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final HelpScoutFlutterPlatform initialPlatform =
      HelpScoutFlutterPlatform.instance;

  test('$MethodChannelHelpScoutFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelHelpScoutFlutter>());
  });

  test('getPlatformVersion', () async {
    HelpScoutFlutterPlatform helpScoutFlutterPlugin =
        HelpScoutFlutterPlatform.instance;
    MockHelpScoutFlutterPlatform fakePlatform = MockHelpScoutFlutterPlatform();
    HelpScoutFlutterPlatform.instance = fakePlatform;

    expect(await helpScoutFlutterPlugin.getPlatformVersion(), '42');
  });
}
