package org.globalforestwatch.features

import geotrellis.vector.Geometry
import geotrellis.vector.io.wkb.WKB
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.globalforestwatch.util.GeometryReducer
import org.globalforestwatch.util.Util._

object SimpleFeature extends Feature {

  val idPos = 0
  val geomPos = 1

  def getFeature(
                  i: Row
                ): geotrellis.vector.Feature[Geometry, SimpleFeatureId] = {
    val feature_id: Int = i.getString(idPos).toInt
    val geom: Geometry =
      GeometryReducer.reduce(GeometryReducer.gpr)(
        WKB.read(i.getString(geomPos))
      )
    geotrellis.vector.Feature(geom, SimpleFeatureId(feature_id))
  }

  def filter(filters: Map[String, Any])(df: DataFrame): DataFrame = {

    val spark: SparkSession = df.sparkSession
    import spark.implicits._

    var newDF = df

    val idStart: Option[Int] = getAnyMapValue(filters, "idStart")
    val idEnd: Option[Int] = getAnyMapValue(filters, "idEnd")
    val limit: Option[Int] = getAnyMapValue(filters, "limit")

    idStart.foreach { startId =>
      newDF = newDF.filter($"fid" >= startId)
    }

    idEnd.foreach { endId =>
      newDF = newDF.filter($"fid" < endId)
    }

    limit.foreach { n =>
      newDF = newDF.limit(n)
    }

    newDF
  }
}
