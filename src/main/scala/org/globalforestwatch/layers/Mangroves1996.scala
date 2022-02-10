package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class Mangroves1996(gridTile: GridTile) extends BooleanLayer with OptionalILayer {
  val uri: String =
    s"$basePath/gmw_global_mangrove_extent_1996/v20180701/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/is/geotiff/${gridTile.tileId}.tif"
}
