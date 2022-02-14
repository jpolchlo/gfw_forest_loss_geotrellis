package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class GrossEmissionsCo2OnlyCo2e(gridTile: GridTile,
                                     model: String = "standard", kwargs: Map[String, Any])
  extends FloatLayer
    with OptionalFLayer {

  val datasetName = "Na"


  val model_suffix: String = if (model == "standard") "standard" else s"$model"
  val uri: String =
  //    s"$basePath/gfw_gross_emissions_co2e_co2_only$model_suffix/v20191106/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/Mg/gdal-geotiff/${gridTile.tileId}.tif"
    s"s3://gfw-files/flux_1_2_1/gross_emissions_co2_only_co2e/$model_suffix/${gridTile.tileId}.tif"
  //    s"s3://gfw-files/flux_1_2_0/gross_emissions_co2_only_co2e/$model_suffix/${gridTile.tileId}.tif"
}
