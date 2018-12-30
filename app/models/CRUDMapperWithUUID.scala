package models

import java.util.UUID

import skinny.orm.SkinnyCRUDMapperWithId

trait CRUDMapperWithUUID[Entity] extends SkinnyCRUDMapperWithId[UUID, Entity] {
  override def useExternalIdGenerator: Boolean = true

  override def generateId: UUID = UUID.randomUUID()

  override def idToRawValue(id: UUID): UUID = id

  override def rawValueToId(value: Any): UUID = UUID.fromString(value.toString)
}
