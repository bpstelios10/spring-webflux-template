Feature: Healthcheck endpoint

  Scenario: Service status endpoint is OK
    When the client intends to call healthcheck status endpoint
    Then a success response is returned
    And the response body is "OK"