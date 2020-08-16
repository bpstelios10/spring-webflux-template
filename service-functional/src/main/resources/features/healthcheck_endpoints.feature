Feature: Healthcheck endpoint

  Scenario: Service status endpoint is OK
    Given application response metric for PRIVATE_STATUS with response status 200 gets initialised
    And the client intends to call PRIVATE_STATUS endpoint
    When the client makes the call
    Then a response with code 200 is returned
    And the response body is OK
    And application response metric for PRIVATE_STATUS with response status 200 is increased