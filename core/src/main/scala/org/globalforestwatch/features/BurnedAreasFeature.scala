package org.globalforestwatch.features

object BurnedAreasFeature extends Feature {
  override val geomPos: Int = 1

  val featureIdExpr = "alert__date as alertDate"
  val featureCount = 1

  def getFeatureId(i: Array[String], parsed: Boolean = false): FeatureId = {
    val alertDate = i(0)
    BurnedAreasFeatureId(alertDate)
  }
}
