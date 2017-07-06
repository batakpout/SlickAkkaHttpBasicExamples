package Utilities

import slick.dbio.Effect.Write
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{CanBeQueryCondition, Rep, Tag}
import slick.sql.FixedSqlAction

import scala.concurrent.Future
import scala.reflect._

trait BaseEntity {
  val id: Long
  val isDeleted: Boolean
}

abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[E](tag, schemaName, tableName) {

  val classOfEntity = classTag[E].runtimeClass

  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
  val isDeleted: Rep[Boolean] = column[Boolean]("IsDeleted", O.Default(false))
}

trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {
  def getById(id: Long): Future[Option[E]]

  def getAll: Future[Seq[E]]

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]

  def save(row: E): Future[E]

  def deleteById(id: Long): Future[Int]

  def updateById(id: Long, row: E): Future[Int]
}

trait BaseRepositoryQuery[T <: BaseTable[E], E <: BaseEntity] {

  val query: slick.jdbc.PostgresProfile.api.type#TableQuery[T]

  def getByIdQuery(id: Long): Query[T, E, Seq] = {
    query.filter(_.id === id).filter(_.isDeleted === false)
  }

  def getAllQuery: Query[T, E, Seq] = {
    query.filter(_.isDeleted === false)
  }

  def filterQuery[C <: Rep[_]](expr: T => C)(implicit wt:     CanBeQueryCondition[C]): Query[T, E, Seq] = {
    query.filter(expr).filter(_.isDeleted === false)
  }

  def saveQuery(row: E): FixedSqlAction[E, NoStream, Write] = {
    query returning query += row
  }

  def deleteByIdQuery(id: Long): FixedSqlAction[Int, NoStream, Write]={
    query.filter(_.id === id).map(_.isDeleted).update(true)
  }

  def updateByIdQuery(id: Long, row: E): FixedSqlAction[Int, NoStream, Write] = {
    query.filter(_.id === id).filter(_.isDeleted === false).update(row)
  }

}

abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity :    ClassTag](clazz: TableQuery[T])
  extends BaseRepositoryQuery[T, E] with BaseRepositoryComponent[T, E] {
  val clazzTable: TableQuery[T] = clazz
  lazy val clazzEntity = classTag[E].runtimeClass
  val query: slick.jdbc.PostgresProfile.api.type#TableQuery[T] = clazz
  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/learning"
  val password = "admin"
  val driver = "org.postgresql.Driver"


  val db = Database.forURL(url, user = user, password = password, driver = driver)

  def getAll: Future[Seq[E]] = {
    db.run(getAllQuery.result)
  }

  def getById(id: Long): Future[Option[E]] = {
    db.run(getByIdQuery(id).result.headOption)
  }

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]] = {
    db.run(filterQuery(expr).result)
  }

  def save(row: E): Future[E] = {
    println(url + ">>>>>>>>>>>>>>>>>>>>>")
    db.run(saveQuery(row))
  }

  def updateById(id: Long, row: E): Future[Int] = {
    db.run(updateByIdQuery(id, row))
  }

  def deleteById(id: Long): Future[Int] = {
    println("git diff file")
    db.run(deleteByIdQuery(id))

  }

}
