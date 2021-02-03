package parking_lot.models


import org.joda.time.DateTime
import parking_lot.models.ErrorLike._
import parking_lot.models.ParkingLotResponseDTOs.{ParkingLotCreationSuccess, ParkingSlotAllocationSuccess, ParkingSlotReleaseSuccess}

import collection._

class ParkingLot(val totalSlots: Int) {
  private val (parkingLotLayout, emptySlots): (mutable.IndexedSeq[ParkingSlotLike], mutable.TreeSet[Int]) = {
    val parkingSlots: mutable.IndexedSeq[ParkingSlotLike] = mutable.IndexedSeq.fill(totalSlots) { EmptyParkingSlot(0) }
    val emptySlots = mutable.TreeSet.empty[Int]
    for (i <- 1 to totalSlots) {
      parkingSlots(i-1) = EmptyParkingSlot(i)
      emptySlots.add(i)
    }
    (parkingSlots, emptySlots)
  }

  def addParkingSlot(vehicle: Vehicle): ResponseLike[ParkingSlotAllocationSuccess] = synchronized {
    if (emptySlots.isEmpty) {
      FailureResponse(ParkingLotFullError)
    } else {
      val firstSlot = emptySlots.firstKey
      parkingLotLayout(firstSlot-1) = OccupiedParkingSlot(firstSlot, vehicle, DateTime.now())
      emptySlots.remove(firstSlot)
      SuccessResponse(ParkingSlotAllocationSuccess(firstSlot))
    }
  }

  def releaseParkingSlot(slotId: Int): ResponseLike[ParkingSlotReleaseSuccess] = synchronized {
    if (isSlotValid(slotId)) {
      parkingLotLayout(slotId-1) match {
        case o: OccupiedParkingSlot =>
          emptySlots.add(o.slotId)
          parkingLotLayout(slotId-1) = EmptyParkingSlot(slotId)
          SuccessResponse(ParkingSlotReleaseSuccess(slotId))
        case _: EmptyParkingSlot => FailureResponse(EmptyParkingSlotError(slotId))
      }
    } else FailureResponse(ParkingSlotNotValid(slotId))
  }

  def isSlotValid(slotId: Int): Boolean = slotId >= 1 && slotId <= totalSlots

  def getOccupiedSlots: Seq[OccupiedParkingSlot] = parkingLotLayout.collect {
    case o: OccupiedParkingSlot => o
  }
}

sealed trait ParkingSlotLike {
  def slotId: Int
}
case class EmptyParkingSlot(slotId: Int) extends ParkingSlotLike
case class OccupiedParkingSlot(slotId: Int, vehicle: Vehicle, occupiedAt: DateTime) extends ParkingSlotLike

case class Vehicle(registrationNum: String, color: Color)

sealed trait Color {
  def asString: String
  final override def toString: String = asString
}
object Color {
  case object White extends Color {
    override def asString: String = "WHITE"
  }
  case object Black extends Color {
    override def asString: String = "BLACK"
  }
  case object Red extends Color {
    override def asString: String = "RED"
  }
  case object Blue extends Color {
    override def asString: String = "BLUE"
  }
  case object Yellow extends Color {
    override def asString: String = "YELLOW"
  }
  case object Green extends Color {
    override def asString: String = "GREEN"
  }
  case class UnknownColor(color: String) extends Color {
    override def asString: String = color.toUpperCase
  }

  val allDefinedColors = Set(White, Black, Red, Blue, Yellow, Green)

  def fromString(asString: String) =
    allDefinedColors.find(_.asString == asString.toUpperCase()).getOrElse(UnknownColor(asString))
}


