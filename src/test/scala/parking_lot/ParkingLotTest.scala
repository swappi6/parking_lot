package parking_lot

import org.scalatest.FunSuite
import parking_lot.models.ParkingLotResponseDTOs.ParkingLotStatus
import parking_lot.models.{FailureResponse, SuccessResponse}

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

    assert(CommandProcessor("status") match {
      case p: SuccessResponse[ParkingLotStatus @unchecked] => p.res.occupiedParkingSlots.size == 4
      case _: FailureResponse[_] => false
    })
  }

  override val parkingLotService: ParkingLotService = new ParkingLotServiceImpl()
}
