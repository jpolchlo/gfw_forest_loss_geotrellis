package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class Logging(gridTile: GridTile) extends BooleanLayer with OptionalILayer {
  val uri: String = s"$basePath/gfw_managed_forests/v202106/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/is/geotiff/${gridTile.tileId}.tif"
}
