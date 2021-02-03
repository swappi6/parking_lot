package parking_lot.models

sealed trait CommandType {
  def asString: String
  final override def toString: String = asString
}

object CommandType {
  case object CreateParkingLot extends CommandType {
    override def asString: String = "create_parking_lot"
  }
  case object Park extends CommandType {
    override def asString: String = "park"
  }
  case object Leave extends CommandType {
    override def asString: String = "leave"
  }
  case object Status extends CommandType {
    override def asString: String = "status"
  }
  case object RegistrationNumOfCarsWithColor extends CommandType {
    override def asString: String = "registration_numbers_for_cars_with_colour"
  }
  case object SlotNumOfCarWithRegistrationNum extends CommandType {
    override def asString: String = "slot_number_for_car_with_registration_number"
  }
  case object SlotNumsOfCarsWithColor extends CommandType {
    override def asString: String = "slot_numbers_for_cars_with_colour"
  }

  val all: Set[CommandType] = Set(CreateParkingLot, Park, Leave, Status, RegistrationNumOfCarsWithColor,
    SlotNumOfCarWithRegistrationNum, SlotNumsOfCarsWithColor)

  def fromStringOpt(asString: String): Option[CommandType] = all.find(_.asString == asString)
}
