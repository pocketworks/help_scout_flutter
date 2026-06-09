# help_scout_flutter

A new Flutter plugin for the implementation of HelpScout Beacon.

## Getting started

### Add dependency

You can use the command to add help_scout_flutter as a dependency with the latest stable version:

```console
$ dart pub add help_scout_flutter
```

Or you can manually add help_scout_flutter into the dependencies section in your pubspec.yaml:

```yaml
dependencies:
  help_scout_flutter: ^replace-with-latest-version
```

### Super simple to use

```dart
final helpScout = HelpScoutFlutter(
  beaconId: '*******beacon-id******',
  user: HSBeaconUser(email: 'john@example.com', name: 'John Doe'),
);

await helpScout.initialize();
await helpScout.open();
```

### Custom attributes (diagnostics)

Pass app-specific diagnostics via `HSBeaconUser.attributes`. They are forwarded
to Help Scout as custom attributes and shown to support agents in the Customer
Properties sidebar.

```dart
final user = HSBeaconUser(
  email: 'john@example.com',
  attributes: {
    'app_version': '1.0.0',
    'platform': 'iOS',
    'plan': 'premium',
  },
);

final helpScout = HelpScoutFlutter(beaconId: '*******beacon-id******', user: user);
await helpScout.initialize();
```

The values are passed straight to the Help Scout Beacon SDK, which imposes the
following rules:

- **Values must be strings.** Format booleans, dates and numbers yourself before
  passing them (e.g. `'unlimited': isUnlimited ? 'Yes' : 'No'`).
- **Maximum of 30 attributes.**
- **`name` and `email` are reserved keys.**
- **For values to sync to the Customer Properties sidebar, each key must match a
  Customer Property ID** — letters, numbers, hyphens and underscores only, with
  no spaces. Map a display label like `App Version` to a key like `app_version`.
