/// Represents a HelpScout Beacon user
class HSBeaconUser {
  final String email;
  final String? name;
  final String? company;
  final String? jobTitle;
  final String? avatar;

  /// Custom attributes attached to the user, surfaced to support agents in the
  /// Help Scout Customer Properties sidebar.
  ///
  /// Use this to forward app-specific diagnostics (e.g. app version, platform,
  /// plan tier). Constraints imposed by the Beacon SDK:
  /// - Values must be strings; format booleans/dates/numbers before passing.
  /// - A maximum of 30 attributes is supported.
  /// - `name` and `email` are reserved keys.
  /// - For values to sync to the sidebar, keys must match Customer Property IDs:
  ///   letters, numbers, hyphens and underscores only — no spaces (map a label
  ///   like `App Version` to a key like `app_version`).
  final Map<String, String>? attributes;

  HSBeaconUser({required this.email, this.name, this.company, this.jobTitle, this.avatar, this.attributes});

  Map<String, dynamic> toMap() {
    final map = <String, dynamic>{
      'email': email,
      'name': name,
      'company': company,
      'jobTitle': jobTitle,
      'avatar': avatar,
    };
    if (attributes != null && attributes!.isNotEmpty) {
      map['attributes'] = attributes;
    }
    return map;
  }
}
