package framework.templates.springbootwebflux.nft.config

import com.typesafe.config.{Config, ConfigFactory}

object NftConfig {

  protected[this] val config: Config = {
    ConfigFactory.load("nft")
  }

  case class NftSimulationConfig(endpointTps: EndpointTps,
                                 rampUpTimeSec: Int,
                                 statusEndpointResponseTime: Int,
                                 randomQuotesEndpointResponseTime: Int,
                                 successfulRequestPercentage: Double
                                )

  def targetEnvironment: String = {
    sys.props.getOrElse("targetEnvironment", "local")
  }

  def appUrl: String = {
    config.getConfig(targetEnvironment).getString("appUrl")
  }

  def testDuration: Int = {
    sys.props.getOrElse("testDuration", "10").toInt
  }

  def nftSimulationConfig: NftSimulationConfig = {
    val nftSimulationConfigPrefix = "nftSimulationConfig."
    val nftSimConfig = config.getConfig(nftSimulationConfigPrefix + sys.props.getOrElse("testType", "defaultSimulation"))
    NftSimulationConfig(
      endpointTps(nftSimulationConfigPrefix + nftSimConfig.getString("endpointTps")),
      nftSimConfig.getInt("rampUpTimeSec"),
      nftSimConfig.getInt("statusEndpointResponseTime"),
      nftSimConfig.getInt("randomQuotesEndpointResponseTime"),
      nftSimConfig.getDouble("successfulRequestPercentage")
    )
  }

  def endpointTps(tpsConfig: String): EndpointTps = {
    val endpointTpsConfig = config.getConfig(tpsConfig)

    EndpointTps(
      endpointTpsConfig.getInt("statusEndpointTps"),
      endpointTpsConfig.getInt("randomQuotesEndpointTps")
    )
  }

  case class EndpointTps(status: Int, randomQuotes: Int)
}
