package framework.templates.springbootwebflux.nft.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

object Chains {

  val listOf200Only = List(200)

  val runIdFixedFeeder: Iterator[Map[String, String]] = Iterator.continually(Map("runId" -> "f81d4fae-7dec-11d0-a765-00a0c91e6bf6"))

  val statusEndpoint: ChainBuilder = feed(runIdFixedFeeder).exec(
    HttpRequests.statusEndpoint(listOf200Only, "#{runId}")
  )
    .doIf(_.isFailed) {
      exec { session =>
        println("***Failure on check status endpoint:")
        print("Gatling Session Data: ")
        println(session("sBodyString"))
        session
      }
    }

  val randomQuotesEndpoint: ChainBuilder = feed(runIdFixedFeeder).exec(
    HttpRequests.randomQuotesEndpoint(listOf200Only, "#{runId}")
  )
    .doIf(_.isFailed) {
      exec { session =>
        println("***Failure on check status endpoint:")
        print("Gatling Session Data: ")
        println(session("sBodyString"))
        session
      }
    }
}
