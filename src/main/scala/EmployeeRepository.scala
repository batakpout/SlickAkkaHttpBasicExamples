import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
abstract class EmployeeRepository
  extends  BaseRepository[EmployeeTable, Employee](TableQuery[EmployeeTable]){

  def insertItem(row: Employee) = {
    super.save(row)
  }

}

object ImplEmployeeRepository extends EmployeeRepository

object TestEmp extends App {

  val emp = Employee(0L, "aamir", false)

  for {
    result <- ImplEmployeeRepository.insertItem(emp)
    _ = println(result)
  } yield result
  Thread.sleep(50000)

}