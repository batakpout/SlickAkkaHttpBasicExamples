package Utilities

import Entities.{Employee, EmployeeTable}
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class EmployeeRepository
  extends BaseRepository[EmployeeTable, Employee](TableQuery[EmployeeTable]) {

  // Employee(0L, "aamir", false)

  def insertItem(row: Employee) = {
    super.save(row)
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
      result = employees.filter(_.firstName == name)
    } yield result
  }

}

object ImplEmployeeRepository extends EmployeeRepository

/*
object xx extends App {
  println("hello")

  for {
    result <- ImplEmployeeRepository.insertItem(ImplEmployeeRepository.emp)
    _ = println(result)
  } yield result
  Thread.sleep(50000)
}*/
