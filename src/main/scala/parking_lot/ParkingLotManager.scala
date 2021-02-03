package parking_lot

import parking_lot.models.ErrorLike.{ParkingLotAlreadyCreatedError, ParkingLotNotCreated}
import parking_lot.models.ParkingLotResponseDTOs.ParkingLotCreationSuccess
import parking_lot.models._

object ParkingLotManager {
  private var parkingLotOpt: Option[ParkingLot] = None

  def apply(slots: Int): ResponseLike[ParkingLotCreationSuccess] = synchronized {
    parkingLotOpt match {
      case Some(_) =>
        FailureResponse(ParkingLotAlreadyCreatedError)
      case None =>
        val newParkingLot = new ParkingLot(slots)
        parkingLotOpt = Some(newParkingLot)
        SuccessResponse(ParkingLotCreationSuccess(newParkingLot))
    }
  }

  def getParkingLot: Either[ErrorLike, ParkingLot] = parkingLotOpt match {
    case Some(parkingLot) => Right(parkingLot)
    case None => Left(ParkingLotNotCreated)
  }


  def destroy(): Unit = synchronized {
    parkingLotOpt = None
  }
}
