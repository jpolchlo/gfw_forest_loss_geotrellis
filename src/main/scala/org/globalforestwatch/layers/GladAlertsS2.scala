package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridId.toGladGridId
import org.globalforestwatch.grids.GridTile

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class GladAlertsS2(gridTile: GridTile, kwargs: Map[String, Any])
  extends DateConfLayer
    with OptionalILayer {

  val datasetName = "umd_glad_sentinel2_alerts"
  val uri: String =
    uriForGrid(gridTile)
}
