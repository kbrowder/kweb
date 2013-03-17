package net.kbserve.kblog

import scala.collection.immutable.Map
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import org.scalatest.FeatureSpec
import org.scalatest.matchers.ShouldMatchers

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.camel.CamelExtension
import akka.camel.CamelMessage
import akka.camel.Consumer
import akka.camel.Producer
import akka.pattern.ask
import akka.util.Timeout

class BasicEndpoint extends Consumer {
  implicit val timeout = Timeout(25 seconds)
  def endpointUri = "jetty:http://localhost:8888/example?matchOnUriPrefix=true"
  def receive = {
    case msg: CamelMessage => {
      sender ! ("<body><head><title>hi</title></head></body>")
    }
  }
}

class Basic2Endpoint extends Consumer {
  implicit val timeout = Timeout(25 seconds)
  def endpointUri = "jetty:http://localhost:8888/endpoint2"
  def receive = {
    case msg: CamelMessage => {
      sender ! ("<body><head><title>h2</title></head></body>")
    }
  }
}



class BasicProducer extends Producer {
  def endpointUri = "http://localhost:8888/example/bar"
}

class ProofOfConcept extends FeatureSpec with ShouldMatchers {
  feature("akka-camel works") {
    scenario("routebox works")(pending)
    scenario("http works") {
      implicit val testSystem = ActorSystem("testsystem")
      val endpoint = testSystem.actorOf(Props[BasicEndpoint])
      val producer = testSystem.actorOf(Props[BasicProducer])

      implicit val timeout = Timeout(25 seconds)
      import testSystem.dispatcher

      val message = new CamelMessage("Hello World", Map[String, Any]())
      
      val res = (producer ? "hi" ).mapTo[CamelMessage]
      Await.result(res, timeout.duration)
      println("actualRes")
      
      res.value should not be (None)
      res.value.get should be ('success)
      val returnedMessage = res.value.get.get
      returnedMessage.headers should contain key ("CamelHttpResponseCode")
      returnedMessage.headers.get("CamelHttpResponseCode") should be (Some(200))
      

    }
  }
}