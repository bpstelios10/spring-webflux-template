package framework.templates.springbootwebflux.nft.scenario

import framework.templates.springbootwebflux.nft.config.NftConfig.appUrl
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, PopulationBuilder}
import io.gatling.http.Predef.http
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

object
ScenarioBuilder {

  private val httpConf: HttpProtocolBuilder = http.baseUrl(appUrl).disableFollowRedirect.shareConnections

  def simpleScenario(scnName: String, tps: Double, rampUpTimeSec: Double, DurationInSecond: Integer, requests: ChainBuilder*): PopulationBuilder = {
    scenario(scnName)
      .exec(requests)
      .inject(
        rampUsersPerSec(tps / 10) to tps during (rampUpTimeSec seconds),
        constantUsersPerSec(tps) during (DurationInSecond seconds)
      )
      .protocols(httpConf)
  }
}
