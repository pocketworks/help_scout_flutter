/// Represents a HelpScout Beacon user
class HSBeaconUser {
  final String email;
  final String? name;
  final String? company;
  final String? jobTitle;
  final String? avatar;

  HSBeaconUser({
    required this.email,
    this.name,
    this.company,
    this.jobTitle,
    this.avatar,
  });

  Map<String, dynamic> toMap() {
    return {
      'email': email,
      'name': name,
      'company': company,
      'jobTitle': jobTitle,
      'avatar': avatar,
    };
  }
}
