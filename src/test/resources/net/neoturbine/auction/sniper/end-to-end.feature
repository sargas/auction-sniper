Feature: Auction Sniper End-to-End tests

  Scenario: Sniper joins auction without bidding until the auction closes
    Given an auction for item "123" has started
    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"
    When the auction for item "123" closes
    And we show the auction for item "123" as "LOST"

  Scenario: Sniper joins auction, places a higher bid, and loses
    Given an auction for item "123" has started

    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"

    When the auction reports another bidder has the highest bid at $1000 with an increment of $98
    Then we show the auction for item "123" as "BIDDING"
    And the sniper places a bid

    When the auction for item "123" closes
    Then we show the auction for item "123" as "LOST"
