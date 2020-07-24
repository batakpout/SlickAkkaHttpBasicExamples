package rest

import Controllers.EmployeeControllerComponent
import Entities.Employee
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction}
import repositories.ImplEmployeeRepository

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeRest(controller: EmployeeControllerComponent) extends Directives {

  implicit val system = ActorSystem.create("Test")
  implicit val materializer = ActorMaterializer()

  implicit val f = DefaultFormats

  val routes = path("employee") {
    post {
      headerValueByName("apiKey") { token =>
        authorize(validateApiKey(token)) {
          entity(as[String]) { data =>
            complete {
              controller.insertEmployeeController(data).map { result =>
                  HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
              }
            }
          }
        }
      }
    } ~ get {
      complete {
        controller.getAllEmployees().map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
        }
      }

    }
  }
  /*~ path("employee" / "employeeId" / LongNumber) { id =>
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
         } /*recover {
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
   }*/

  def validateApiKey(apiKey: String): Boolean = {
    val apiKeysJson = ConfigFactory.load().getString("apiKeys").trim
    //add other validations here
    true
  }

  case class ErrorMessageContainer(message: String, ex: Option[String] = None, code: String = "")


}
