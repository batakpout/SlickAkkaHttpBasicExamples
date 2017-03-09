package rest

import Entities.Employee
import Utilities.ImplEmployeeRepository
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeRest extends Directives {
  implicit val system = ActorSystem.create("Test")
  implicit val materializer = ActorMaterializer()

  implicit val f = DefaultFormats
  val routes = path("employee") {
    post {
      entity(as[String]) { data =>
        complete {
          val dd = parse(data).extract[Employee]
          ImplEmployeeRepository.insertItem(dd).map { result =>
            HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, compact(Extraction.decompose(result))))
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
        }
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
}