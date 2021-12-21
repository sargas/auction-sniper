Feature: Auction Sniper End-to-End tests

  Scenario: Sniper joins auction without bidding until the auction closes
    Given an auction for item "123" has started
    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"
    When the auction for item "123" closes
    And we show the auction for item "123" with status LOST

  Scenario: Sniper joins auction, places a higher bid, and loses
    Given an auction for item "123" has started

    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"

    When the auction reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098
    And the sniper places a bid for 1098 USD

    When the auction for item "123" closes
    Then we show the auction for item "123" as LOST having the winning bid 1000 with our last bid 1098

  Scenario: Sniper joins auction, places a higher bid, and wins
    Given an auction for item "123" has started

    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"

    When the auction reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098

    When the sniper places a bid for 1098 USD
    Then the auction reports we have the highest bid at 1098 USD with an increment of 97 USD
    And we show the auction for item "123" as WINNING having the winning bid 1098 with our last bid 1098


    When the auction for item "123" closes
    Then we show the auction for item "123" as WON having the winning bid 1098 with our last bid 1098
