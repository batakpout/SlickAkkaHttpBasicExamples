package Utilities

import Entities.{Employee, EmployeeTable}
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class EmployeeRepository
  extends BaseRepository[EmployeeTable, Employee](TableQuery[EmployeeTable]) {

  // Employee(0L, "aamir", false)

  def insertItem(row: Employee): Future[Employee] = {
    val res = super.save(row)
    res.map(x => println(x + ">>>>>>>>>>>>>>>>>"))
    res
  }

  def getEmployees = {
    super.getAll
  }

  def deleteRecord(id: Long) = {
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
  }

}

object ImplEmployeeRepository extends EmployeeRepository
