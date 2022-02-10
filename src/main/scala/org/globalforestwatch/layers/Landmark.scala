package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class Landmark(gridTile: GridTile) extends BooleanLayer with OptionalILayer {
  val uri: String = s"$basePath/landmark_indigenous_and_community_lands/v20201215/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/is/geotiff/${gridTile.tileId}.tif"
}
