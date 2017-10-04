package rest

import Controllers.EmployeeControllerComponent
import Entities.Employee
import Utilities.ImplEmployeeRepository
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeRest(actorRef: ActorRef, controller: EmployeeControllerComponent) extends Directives {
  implicit val system = ActorSystem.create("Test")
  implicit val materializer = ActorMaterializer()

  implicit val f = DefaultFormats
  val routes = path("employee") {
    post {
      entity(as[String]) { data =>
        complete {
          controller.insertEmployeeController(actorRef, data).map { result =>
            result map { finalResult =>
              HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(finalResult))))
            }
          }
        }
      }
    } ~ get {
      complete {
        ImplEmployeeRepository.getAll.map { result =>
          HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
        }
      }

    }
  } ~ path("employee" / "employeeId" / LongNumber) { id =>
    delete {
      complete {
        ImplEmployeeRepository.deleteRecord(id).map { result =>
          HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
        }
      }
    } ~ put {
      entity(as[String]) { data =>
        complete {
          val dd = parse(data).extract[Employee]
          ImplEmployeeRepository.updateEmployee(id, dd).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          }
        }/*recover {
            case ex => val (statusCode, message) = handleErrorMessages(ex)
              if (statusCode == StatusCodes.NoContent)
                HttpResponse(status = statusCode)
              else
                HttpResponse(status = statusCode, entity = HttpEntity(MediaTypes.`application/json`, message.asJson))
          }*/
      }
    }
  } ~ pathPrefix("employeeByName") {
    path(Rest) { name =>
      get {
        complete {
          ImplEmployeeRepository.getEmployeeByName(name).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
          }

        }
      }
    }
  }
  
  /*
   def handleErrorMessages(ex: Throwable) = {
    ex.printStackTrace()

    ex match {
      case cmd: DuplicateEntityException => {
        //logger.error("Exception occurred. " + stackTraceAsString(cmd.exception))
         (StatusCodes.Conflict, ErrorMessageContainer(cmd.message))
      }
  
  }}
  case class DuplicateNameException(errorCode:String = ErrorCodes.DUPLICATE_NAME, message:String = ErrorMessages.DUPLICATE_NAME, exception:Throwable) extends ReactoreException
object ErrorCodes {
  val DUPLICATE_NAME:String = "1000"
  }
  object ErrorMessages {
   val DUPLICATE_NAME:String = "Duplicate Name"
   }
   case class ErrorMessageContainer(message: String, ex: Option[String] = None, code: String = "")

  */
}
