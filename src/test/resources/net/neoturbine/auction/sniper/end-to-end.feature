Feature: Auction Sniper

  Scenario: Sniper Joins Auction until Auction Closes
    Given an auction for item "123" has started
    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"
    When the auction for item "123" closes
    And we show the auction for item "123" as "lost"
