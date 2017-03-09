package Utilities

import Actors.EmployeeActor
import Controllers.EmployeeController
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import rest.EmployeeRest

import scala.concurrent.ExecutionContext.Implicits.global

trait RestEndCollection extends RouteConcatenation with CORSSupport with EmployeeActor {
  //implicit def system: ActorSystem
  override val contextRoot: String = "training"

  val allRoutes = new EmployeeRest(employeeActor, EmployeeController).routes/*~new BookRest().routes*/
  val availableRoutes= cors(allRoutes)
}

object Boot extends App with RestEndCollection {
  implicit val materializer = ActorMaterializer()

  val r = Http().bindAndHandle(availableRoutes, interface = "0.0.0.0", port = 9000)
  r.map { x => println("Successfully Bound to " + x.localAddress) }.recover { case _ => println("Failed to Bind ") }
  Thread.sleep(5000)
}