Feature: Healthcheck endpoint

  Scenario: Service status endpoint is OK
    Given application response metric for PRIVATE_STATUS with response status 200 gets initialised
    And the client intends to call PRIVATE_STATUS endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And the response body is OK
    And application response metric for PRIVATE_STATUS with response status 200 is increased

  Scenario: Service returns 404 for missing endpoint
    Given application response metric for MISSING_ENDPOINT with response status 404 gets initialised
    And the client intends to call MISSING_ENDPOINT endpoint
    When the client makes the call
    Then a response with code 404 is returned
    And the response body is empty
    And application response metric for MISSING_ENDPOINT with response status 404 is increased