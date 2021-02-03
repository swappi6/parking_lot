package parking_lot

import parking_lot.models.CommandType._
import parking_lot.models.ErrorLike.{EmptyCommandError, MissingInfoError, UnknownCommandError}
import parking_lot.models._
import parking_lot.service.ParkingLotService

import scala.util.Try

trait CommandLineInterceptor {

  def parkingLotService: ParkingLotService

  object CommandProcessor {
    def apply(command: String): ResponseLike[_] = {
      val cmdArray = command.split(" ")
      if (cmdArray.isEmpty) {
        FailureResponse(EmptyCommandError)
      } else {
        CommandType.fromStringOpt(cmdArray(0)) match {
          case Some(cmdType) => cmdType match {
            case CreateParkingLot =>
              Try(cmdArray(1).toInt).toOption match {
                case Some(totalSlots) => parkingLotService.createParkingLot(totalSlots)
                case None => FailureResponse(MissingInfoError("Number of Parking Slots are either missing/invalid"))
              }

            case Park =>
              Try((cmdArray(1), cmdArray(2))).toOption match {
                case Some((registrationNum, color)) =>
                  parkingLotService.parkVehicle(Vehicle(registrationNum, Color.fromString(color)))
                case None => FailureResponse(MissingInfoError("Either Color or/and registration num is missing"))
              }

            case Leave =>
              Try(cmdArray(1).toInt).toOption match {
                case Some(slotId) => parkingLotService.releaseParkingSlot(slotId)
                case None => FailureResponse(MissingInfoError("Slot Id is missing/invalid"))
              }

            case Status => parkingLotService.getAllOccupiedSlots()

            case RegistrationNumOfCarsWithColor =>
              Try(cmdArray(1)).toOption match {
                case Some(color) => parkingLotService.getParkedVehiclesByColor(Color.fromString(color))
                case None => FailureResponse(MissingInfoError("Color is missing"))
              }

            case SlotNumOfCarWithRegistrationNum =>
              Try(cmdArray(1)).toOption match {
                case Some(regNum) => parkingLotService.getParkedVehicleSlotNum(regNum)
                case None => FailureResponse(MissingInfoError("Registration Num is missing"))
              }

            case SlotNumsOfCarsWithColor =>
              Try(cmdArray(1)).toOption match {
                case Some(color) => parkingLotService.getParkingSlotsByColor(Color.fromString(color))
                case None => FailureResponse(MissingInfoError("Color is missing"))
              }
          }
          case None => FailureResponse(UnknownCommandError(cmdArray(0)))
        }
      }
    }
  }
}
