package Entities

import Utilities.{BaseEntity, BaseTable}
import slick.lifted.{Rep, TableQuery, Tag}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape.proveShapeOf

/**
  * Created by aamir on 9/3/17.
  */
class EmployeeTable(_tableTag: Tag) extends BaseTable[Employee](_tableTag, Some("learning"), "Employee") {

  def * = (id, firstName, isDeleted) <> (Employee.tupled, Employee.unapply)

  def ? = (Rep.Some(id), Rep.Some(firstName),Rep.Some(isDeleted)).shaped.<>({ r =>  import r._; _1.map(_ => Employee.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  override val id: Rep[Long] = column[Long]("EmployeeId", O.AutoInc, O.PrimaryKey)
  val firstName: Rep[String] = column[String]("FirstName")
  override val isDeleted: Rep[Boolean] = column[Boolean]("IsDeleted")
  lazy val employeeTable = new TableQuery(tag => new EmployeeTable(tag))
}
case class Employee(id: Long, firstName: String, isDeleted: Boolean) extends BaseEntity
