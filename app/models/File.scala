package models

import scalikejdbc._
import skinny.orm.SkinnyCRUDMapper

case class File(
                 id: Long,
                 name: String,
                 mtime: Float,
                 hashName: String,
                 hashMtime: String,
                 hashCombined: String,
                 md5: String,
                 directoryId: Long,
                 directory: Option[Directory] = None
               )

object File extends SkinnyCRUDMapper[File] {
  override lazy val defaultAlias = createAlias("file")
  private[this] lazy val art = defaultAlias

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[File]) = new File(
    rs.long(n.id),
    rs.string(n.name),
    rs.float(n.mtime),
    rs.string(n.hashName),
    rs.string(n.hashMtime),
    rs.string(n.hashCombined),
    rs.string(n.md5),
    rs.long(n.directoryId)
  )

  val dirsTo = belongsTo[Directory](Directory, (f, d) => f.copy(directory = d))

  def findByHashWithDirs(hash:String): List[File] = {
    File.joins(dirsTo).findAllBy(sqls.eq(art.md5, hash))
  }

  def sameHash(): List[String] = {
    val f = syntax("f")
    DB readOnly { implicit session =>
      withSQL {
        select(f.result.hashCombined).from(File as f).groupBy(f.resultName.hashCombined).having(sqls.gt(sqls.count, 2))
      }.map(_.string(f.resultName.hashCombined)).list.apply()
    }
  }

  def findDuplicates(): List[File] = {
    val f = syntax("f")
    val fs = SubQuery.syntax("fs").include(f)
    val fx = syntax("fx")

    DB readOnly { implicit session =>
      withSQL {
        select.all.from(File as f).innerJoin {
          select(fx.result.md5)
            .from(File as fx)
            .groupBy(fx.resultName.md5)
            .having(sqls.gt(sqls.count, 1))
            .as(fs)
        }.on(f.md5, fs(fx).md5)
//          .leftJoin(Directory as d).on(f.directoryId, d.id)
      }.map(r => {
        File(
          r.long(f.resultName.id),
          r.string(f.resultName.name),
          r.float(f.resultName.mtime),
          r.string(f.resultName.hashName),
          r.string(f.resultName.hashMtime),
          r.string(f.resultName.hashCombined),
          r.string(f.resultName.md5),
          r.long(f.resultName.directoryId)
//          Some(Directory(
//            r.long(d.resultName.id),
//            r.string(d.resultName.hash),
//            r.string(d.resultName.path),
//            r.string(d.resultName.name)
//          ))
        )
      }).list.apply()
    }
  }

}