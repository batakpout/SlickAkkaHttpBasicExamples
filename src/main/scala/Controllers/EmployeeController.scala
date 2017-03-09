package Controllers

import Actors.ActorHelper
import Entities.Employee
import akka.actor.ActorRef
import org.json4s.jackson.JsonMethods.parse
import akka.pattern.ask
import org.json4s.DefaultFormats

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

abstract class EmployeeControllerComponent {
   def insertEmployeeController(actorRef: ActorRef, data: String):Future[Employee]
}
object EmployeeController extends EmployeeControllerComponent {

  implicit val f = DefaultFormats

  def insertEmployeeController(actorRef: ActorRef, data: String):Future[Employee] = {
    val dd: Try[Employee] = Try(parse(data).extract[Employee])
     dd match {
       case Success(s) => {
         (actorRef ? s)(ActorHelper.commonOperationsTimeout).mapTo[Employee]
       }
       case Failure(f) =>
     }
    Future.successful(Employee(1l,"",false))
  }
}