package Service

import Actors.BaseActor
import Entities.Employee
import Utilities.EmployeeRepository

class EmployeeService(repository: EmployeeRepository) extends BaseActor {
  receiver {
    case e: Employee => repository.insertItem(e)
  }
}
