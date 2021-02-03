package parking_lot

import parking_lot.models.{FailureResponse, ParkingLot, ParkingLotResponseLike, ResponseLike}

trait ParkingLotHelper {
  object WithParkingLot {
    def apply[R <: ParkingLotResponseLike](onSuccess: ParkingLot => ResponseLike[R]): ResponseLike[R] =
      ParkingLotManager.getParkingLot match {
        case Right(parkingLot) => onSuccess(parkingLot)
        case Left(errorLike) => FailureResponse(errorLike)
      }
  }
}
