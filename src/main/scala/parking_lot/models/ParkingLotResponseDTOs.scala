package parking_lot.models

object ParkingLotResponseDTOs {

  case class ParkingLotCreationSuccess(parkingLot: ParkingLot) extends ParkingLotResponseLike {
    override def successMessage: String = s"Created a parking lot with ${parkingLot.totalSlots} slots"
  }

  case class ParkingSlotAllocationSuccess(slotId: Int) extends ParkingLotResponseLike {
    override def successMessage: String = s"Allocated slot number: $slotId"
  }

  case class ParkingSlotReleaseSuccess(slotId: Int) extends ParkingLotResponseLike {
    override def successMessage: String = s"Slot number $slotId is free"
  }

  case class ParkingLotStatus(occupiedParkingSlots: Seq[OccupiedParkingSlot]) extends ParkingLotResponseLike {
    override def successMessage: String =
      "Slot No.\tRegistration No.\tColor\n" + occupiedParkingSlots.map { o =>
        s"${o.slotId}\t\t${o.vehicle.registrationNum}\t\t${o.vehicle.color.asString}"
      }.mkString("\n")
  }

  case class ParkedVehiclesResponse(vehicles: Seq[Vehicle]) extends ParkingLotResponseLike {
    override def successMessage: String = vehicles.map(_.registrationNum).mkString(",")
  }

  case class ParkedVehicleSlotResponse(slotId: Int) extends ParkingLotResponseLike {
    override def successMessage: String = s"Slot Id: $slotId"
  }

  case class ParkedVehicleSlotsResponse(slots: Seq[Int]) extends ParkingLotResponseLike {
    override def successMessage: String = slots.mkString(",")
  }
}
