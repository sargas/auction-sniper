[![CI](https://github.com/sargas/auction-sniper/actions/workflows/main.yml/badge.svg)](https://github.com/sargas/auction-sniper/actions/workflows/main.yml) [![codecov](https://codecov.io/gh/sargas/auction-sniper/branch/main/graph/badge.svg?token=GVDP24HXLG)](https://codecov.io/gh/sargas/auction-sniper)

This is an implementation of the example code in [Growing Object-Oriented Software Guided by Tests](http://www.growing-object-oriented-software.com/) with a few key differences:

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
- Automated CI pipeline with GitHub Actions
- Because TestFx is used for the end-to-end test and this needs to start the JavaFX application in a special stage, the "start" of the application is really the `tornadofx.App` object as opposed to some `Main` object that creates the UI.


TO-DO
-----
- [x] Single item - join, lose without bidding
- [x] Single item - join, bid and lose
- [x] Single item - join, bid and win
- [x] Single item - show price details (UI)
- [x] Multiple Items
- [ ] Add New Items in UI
- [ ] Stop Bidding at stop price
- [ ] translator - invalid message from Auction
- [ ] translator - incorrect message version
- [ ] auction - handle `XMPPException` on send
