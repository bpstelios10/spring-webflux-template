Feature: Quote random endpoint, downstream-yoda tests

  Scenario: Quote random endpoint gives 500 when downstream-yoda returns invalid body
    Given application response metric for QUOTE_RANDOM with response status 500 gets initialised
    And downstream response metric for YODA_SPEECH with response status 200 gets initialised
    And yoda-speech is primed to return a successful response with invalid body
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [YODASPEECH]"}
    And the response contains header with name Content-Type and value application/json
    And application response metric for QUOTE_RANDOM with response status 500 is increased
    And downstream response metric for YODA_SPEECH with response status 200 is increased

  Scenario: Quote random endpoint gives 500 when downstream-yoda returns 500
    Given downstream response metric for YODA_SPEECH with response status 500 gets initialised
    And yoda-speech is primed to return a 500 response with body something went wrong
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [YODASPEECH]"}
    And downstream response metric for YODA_SPEECH with response status 500 is increased

  Scenario: Quote random endpoint gives 500 when downstream-yoda returns 400
    Given downstream response metric for YODA_SPEECH with response status 400 gets initialised
    And yoda-speech is primed to return a 400 response with body bad request u noob
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [YODASPEECH]"}
    And downstream response metric for YODA_SPEECH with response status 400 is increased

  Scenario: Quote random endpoint gives 500 when downstream-yoda cant connect
    Given downstream response metric for YODA_SPEECH with response status ReadTimeoutException gets initialised
    And yoda-speech is primed to return a successful response with fixed delay of 1950 milliseconds
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 500 is returned
    And the response body is {"errorDescription":"Dependency failure of [YODASPEECH]"}
    And downstream response metric for YODA_SPEECH with response status ReadTimeoutException is increased

  Scenario: Quote random endpoint gives 200 even when downstream-yoda is slow
    Given downstream response metric for YODA_SPEECH with response status 200 gets initialised
    And yoda-speech is primed to return a successful response with dribbled delay of 10000 milliseconds in 100 chunks
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And downstream response metric for YODA_SPEECH with response status 200 is increased

  #we need 2/4 to fail before circuit opens so we start with 2 2xx and then trigger 2 5xx,
  # in order to not care what has happened before this test execution
  @open-circuit
  Scenario: Quote random endpoint gives 500 when downstream-yoda returns 500, with circuit breaker
    Given the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 200 is returned
    When the client makes the call
    Then a response with code 200 is returned
    Given downstream response metric for YODA_SPEECH with response status ReadTimeoutException gets initialised
    And yoda-speech is primed to return a successful response with fixed delay of 1950 milliseconds
    When the client makes the call
    Then a response with code 500 is returned
    And downstream response metric for YODA_SPEECH with response status ReadTimeoutException is increased
    Given downstream response metric for YODA_SPEECH with response status ReadTimeoutException gets initialised
    When the client makes the call
    Then a response with code 500 is returned
    And downstream response metric for YODA_SPEECH with response status ReadTimeoutException is increased
    Given downstream response metric for YODA_SPEECH with response status 200 gets initialised
    When the client makes the call
    Then a response with code 200 is returned
    And the response body is {"quote":"(yoda needs to rest now) Women have one of the great acts of all time. The smart ones act very feminine and needy, but inside they are real killers. The person who came up with the expression 'the weaker sex' was either very naive or had to be kidding. I have seen women manipulate men with just a twitch of their eye -- or perhaps another body part."}
    And downstream response metric for YODA_SPEECH with response status 200 is increased
