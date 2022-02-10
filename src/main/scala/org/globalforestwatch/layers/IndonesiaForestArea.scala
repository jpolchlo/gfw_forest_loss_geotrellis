package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile
import org.globalforestwatch.config.GfwConfig

case class IndonesiaForestArea(gridTile: GridTile)
  extends StringLayer
    with OptionalILayer {

  val datasetName = "idn_forest_area"
  val uri: String = uriForGrid(GfwConfig.get.rasterLayers(getClass.getSimpleName()), gridTile)

  override val externalNoDataValue: String = ""

  def lookup(value: Int): String = value match {
    case 1 => "Protected Forest"
    case 2 => "Production Forest"
    case 3 => "Limited Production Forest"
    case 4 => "Converted Production Forest"
    case 5 => "Other Utilization Area"
    case 6 => "Sanctuary Reserves/Nature Conservation Area"
    case 7 => "Marine Protected Areas"
    case _ => ""

  }
}
