package exceptions

trait MyException extends Exception

case class InvalidInputException(errorCode: String = ErrorCodes.INVALID_INPUT_EXCEPTION, message: String = ErrorMessages.INVALID_INPUT, exception: Throwable) extends MyException

object ErrorCodes {
  val INVALID_INPUT_EXCEPTION: String = "1010"
}

object ErrorMessages {
  val INVALID_INPUT: String = "Invalid Input"
}