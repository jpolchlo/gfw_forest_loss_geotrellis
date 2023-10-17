package org.globalforestwatch.summarystats.treecoverloss

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.globalforestwatch.features.{
  FeatureId,
  GadmFeatureId,
  SimpleFeatureId,
  WdpaFeatureId
}

case class TreeLossDFFactory(
  featureType: String,
  summaryRDD: RDD[(FeatureId, TreeLossSummary)],
  spark: SparkSession
) {

  import spark.implicits._

  def getDataFrame: DataFrame = {
    featureType match {
      case "gadm"    => getGadmDataFrame
      case "feature" => getFeatureDataFrame
      case "wdpa"    => getWdpaDataFrame
      case _ =>
        throw new IllegalArgumentException("Not a valid FeatureId")
    }
  }

  private def getGadmDataFrame: DataFrame = {
    summaryRDD
      .flatMap {
        case (id, summary) =>
          summary.stats.map {
            case (dataGroup, data) => {
              id match {
                case gadmId: GadmFeatureId =>
                  TreeLossRowGadm(gadmId, dataGroup, data)
                case _ =>
                  throw new IllegalArgumentException("Not a GadmFeatureId")
              }
            }
          }
      }
      .toDF("id", "data_group", "data")
  }
  private def getFeatureDataFrame: DataFrame = {
    summaryRDD
      .flatMap {
        case (id, gladAlertSummary) =>
          gladAlertSummary.stats.map {
            case (dataGroup, data) => {
              id match {
                case simpleId: SimpleFeatureId =>
                  TreeLossRowSimple(simpleId, dataGroup, data)
                case _ =>
                  throw new IllegalArgumentException("Not a SimpleFeatureId")
              }
            }
          }
      }
      .toDF("id", "data_group", "data")
  }
  private def getWdpaDataFrame: DataFrame = {
    summaryRDD
      .flatMap {
        case (id, gladAlertSummary) =>
          gladAlertSummary.stats.map {
            case (dataGroup, data) => {
              id match {
                case wdpaId: WdpaFeatureId =>
                  TreeLossRowWdpa(wdpaId, dataGroup, data)
                case _ =>
                  throw new IllegalArgumentException("Not a SimpleFeatureId")
              }
            }
          }
      }
      .toDF("id", "data_group", "data")
  }
}
