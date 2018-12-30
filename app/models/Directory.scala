package models

import java.util.UUID

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class Directory(id: Long, hash: String, path: String, name: String)

object Directory extends SkinnyCRUDMapper[Directory] {
  override lazy val defaultAlias = createAlias("directory")
  private[this] lazy val art = defaultAlias

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Directory]) = new Directory(
    rs.long(n.id),
    rs.string(n.hash),
    rs.string(n.path),
    rs.string(n.name)
  )

  def create(dir: Directory): Long = {
    createWithAttributes(
      'hash -> dir.hash,
      'path -> dir.path,
      'name -> dir.name
    )
  }

  def findByHash(sha1: String): Option[Directory] = {
    findBy(sqls.eq(art.hash, sha1))
  }

}