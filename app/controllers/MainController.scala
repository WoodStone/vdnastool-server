package controllers

import javax.inject._
import json.JsonFile
import models.{Directory, File}
import play.api.Environment
import play.api.libs.Codecs
import play.api.libs.json._
import play.api.mvc._
import play.filters.headers.SecurityHeadersFilter

@Singleton
class MainController @Inject()(cc: ControllerComponents, env: Environment) extends AbstractController(cc) with JsonFile {
  private[this] val JsonContentType = "application/json; charset=utf-8"

  def initDb = {
    env.resourceAsStream("walk-output2.json").map(Json.parse(_).as[JsObject]) match {
      case Some(json) => {
        File.deleteAll()
        Directory.deleteAll()

        json.keys.foreach(key => {
          Directory.createWithAttributes(
            'hash -> Codecs.sha1(key),
            'path -> key,
            'name -> key
          )
        })

        json.keys.foreach(key => {
          (json \ key \ "files").validate[List[PreFile]] match {
            case s: JsSuccess[List[PreFile]] => {
              val fileList = s.get

              val dirId: Long = Directory.findByHash(Codecs.sha1(key)).get.id

              fileList.foreach(file => {
                val hashName = Codecs.sha1(file.name)
                val hashMtime = Codecs.sha1(file.mtime.toString)
                val hashCombined = Codecs.sha1(hashName + hashMtime)

                File.createWithAttributes(
                  'name -> file.name,
                  'mtime -> file.mtime,
                  'hashName -> hashName,
                  'hashMtime -> hashMtime,
                  'hashCombined -> hashCombined,
                  'md5 -> file.md5,
                  'directoryId -> dirId
                )
              })
            }
            case e: JsError => {

            }
          }
        })


      }
      case None => {

      }
    }
  }

  case class FileDir(id: String, hash: String, filename: String, amount: Int, dirs: List[Option[Directory]])

  implicit val writeFileDir = new Writes[FileDir] {
    override def writes(o: FileDir) = Json.obj(
      "id" -> o.id,
      "hash" -> o.hash,
      "filename" -> o.filename,
      "amount" -> o.amount,
      "dirs" -> o.dirs
    )
  }

  def duplicates = Action {
    Ok(Json.prettyPrint(Json.toJson(
      File.findDuplicates()
        .groupBy(f => f.hashCombined)
        .map { case (s, l) => FileDir(s, s, l.head.name, l.length, l.map(_.directory))}.take(20)
    ))).withHeaders(SecurityHeadersFilter.CONTENT_SECURITY_POLICY_HEADER -> "style-src 'unsafe-inline'")
  }

  case class SameFile(id: String, hash: String, filename: String, amount: Int)

  implicit val writeSameFile = new Writes[SameFile] {
    override def writes(o: SameFile) = Json.obj(
      "id" -> o.id,
      "hash" -> o.hash,
      "filename" -> o.filename,
      "amount" -> o.amount
    )
  }

  def point(results: Int, page: Int, sortField: Option[String], sortOrder: Option[String]) = Action {
    val duplicates: Map[String, List[File]] = File.findDuplicates().groupBy(_.md5)
    val n = duplicates.size

    val formattedSlice: Vector[SameFile] = duplicates.map { case (s, l) => SameFile(s, s, l.head.name, l.length)}.toVector

    def response(total: Int, data: Vector[SameFile]) = Json.obj(
      "total" -> total,
      "data" -> Json.toJson(data)
    )

    def pSlice(formatted: Vector[SameFile], results: Int, page: Int): Vector[SameFile] = formatted.slice(results * (page-1), results * page)

    def sField(slice: Vector[SameFile], sortField: Option[String]): Vector[SameFile] = {
      sortField match {
        case Some(field) => {
          field match {
            case "amount" => slice.sortBy(_.amount)
            case "filename" => slice.sortBy(_.filename)
          }
        }
        case None => slice
      }
    }

    def sOrder(sorted: Vector[SameFile], sortOrder: Option[String]): Vector[SameFile] = {
      sortOrder match {
        case Some(order) => {
          order match {
            case "ascend" => sorted
            case "descend" => sorted.reverse
          }
        }
        case None => sorted
      }
    }

    Ok(response(n, pSlice(sOrder(sField(formattedSlice, sortField), sortOrder), results, page)))
  }

  def hashDir(id: String) = Action {
    Ok(Json.toJson(
      File.findByHashWithDirs(id)
        .map(_.directory)
    ))
  }

}
