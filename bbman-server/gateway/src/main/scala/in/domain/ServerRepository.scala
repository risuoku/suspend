package in.domain

import in.domain.Server

trait ServerRepository {
  def getServerList():List[Server]
}
