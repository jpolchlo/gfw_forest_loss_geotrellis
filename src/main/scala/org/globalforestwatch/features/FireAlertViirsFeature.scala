package org.globalforestwatch.features

import geotrellis.vector
import geotrellis.vector.Geometry
import org.apache.spark.sql.Row
import org.globalforestwatch.util.GeotrellisGeometryValidator.makeValidGeom

object FireAlertViirsFeature extends Feature {
  override val geomPos: Int = 0

  val featureCount = 8
  val featureIdExpr =
    "latitude as lat, longitude as lon, acq_date as acqDate, acq_time as acqTime, confidence, " +
      "bright_ti4 as brightTi4, bright_ti5 as brightTi5, frp"

  override def isNonEmptyGeom(i: Row): Boolean = {
    val lon = i.getString(geomPos + 1).toDouble
    val lat = i.getString(geomPos).toDouble

    lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180
  }

  override def get(i: Row): vector.Feature[Geometry, FeatureId] = {
    val featureId = getFeatureId(i)
    val lon = i.getString(geomPos + 1).toDouble
    val lat = i.getString(geomPos).toDouble

    // if lat or lon is whole number, it might be on border of geotrellis grid cell,
    // so push it slightly to south or east cell (since cells are inclusive on northwest)
    val adjustedLon =
      if (lon.toString.split("[.]")(1).size == 1)
        lon + 0.00001
      else lon

    val adjustedLat =
      if (lat.toString.split("[.]")(1).size == 1)
        lat - 0.00001
      else lat

    val geom = makeValidGeom(vector.Point(adjustedLon, adjustedLat))

    geotrellis.vector.Feature(geom, featureId)
  }

  override def getFeatureId(i: Array[String], parsed: Boolean = false): FeatureId = {
    val lat: Double = i(0).toDouble
    val lon: Double = i(1).toDouble
    val acqDate: String = i(2)
    val acqTime: Int = i(3).toInt
    val confidence: String = i(4)
    val brightTi4: Float = i(5).toFloat
    val brightTi5: Float = i(6).toFloat
    val frp: Float = i(7).toFloat

    FireAlertViirsFeatureId(lon, lat, acqDate, acqTime, confidence, brightTi4, brightTi5, frp)
  }
}
