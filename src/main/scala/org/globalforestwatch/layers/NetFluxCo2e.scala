package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class NetFluxCo2e(gridTile: GridTile, model: String="standard")
  extends FloatLayer
    with OptionalFLayer {
  //      val model_suffix = if (model == "standard") "" else s"__$model"
  val model_suffix: String = if (model == "standard") "standard" else s"$model"
  val uri: String =
//    s"$basePath/gfw_net_flux_co2e$model_suffix/v20191106/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/Mg_ha-1/geotiff/${gridTile.tileId}.tif"
    s"s3://gfw-files/flux_2_1_0/net_flux_all_forest_types_all_drivers/$model_suffix/${gridTile.tileId}.tif"
//  println("Net flux model type: " + model)
//  println("Net flux model suffix: " + model_suffix)
//  println("Net flux uri: " + uri)
}
