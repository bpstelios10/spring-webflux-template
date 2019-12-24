Feature: Healthcheck endpoint

  Scenario: Service status endpoint is OK
    Given application response metric for healthcheck status with response status 200 gets initialised
    When the client intends to call healthcheck status endpoint
    Then a success response is returned
    And the response body is "OK"
    And application response metric for healthcheck status with response status 200 is increased