package parking_lot

import java.io.File

import scala.io.Source
import scala.util.Try

trait FileProcessor {
  object WithFileContent {
    def apply[R](filePath: String)
                (onSuccess: List[String] => R): Try[R] = Try {
      val file = new File(filePath)
      onSuccess(Source.fromFile(file).getLines().toList)
    }
  }
}
