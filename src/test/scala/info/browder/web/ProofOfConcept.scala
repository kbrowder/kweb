package info.browder.web

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
import org.apache.camel.Exchange
import akka.actor.ActorRef

class BasicEndpoint(transformer: ActorRef) extends Consumer {
  implicit val timeout = Timeout(25 seconds)
  def endpointUri = "jetty:http://localhost:8888/example?matchOnUriPrefix=true"
  def receive = {
    case msg => {
      transformer forward msg
    }
  }
}

class ScalateTransform extends Producer {
  def endpointUri = "scalate:" + this.getClass().getResource("template.jade")
  
}

class BasicProducer extends Producer {
  def endpointUri = "http://localhost:8888/example/bar"
}

class ProofOfConcept extends FeatureSpec with ShouldMatchers {
  feature("akka-camel works") {
    scenario("routebox works")(pending)
    scenario("http works") {
      implicit val testSystem = ActorSystem("testsystem")
      this.getClass().getResource("template.jade") should not be (null)

      val transformer = testSystem.actorOf(Props[ScalateTransform])
      val endpoint = testSystem.actorOf(Props(new BasicEndpoint(transformer)))
      val producer = testSystem.actorOf(Props[BasicProducer])

      implicit val timeout = Timeout(25 seconds)
      import testSystem.dispatcher

      val message = new CamelMessage("Hello World", Map[String, Any]())

      val res = (producer ? "hi").mapTo[CamelMessage]
      Await.result(res, timeout.duration)

      res.value should not be (None)
      res.value.get should be('success)
      val returnedMessage = res.value.get.get
      
      implicit val camelContext = CamelExtension(testSystem).context
      
      returnedMessage.headers should contain key ("CamelHttpResponseCode")
      returnedMessage.headers.get("CamelHttpResponseCode") should be(Some(200))
      
      println(returnedMessage.bodyAs[String])
      //val xmlRes = xml.XML.load(returnedMessage.bodyAs[String])
      //(xmlRes \\ "title").text should be ("hi world")
      

    }
  }
}