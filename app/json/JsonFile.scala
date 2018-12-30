package json

import models.File
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

trait JsonFile extends JsonDirectory {

  case class PreFile(name: String, mtime: Float, md5: String)

  implicit val fileWrite = new Writes[File] {
    override def writes(o: File) = Json.obj(
      "id" -> o.id,
      "name" -> o.name,
      "mtime" -> o.mtime,
      "hashName" -> o.hashName,
      "hashMtime" -> o.hashMtime,
      "hashCombined" -> o.hashCombined,
      "md5" -> o.md5,
      "directoryId" -> o.directoryId,
      "directory" -> o.directory
    )
  }

  implicit val preFileRead: Reads[PreFile] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "mtime").read[Float] and
      (JsPath \ "md5").read[String]
    )(PreFile.apply _)

}
