package json

import models.Directory
import play.api.libs.json.{Json, Writes}

trait JsonDirectory {

  implicit val directoryWrite = new Writes[Directory] {
    override def writes(o: Directory) = Json.obj(
      "id" -> o.id,
      "hash" -> o.hash,
      "path" -> o.path,
      "name" -> o.name
    )
  }

}
