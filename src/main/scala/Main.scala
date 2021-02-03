import parking_lot.service.{ParkingLotService, ParkingLotServiceImpl}
import parking_lot.{CommandLineInterceptor, FileProcessor}

import scala.io.StdIn._
import scala.util.{Failure, Success, Try}

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
                } match {
                  case Success(_) => parkingLotService.doCleanup()
                  case Failure(_) => println("Sorry, Please enter a valid file with correct absolute path")
                }
              case None => println("Sorry, Please enter a valid file name")
            }
          case _ => println("Sorry, Please enter a valid input type: command/file")
        }
      case None => println("Sorry, Please enter the input type: command/file")
    }
  }

  override val parkingLotService: ParkingLotService = new ParkingLotServiceImpl()
}
