Feature: Quote random endpoint, downstream tests

  Scenario: Quote random endpoint gives 500 when downstream returns invalid body
    Given application response metric for QUOTE_RANDOM with response status 500 gets initialised
    And downstream response metric for QUOTE_RANDOM with response status 200 gets initialised
    And quote-random is primed to return a successful response with invalid body
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [TRONALDDUMP]"}
    And the response contains header with name Content-Type and value application/json
    And application response metric for QUOTE_RANDOM with response status 500 is increased
    And downstream response metric for QUOTE_RANDOM with response status 200 is increased

  Scenario: Quote random endpoint gives 500 when downstream returns 500
    Given application response metric for QUOTE_RANDOM with response status 500 gets initialised
    And downstream response metric for QUOTE_RANDOM with response status 500 gets initialised
    And quote-random is primed to return a 500 response with body something went wrong
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [TRONALDDUMP]"}
    And application response metric for QUOTE_RANDOM with response status 500 is increased
    And downstream response metric for QUOTE_RANDOM with response status 500 is increased

  Scenario: Quote random endpoint gives 500 when downstream returns 400
    Given application response metric for QUOTE_RANDOM with response status 500 gets initialised
    And downstream response metric for QUOTE_RANDOM with response status 400 gets initialised
    And quote-random is primed to return a 400 response with body bad request u noob
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [TRONALDDUMP]"}
    And application response metric for QUOTE_RANDOM with response status 500 is increased
    And downstream response metric for QUOTE_RANDOM with response status 400 is increased

  Scenario: Quote random endpoint gives 500 when downstream cant connect
    Given application response metric for QUOTE_RANDOM with response status 500 gets initialised
    And downstream response metric for QUOTE_RANDOM with response status ReadTimeoutException gets initialised
    And quote-random is primed to return a successful response with fixed delay of 550 milliseconds
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [TRONALDDUMP]"}
    And application response metric for QUOTE_RANDOM with response status 500 is increased
    And downstream response metric for QUOTE_RANDOM with response status ReadTimeoutException is increased

  Scenario: Quote random endpoint gives 200 even when downstream is slow
    Given application response metric for QUOTE_RANDOM with response status 200 gets initialised
    And downstream response metric for QUOTE_RANDOM with response status 200 gets initialised
    And quote-random is primed to return a successful response with dribbled delay of 10000 milliseconds in 100 chunks
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And application response metric for QUOTE_RANDOM with response status 200 is increased
    And downstream response metric for QUOTE_RANDOM with response status 200 is increased
