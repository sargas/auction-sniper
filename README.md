This is a implementation of the example code in [Growing Object-Oriented Software Guided by Tests](http://www.growing-object-oriented-software.com/) with a few key differences:

- Gradle replaces Ant
- Kotlin + recent JVM replaces Java 6
- Smack is upgraded (4.x vs 3.x in the book)
- JUnit 5 (Jupiter, Platform) replaces JUnit 4
- Cucumber is used for the end-to-end test
- The XMPP server is run using TestContainers
- Mockk replaces JMockit
- AssertJ generally replaces hamcrest matchers
- JavaFx/TornadoFX replaces Swing
- TestFx replaces WindowLicker


TO-DO
-----
- [ ] Single item - join, lose without bidding
- [ ] Single item - join, bid and lose
- [ ] Single item - join, bid and win
- [ ] Single item - show price details (UI)
- [ ] Multiple Items
- [ ] Add New Items in UI
- [ ] Stop Bidding at stop price
