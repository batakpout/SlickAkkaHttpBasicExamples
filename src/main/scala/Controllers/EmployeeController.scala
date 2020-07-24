package Controllers

import Entities.Employee
import akka.actor.ActorRef
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import repositories.ImplEmployeeRepository

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


abstract class EmployeeControllerComponent {
  def insertEmployeeController(data: String): Future[Employee]

  def getAllEmployees(): Future[Seq[Employee]]
}

object EmployeeController extends EmployeeControllerComponent {

  implicit val f = DefaultFormats

  def insertEmployeeController(data: String): Future[Employee] = {
    val employeeTry: Try[Employee] = Try(parse(data).extract[Employee])
    employeeTry match {
      case Success(s) => {
        ImplEmployeeRepository.insertItem(s)
      }
      case Failure(f) => Future.failed(InvalidInputException(ErrorCodes.INVALID_INPUT_EXCEPTION, message = "some msg", exception = new Exception(f.getCause)))
    }
  }

  def getAllEmployees() = {
    ImplEmployeeRepository.getEmployees
  }

}

trait MyException extends Exception

case class InvalidInputException(errorCode: String = ErrorCodes.INVALID_INPUT_EXCEPTION, message: String = ErrorMessages.INVALID_INPUT, exception: Throwable) extends MyException

object ErrorCodes {
  val INVALID_INPUT_EXCEPTION: String = "1010"
}

object ErrorMessages {
  val INVALID_INPUT: String = "Invalid Input"
}