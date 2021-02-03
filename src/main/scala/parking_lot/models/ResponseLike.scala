package parking_lot.models

import parking_lot.models.ResponseType.{Failure, Success}

sealed trait ResponseLike[R <: ParkingLotResponseLike] {
  def `type`: ResponseType

  def print() = this match {
    case s: SuccessResponse[_] => println(s.res.successMessage)
    case f: FailureResponse[_] => println(f.error.displayString)
  }
}
case class SuccessResponse[R <: ParkingLotResponseLike](res: R) extends ResponseLike[R] {
  override def `type`: ResponseType = Success
}
case class FailureResponse[R <: ParkingLotResponseLike](error: ErrorLike) extends ResponseLike[R] {
  override def `type`: ResponseType = Failure
}

sealed trait ResponseType {
  def asString: String
  final override def toString: String = asString
}
object ResponseType {
  case object Success extends ResponseType {
    override def asString: String = "SUCCESS"
  }
  case object Failure extends ResponseType {
    override def asString: String = "FAILURE"
  }
}

trait ParkingLotResponseLike {
  def successMessage: String
}

sealed trait ErrorLike {
  def asString: String
  final override def toString: String = asString
  def displayString: String
}
object ErrorLike {
  case object ParkingLotFullError extends ErrorLike {
    override def asString: String = "PARKING_LOT_FULL"
    override def displayString: String = "Sorry, parking lot is full"
  }

  case object ParkingLotAlreadyCreatedError extends ErrorLike {
    override def asString: String = "PARKING_LOT_ALREADY_CREATED"
    override def displayString: String = "Sorry, Parking Lot is already created"
  }

  case object ParkingLotNotCreated extends ErrorLike {
    override def asString: String = "PARKING_LOT_NOT_CREATED"
    override def displayString: String = "Sorry, Parking Lot has not been created/setup"
  }

  case class ParkingSlotNotValid(slotId: Int) extends ErrorLike {
    override def asString: String = "PARKING_SLOT_NOT_VALID"
    override def displayString: String = s"Sorry, Parking Slot $slotId is not valid"
  }

  case class EmptyParkingSlotError(slotId: Int) extends ErrorLike {
    override def asString: String = "EMPTY_PARKING_SLOT"
    override def displayString: String = s"Sorry, Parking slot $slotId is already empty"
  }

  case class VehicleNotFound(registrationNum: String) extends ErrorLike {
    override def asString: String = "VEHICLE_NOT_FOUND"
    override def displayString: String = s"Sorry, Vehicle with registration no.$registrationNum not found"
  }

  case object EmptyCommandError extends ErrorLike {
    override def asString: String = "EMPTY_COMMAND"
    override def displayString: String = "Sorry, The command is empty"
  }

  case class UnknownCommandError(command: String) extends ErrorLike {
    override def asString: String = "UNKNOWN_COMMAND"
    override def displayString: String = s"Sorry, The command $command is invalid"
  }

  case class MissingInfoError(errorStr: String) extends ErrorLike {
    override def asString: String = "MISSING_INFO"
    override def displayString: String = s"Sorry, $errorStr"
  }
}
