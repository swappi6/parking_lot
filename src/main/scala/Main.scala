import parking_lot.{CommandLineInterceptor, FileProcessor, ParkingLotService, ParkingLotServiceImpl}

import scala.io.StdIn._
import scala.util.Try

object Main extends CommandLineInterceptor with FileProcessor {
  def main(args: Array[String]): Unit = {
    Try(args(0)).toOption match {
      case Some(inputType) =>
        inputType match {
          case "command" =>
            var shouldExecute = true
            while(shouldExecute) {
              val command = readLine()
              command match {
                case "exit" =>
                  parkingLotService.doCleanup()
                  shouldExecute = false
                case other => CommandProcessor(other).print()
              }
            }
          case "file" =>
            Try(args(1)).toOption match {
              case Some(fileName) =>
                WithFileContent(fileName) { fileContent =>
                  fileContent.foreach(CommandProcessor(_).print())
                }
                parkingLotService.doCleanup()
              case None => println("Sorry, Please enter a valid file name")
            }
          case unknown => println("Sorry, Please enter a valid input type: command/file")
        }
      case None => println("Sorry, Please enter the input type: command/file")
    }
  }

  override def parkingLotService: ParkingLotService = new ParkingLotServiceImpl()
}
