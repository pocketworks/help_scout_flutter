// Basic smoke test for the HelpScout example app.

import 'package:flutter_test/flutter_test.dart';

import 'package:help_scout_flutter_example/main.dart';

void main() {
  testWidgets('renders the HelpScout button', (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());

    expect(find.text('HelpScout Button'), findsOneWidget);
  });
}
