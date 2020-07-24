package repositories

import Entities.Employee

import scala.concurrent.Future

abstract class EmployeeRepository {

  def insertItem(row: Employee): Future[Employee] = {
    Future.successful(row)
  }

  def getEmployees = {
    //super.getAll
    Future.successful(Seq(Employee(1, "obaid", false)))
  }

  /*def deleteRecord(id: Long) = {
    super.deleteById(id)
  }

  def updateEmployee(id: Long, emp: Employee): Future[Int] = {
    super.updateById(id, emp)
  }

  def getEmployeeByName(name: String): Future[Seq[Employee]] = {
    for {
      employees <- super.getAll
      result = employees.filter(_.firstName.equalsIgnoreCase(name))
    } yield result
  }*/

}

object ImplEmployeeRepository extends EmployeeRepository
