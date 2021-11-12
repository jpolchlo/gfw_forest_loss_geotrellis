package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class SoyPlantedAreas(gridTile: GridTile, kwargs: Map[String, Any])
  extends BooleanLayer
    with OptionalILayer {

  val datasetName = "umd_soy_planted_area"
  override lazy val version = "v1" //TODO: promote v1 to latest. Need to create the correct raster tile cache assets first

  val uri: String =
    uriForGrid(gridTile)

}
