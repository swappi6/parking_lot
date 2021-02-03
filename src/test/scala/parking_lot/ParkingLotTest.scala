package parking_lot

import org.scalatest.FunSuite
import parking_lot.models.ParkingLotResponseDTOs.{ParkedVehicleSlotResponse, ParkedVehicleSlotsResponse, ParkedVehiclesResponse, ParkingLotStatus}
import parking_lot.models.{Color, FailureResponse, SuccessResponse}

class ParkingLotTest extends FunSuite with CommandLineInterceptor {

  test("The Parking Lot should be created properly with 6 slots") {
    val parkingLotCreationResponse = CommandProcessor("create_parking_lot 6")
    assert(parkingLotCreationResponse match {
      case _: SuccessResponse[_] => true
      case _: FailureResponse[_] => false
    })
  }

  test("A car should be able to park successfully") {
    val parkingAllotmentResponse = CommandProcessor("park KA-01-HH-3141 Black")
    assert(parkingAllotmentResponse match {
      case _: SuccessResponse[_] => true
      case _: FailureResponse[_] => false
    })
  }

  test("A parking slot should be able to release successfully, If the slot is occupied") {
    val parkingSlotRelease = CommandProcessor("leave 1")
    assert(parkingSlotRelease match {
      case _: SuccessResponse[_] => true
      case _: FailureResponse[_] => false
    })
  }

  test("Should throw an error on release of parking slot, If the slot is not occupied/invalid") {
    val parkingSlotRelease = CommandProcessor("leave 1")
    assert(parkingSlotRelease match {
      case _: SuccessResponse[_] => false
      case _: FailureResponse[_] => true
    })
  }

  test("Should be able to give the status of Parking Lot") {
    parkingLotService.doCleanup()
    CommandProcessor("create_parking_lot 6")
    CommandProcessor("park KA-01-HH-3141 Black")
    CommandProcessor("park KA-01-HH-3142 Blue")
    CommandProcessor("park KA-01-HH-3143 Black")
    CommandProcessor("park KA-01-HH-3144 White")

    val correctOutput = Map(
      1 -> ("KA-01-HH-3141", "Black"),
      2 -> ("KA-01-HH-3142", "Blue"),
      3 -> ("KA-01-HH-3143", "Black"),
      4 -> ("KA-01-HH-3144", "White")
    )

    assert(CommandProcessor("status") match {
      case p: SuccessResponse[ParkingLotStatus @unchecked] => p.res.occupiedParkingSlots.size == 4 &&
        p.res.occupiedParkingSlots.zipWithIndex.forall {
          case (slot, idx) => correctOutput.get(idx+1).exists {
            case (regNum, color) => slot.vehicle.registrationNum == regNum &&
              slot.vehicle.color.asString == Color.fromString(color).asString
          }
        }
      case _: FailureResponse[_] => false
    })
  }

  test("Should show registration numbers of cars with given color") {
    parkingLotService.doCleanup()
    CommandProcessor("create_parking_lot 6")
    CommandProcessor("park KA-01-HH-3141 Black")
    CommandProcessor("park KA-01-HH-3142 Blue")
    CommandProcessor("park KA-01-HH-3143 Black")
    CommandProcessor("park KA-01-HH-3144 White")

    val correctOutput = Map(1 -> "KA-01-HH-3141", 2 -> "KA-01-HH-3143")

    assert(CommandProcessor("registration_numbers_for_cars_with_colour Black") match {
      case p: SuccessResponse[ParkedVehiclesResponse @unchecked] =>
        p.res.vehicles.size == 2 & p.res.vehicles.zipWithIndex.forall {
          case (v, idx) => correctOutput.get(idx + 1).contains(v.registrationNum)
        }
      case f: FailureResponse[_] => false
    })
  }

  test("Should show slot number of cars with given registration num") {
    parkingLotService.doCleanup()
    CommandProcessor("create_parking_lot 6")
    CommandProcessor("park KA-01-HH-3141 Black")
    CommandProcessor("park KA-01-HH-3142 Blue")
    CommandProcessor("park KA-01-HH-3143 Black")
    CommandProcessor("park KA-01-HH-3144 White")

    assert(CommandProcessor("slot_number_for_car_with_registration_number KA-01-HH-3141") match {
      case p: SuccessResponse[ParkedVehicleSlotResponse @unchecked] => p.res.slotId == 1
      case _: FailureResponse[_] => false
    })
  }

  test("Should show slot numbers of cars with given color") {
    parkingLotService.doCleanup()
    CommandProcessor("create_parking_lot 6")
    CommandProcessor("park KA-01-HH-3141 Black")
    CommandProcessor("park KA-01-HH-3142 Blue")
    CommandProcessor("park KA-01-HH-3143 Black")
    CommandProcessor("park KA-01-HH-3144 White")

    val correctOutput = Map(1 -> 1, 2 -> 3)

    assert(CommandProcessor("slot_numbers_for_cars_with_colour Black") match {
      case p: SuccessResponse[ParkedVehicleSlotsResponse @unchecked] => p.res.slots.zipWithIndex.forall {
        case (slotId, idx) => correctOutput.get(idx+1).contains(slotId)
      }
      case _: FailureResponse[_] => false
    })
  }

  override val parkingLotService: ParkingLotService = new ParkingLotServiceImpl()
}
