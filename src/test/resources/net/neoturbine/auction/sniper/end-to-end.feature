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

    When the auction for "123" reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098
    And the sniper places a bid on "123" for 1098 USD

    When the auction for item "123" closes
    Then we show the auction for item "123" as LOST having the winning bid 1000 with our last bid 1098

  Scenario: Sniper joins auction, places a higher bid, and wins
    Given an auction for item "123" has started

    When we start bidding for item "123"
    Then server receives join request from sniper for item "123"

    When the auction for "123" reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098

    When the sniper places a bid on "123" for 1098 USD
    Then the auction for "123" reports we have the highest bid at 1098 USD with an increment of 97 USD
    And we show the auction for item "123" as WINNING having the winning bid 1098 with our last bid 1098


    When the auction for item "123" closes
    Then we show the auction for item "123" as WON having the winning bid 1098 with our last bid 1098

  Scenario: Sniper bids for multiple items
    Given an auction for item "123" has started
    And an auction for item "321" has started

    When we start bidding for items:
    | 123 | 321 |
    Then server receives join request from sniper for item "123"
    And server receives join request from sniper for item "321"

    When the auction for "123" reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098
    And the sniper places a bid on "123" for 1098 USD

    When the auction for "321" reports another bidder has the highest bid at 500 USD with an increment of 21 USD
    Then we show the auction for item "321" as BIDDING having the winning bid 500 with our last bid 521
    And the sniper places a bid on "321" for 521 USD

    When the auction for "123" reports we have the highest bid at 1098 USD with an increment of 97 USD
    And the auction for "321" reports we have the highest bid at 521 USD with an increment of 22 USD
    Then we show the auction for item "123" as WINNING having the winning bid 1098 with our last bid 1098
    And we show the auction for item "321" as WINNING having the winning bid 521 with our last bid 521

    When the auction for item "123" closes
    And the auction for item "321" closes
    Then we show the auction for item "123" as WON having the winning bid 1098 with our last bid 1098
    And we show the auction for item "321" as WON having the winning bid 521 with our last bid 521

  Scenario: Sniper loses an auction when the price is too high
    Given an auction for item "123" has started

    When we start bidding for item "123" with stop price 1100 USD
    Then server receives join request from sniper for item "123"

    When the auction for "123" reports another bidder has the highest bid at 1000 USD with an increment of 98 USD
    Then we show the auction for item "123" as BIDDING having the winning bid 1000 with our last bid 1098
    And the sniper places a bid on "123" for 1098 USD

    When the auction for "123" reports a third bidder has the highest bid at 1197 USD with an increment of 10 USD
    Then we show the auction for item "123" as LOSING having the winning bid 1197 with our last bid 1098

    When the auction for "123" reports a fourth bidder has the highest bid at 1207 USD with an increment of 10 USD
    Then we show the auction for item "123" as LOSING having the winning bid 1207 with our last bid 1098

    When the auction for item "123" closes
    Then we show the auction for item "123" as LOST having the winning bid 1207 with our last bid 1098
