package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

import java.time.LocalDate
import org.globalforestwatch.grids.GridId.toGladGridId


case class GladAlerts(gridTile: GridTile, kwargs: Map[String, Any]) extends DateConfLayer with OptionalILayer {
  val datasetName = "umd_glad_landsat_alerts"
  val gladGrid: String = toGladGridId(gridTile.tileId)

  val uri: String =
    if (gridTile.gridSize == 10 && gridTile.rowCount == 40000) {
      s"s3://gfw2-data/forest_change/umd_landsat_alerts/prod/analysis/$gladGrid.tif"
    } else {
      uriForGrid(gridTile)
    }
}
