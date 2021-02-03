package parking_lot.service

import com.google.inject.{ImplementedBy, Singleton}
import parking_lot.models.ErrorLike.VehicleNotFound
import parking_lot.models.ParkingLotResponseDTOs._
import parking_lot.models._

@ImplementedBy(classOf[ParkingLotServiceImpl])
trait ParkingLotService {
  def createParkingLot(totalSlots: Int): ResponseLike[ParkingLotCreationSuccess]
  def parkVehicle(vehicle: Vehicle): ResponseLike[ParkingSlotAllocationSuccess]
  def releaseParkingSlot(slotId: Int): ResponseLike[ParkingSlotReleaseSuccess]
  def getAllOccupiedSlots(): ResponseLike[ParkingLotStatus]
  def getParkedVehiclesByColor(color: Color): ResponseLike[ParkedVehiclesResponse]
  def getParkedVehicleSlotNum(registrationNum: String): ResponseLike[ParkedVehicleSlotResponse]
  def getParkingSlotsByColor(color: Color): ResponseLike[ParkedVehicleSlotsResponse]
  def doCleanup(): Unit
}

@Singleton
class ParkingLotServiceImpl extends ParkingLotService with ParkingLotHelper {

  override def createParkingLot(totalSlots: Int): ResponseLike[ParkingLotCreationSuccess] =
    ParkingLotManager(totalSlots)

  override def parkVehicle(vehicle: Vehicle): ResponseLike[ParkingSlotAllocationSuccess] =
    WithParkingLot { parkingLot =>
      parkingLot.addParkingSlot(vehicle)
    }

  override def releaseParkingSlot(slotId: Int): ResponseLike[ParkingSlotReleaseSuccess] =
    WithParkingLot { parkingLot =>
      parkingLot.releaseParkingSlot(slotId)
    }

  override def getAllOccupiedSlots(): ResponseLike[ParkingLotStatus] =
    WithParkingLot { parkingLot =>
      SuccessResponse(ParkingLotStatus(parkingLot.getOccupiedSlots))
    }

  override def getParkedVehiclesByColor(color: Color): ResponseLike[ParkedVehiclesResponse] =
    WithParkingLot { parkingLot =>
      SuccessResponse(ParkedVehiclesResponse(parkingLot.getOccupiedSlots.filter(_.vehicle.color.asString == color.asString)
        .map(_.vehicle)))
    }

  override def getParkedVehicleSlotNum(registrationNum: String): ResponseLike[ParkedVehicleSlotResponse] =
    WithParkingLot { parkingLot =>
      parkingLot.getOccupiedSlots.find(_.vehicle.registrationNum == registrationNum).map { o =>
        SuccessResponse(ParkedVehicleSlotResponse(o.slotId))
      }.getOrElse(FailureResponse(VehicleNotFound(registrationNum)))
    }

  override def getParkingSlotsByColor(color: Color): ResponseLike[ParkedVehicleSlotsResponse] =
    WithParkingLot { parkingLot =>
      SuccessResponse(ParkedVehicleSlotsResponse(parkingLot.getOccupiedSlots
        .filter(_.vehicle.color.asString == color.asString).map(_.slotId)))
    }

  override def doCleanup(): Unit = ParkingLotManager.destroy()
}


