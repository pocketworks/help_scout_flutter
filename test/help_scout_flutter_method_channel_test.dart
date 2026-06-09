import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:help_scout_flutter/help_scout_flutter_method_channel.dart';
import 'package:help_scout_flutter/model.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  final MethodChannelHelpScoutFlutter platform = MethodChannelHelpScoutFlutter();
  const MethodChannel channel = MethodChannel('help_scout_flutter');

  final List<MethodCall> log = <MethodCall>[];

  void mockHandler(Future<Object?>? Function(MethodCall call) handler) {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, (call) {
      log.add(call);
      return handler(call);
    });
  }

  setUp(() {
    log.clear();
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  group('initialize', () {
    test('sends initialize with beaconId and attributes in the payload', () async {
      mockHandler((_) async => 'Beacon successfully initialized');
      final user = HSBeaconUser(
        email: 'john@example.com',
        name: 'John Doe',
        attributes: {'app_version': '1.0.0', 'platform': 'iOS'},
      );

      await platform.initialize(user, 'beacon-123');

      expect(log, hasLength(1));
      expect(log.single.method, 'initialize');
      final args = Map<String, dynamic>.from(log.single.arguments as Map);
      expect(args['email'], 'john@example.com');
      expect(args['beaconId'], 'beacon-123');
      expect(args['attributes'], {'app_version': '1.0.0', 'platform': 'iOS'});
    });

    test('omits attributes key when none supplied', () async {
      mockHandler((_) async => null);

      await platform.initialize(HSBeaconUser(email: 'john@example.com'), 'beacon-123');

      final args = Map<String, dynamic>.from(log.single.arguments as Map);
      expect(args.containsKey('attributes'), isFalse);
    });

    test('maps a PlatformException to an error string', () async {
      mockHandler((_) async => throw PlatformException(code: 'ERR', message: 'boom'));

      final result = await platform.initialize(HSBeaconUser(email: 'john@example.com'), 'beacon-123');

      expect(result, 'Failed to initialize: boom');
    });
  });

  group('open', () {
    test('sends openBeacon with beaconId', () async {
      mockHandler((_) async => null);

      await platform.open('beacon-123');

      expect(log.single.method, 'openBeacon');
      expect((log.single.arguments as Map)['beaconId'], 'beacon-123');
    });

    test('maps a PlatformException to an error string', () async {
      mockHandler((_) async => throw PlatformException(code: 'ERR', message: 'boom'));

      expect(await platform.open('beacon-123'), 'Failed to open beacon: boom');
    });
  });

  group('clear', () {
    test('sends clearBeacon with no payload', () async {
      mockHandler((_) async => null);

      await platform.clear();

      expect(log.single.method, 'clearBeacon');
      expect(log.single.arguments, isNull);
    });

    test('maps a PlatformException to an error string', () async {
      mockHandler((_) async => throw PlatformException(code: 'ERR', message: 'boom'));

      expect(await platform.clear(), 'Failed to clear beacon: boom');
    });
  });
}
