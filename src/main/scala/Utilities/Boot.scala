package Utilities

import Actors.{ActorSystemContainer, GeneralActors}
import Controllers.EmployeeController
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import rest.EmployeeRest

import scala.concurrent.ExecutionContext.Implicits.global

trait EmployeeRestService extends RouteConcatenation with CORSSupport with GeneralActors {
  override val contextRoot: String = "datafoundry"
  val routes: Route = allRoutes

  override implicit def system: ActorSystem = ActorSystemContainer.system

  def allRoutes: Route = {
    val availableRoutes = new EmployeeRest(EmployeeController).routes /*~new BookRest().routes*/
    availableRoutes
  }

}

trait RestEndCollection extends EmployeeRestService {
  val availableRoutes: Route = cors(routes)
}

object Boot extends App with RestEndCollection {
  implicit val materializer = ActorMaterializer()

  val r = Http().bindAndHandle(availableRoutes, interface = "0.0.0.0", port = 9000)
  r.map { x => println("Successfully Bound to " + x.localAddress) }.recover { case _ => println("Failed to Bind ") }
  Thread.sleep(5000)
}