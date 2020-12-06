package framework.templates.springbootwebflux.nft.simulations

import com.typesafe.scalalogging.StrictLogging
import framework.templates.springbootwebflux.nft.actions.Chains
import framework.templates.springbootwebflux.nft.config.NftConfig._
import framework.templates.springbootwebflux.nft.scenario.ScenarioBuilder
import io.gatling.core.Predef.{details, _}

class ServiceSimulation extends Simulation with StrictLogging {

  setUp(
    ScenarioBuilder.simpleScenario("statusEndpoint", tps = nftSimulationConfig.endpointTps.status,
      rampUpTimeSec = nftSimulationConfig.rampUpTimeSec, testDuration, Chains.statusEndpoint),
    ScenarioBuilder.simpleScenario("randomQuotesEndpoint", tps = nftSimulationConfig.endpointTps.randomQuotes,
      rampUpTimeSec = nftSimulationConfig.rampUpTimeSec, testDuration, Chains.randomQuotesEndpoint)
  ).assertions(
    details("statusEndpoint").successfulRequests.percent.gt(nftSimulationConfig.successfulRequestPercentage),
    details("statusEndpoint").responseTime.percentile4.lt(nftSimulationConfig.statusEndpointResponseTime),
    details("randomQuotesEndpoint").successfulRequests.percent.gt(nftSimulationConfig.successfulRequestPercentage),
    details("randomQuotesEndpoint").responseTime.percentile4.lt(nftSimulationConfig.randomQuotesEndpointResponseTime)
  )
}
