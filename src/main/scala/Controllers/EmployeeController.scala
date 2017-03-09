package Controllers

import Actors.ActorHelper
import Entities.Employee
import akka.actor.ActorRef
import org.json4s.jackson.JsonMethods.parse
import akka.pattern.ask
import org.json4s.DefaultFormats
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

abstract class EmployeeControllerComponent {
   def insertEmployeeController(actorRef: ActorRef, data: String):Future[Future[Employee]]
}
object EmployeeController extends EmployeeControllerComponent {

  implicit val f = DefaultFormats

  def insertEmployeeController(actorRef: ActorRef, data: String): Future[Future[Employee]] = {
    val dd: Try[Employee] = Try(parse(data).extract[Employee])
     dd match {
       case Success(s) => {
         val res = (actorRef ? s)(ActorHelper.commonOperationsTimeout).mapTo[Future[Employee]]
         res.map(x => x.map(y => println(y + "======")))
         res
       }
       case Failure(f) => Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION, message = "some msg", exception = new Exception(f.getCause)))
     }
  }
}

trait MyException extends Exception

case class InvalidInputException(errorCode:String = ErrorCodes.INVALID_INPUT_EXCEPTION, message:String = ErrorMessages.INVALID_INPUT, exception:Throwable) extends MyException

object ErrorCodes {
  val DUPLICATE_NAME:String = "1000"
  val UNIQUE_KEY_VIOLATION:String = "1001"
  val FOREIGN_KEY_VIOLATION:String = "1002"
  val GENERIC_DATABASE_ERROR:String = "1003"
  val GENERIC_EXCEPTION:String = "1004"
  val NO_SUCH_ENTITY_EXCEPTION:String = "1005"
  val VALIDATION_FAILED_EXCEPTION:String = "1006"
  val AUTHENTICATION_FAILED_EXCEPTION:String = "1007"
  val AUTHORIZATION_FAILED_EXCEPTION:String = "1008"
  val DUPLICATE_ENTITY:String = "1009"
  val INVALID_INPUT_EXCEPTION:String = "1010"
  val NOT_IMPLEMENTED_EXCEPTION  ="1011"
  val DEPENDENCY_EXCEPTION:String = "1012"
  val TARGET_DEPENDENCY_EXCEPTION:String = "1013"
  val OPERATION_NOT_ALLOWED_EXCEPTION:String = "1014"

  val WORKFLOW_INITIATED:String = "2000"
  val WORKFLOW_NOT_DEPLOYED:String = "2001"
}

object ErrorMessages {
  val DATABASE_ERROR_MESSAGE:String = "An exception occurred during database operation"
  val GENERIC_ERROR_MESSAGE:String = "An unexpected error occurred"
  val ENTITY_NOT_FOUND:String = "Entity not found"
  val DUPLICATE_NAME:String = "Duplicate Name"
  val DUPLICATE_ENTITY:String = "Duplicate Entity"
  val VALIDATION_FAILED:String = "Validation failed"
  val AUTHENTICATION_FAILED:String = "Authentication Failed"
  val AUTHORIZATION_FAILED:String = "Authorization Failed"
  val INVALID_INPUT:String = "Invalid Input"
  val NOT_IMPLEMENTED ="Method is not implemented."
  val DEPENDENCY_UNAVAILABLE ="One or more dependencies are not available"
  val TARGET_DEPENDENCY_UNAVAILABLE ="Target data is not available"
  val OPERATION_NOT_ALLOWED ="Operation is not allowed"
  val UPDATE_NOT_ALLOWED ="Update not allowed for reference data"
}