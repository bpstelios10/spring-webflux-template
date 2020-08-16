Feature: Quote random endpoint

  Scenario Outline: Quote random endpoint is OK for valid Accept Header
    Given application response metric for QUOTE_RANDOM with response status 200 gets initialised
    And request has header with name Accept and value <ACCEPT_HEADER_VALUE>
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And the response body is <RESPONSE_BODY>
    And the response contains header with name Content-Type and value <ACCEPT_HEADER_VALUE>
    And application response metric for QUOTE_RANDOM with response status 200 is increased
    Examples:
      | ACCEPT_HEADER_VALUE | RESPONSE_BODY          |
      | text/plain          | Temp quote             |
      | application/json    | {"quote":"Temp quote"} |

  Scenario: Quote random endpoint is OK when no Accept Header
    Given application response metric for QUOTE_RANDOM with response status 200 gets initialised
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And the response body is {"quote":"Temp quote"}
    And the response contains header with name Content-Type and value application/json
    And application response metric for QUOTE_RANDOM with response status 200 is increased

  Scenario: Quote random endpoint returns 406 for invalid Accept Header
    Given application response metric for QUOTE_RANDOM with response status 406 gets initialised
    And request has header with name Accept and value text/not-plain
    And the client intends to call QUOTE_RANDOM endpoint
    When the client makes the call
    Then a response with code 406 is returned
    And the response body is empty
    And application response metric for QUOTE_RANDOM with response status 406 is increased
