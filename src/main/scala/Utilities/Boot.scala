package Utilities

import Actors.EmployeeActor
import Controllers.EmployeeController
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import rest.EmployeeRest

import scala.concurrent.ExecutionContext.Implicits.global

trait EmployeeRestService extends RouteConcatenation with CORSSupport with EmployeeActor {
  override val contextRoot: String = "training"
  val routes: Route = employeeRoutes

  def employeeRoutes = {
    val availableRoutes = new EmployeeRest(employeeActor, EmployeeController).routes /*~new BookRest().routes*/
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