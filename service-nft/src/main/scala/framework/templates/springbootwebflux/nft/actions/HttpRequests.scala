package framework.templates.springbootwebflux.nft.actions

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object HttpRequests {

  def statusEndpoint(statusCheckList: List[Int], runId: String): HttpRequestBuilder =
    http("statusEndpoint")
      .get("/private/status")
      .header("RunId", runId)
      .check(status.in(statusCheckList))
      .check(bodyString.optional.saveAs("sBodyString"))

  def randomQuotesEndpoint(statusCheckList: List[Int], runId: String): HttpRequestBuilder =
    http("randomQuotesEndpoint")
      .get("/trolltrump/quote/random")
      .header("RunId", runId)
      .check(status.in(statusCheckList))
      .check(bodyString.optional.saveAs("sBodyString"))
}
