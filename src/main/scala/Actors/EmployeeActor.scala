package Actors

import Service.EmployeeService
import Utilities.ImplEmployeeRepository
import akka.actor.{ActorSystem, Props}


trait EmployeeActor extends ReactoreActors with CoreActorSystem with RootSupervisorHelper {

   override implicit def system: ActorSystem = ActorSystemContainer.system

  val employeeActor = createRouters(Props(classOf[EmployeeService], ImplEmployeeRepository), availableProcessors)
}