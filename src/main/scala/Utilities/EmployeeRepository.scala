package Utilities

import Entities.{Employee, EmployeeTable}
import slick.lifted.TableQuery
abstract class EmployeeRepository
  extends  BaseRepository[EmployeeTable, Employee](TableQuery[EmployeeTable]){

 // Employee(0L, "aamir", false)

  def insertItem(row: Employee) = {
    super.save(row)
  }

  def getEmployees = {
    super.getAll
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
