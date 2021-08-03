package org.globalforestwatch.summarystats.firealerts

import com.github.mrpowers.spark.daria.sql.DataFrameHelpers.validatePresenceOfColumns
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}

object FireAlertsDF {

  val contextualLayers: List[String] = List(
    "umd_tree_cover_density__threshold",
    "is__umd_regional_primary_forest_2001",
    "is__birdlife_alliance_for_zero_extinction_site",
    "is__birdlife_key_biodiversity_area",
    "is__landmark_land_right",
    "gfw_plantation__type",
    "is__gfw_mining",
    "is__gfw_managed_forest",
    "rspo_oil_palm__certification_status",
    "is__gfw_wood_fiber",
    "is__peatland",
    "is__idn_forest_moratorium",
    "is__gfw_oil_palm",
    "idn_forest_area__type",
    "per_forest_concession__type",
    "is__gfw_oil_gas",
    "is__gmw_mangroves_2016",
    "is__ifl_intact_forest_landscape_2016",
    "bra_biome__name"
  )

  def unpackValues(unpackCols: List[Column],
                   wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    validatePresenceOfColumns(df, Seq("fireId", "featureId", "data_group", "data"))

    def defaultCols =
      List(
        $"data_group.threshold" as "umd_tree_cover_density__threshold",
        $"data_group.primaryForest" as "is__umd_regional_primary_forest_2001",
        $"data_group.aze" as "is__birdlife_alliance_for_zero_extinction_site",
        $"data_group.keyBiodiversityAreas" as "is__birdlife_key_biodiversity_area",
        $"data_group.landmark" as "is__landmark_land_right",
        $"data_group.plantations" as "gfw_plantation__type",
        $"data_group.mining" as "is__gfw_mining",
        $"data_group.logging" as "is__gfw_managed_forest",
        $"data_group.rspo" as "rspo_oil_palm__certification_status",
        $"data_group.woodFiber" as "is__gfw_wood_fiber",
        $"data_group.peatlands" as "is__peatland",
        $"data_group.indonesiaForestMoratorium" as "is__idn_forest_moratorium",
        $"data_group.oilPalm" as "is__gfw_oil_palm",
        $"data_group.indonesiaForestArea" as "idn_forest_area__type",
        $"data_group.peruForestConcessions" as "per_forest_concession__type",
        $"data_group.oilGas" as "is__gfw_oil_gas",
        $"data_group.mangroves2016" as "is__gmw_mangroves_2016",
        $"data_group.intactForestLandscapes2016" as "is__ifl_intact_forest_landscape_2016",
        $"data_group.braBiomes" as "bra_biome__name",
      )

    val cols =
      if (!wdpa)
        unpackCols ::: ($"data_group.protectedAreas" as "wdpa_protected_area__iucn_cat") :: defaultCols
      else unpackCols ::: defaultCols

    df.select(cols: _*)
  }

  def aggChangeDaily(groupByCols: List[String],
                     aggCol: String,
                     wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val confCols = if (!aggCol.equals("burned_area__ha")) List("confidence__cat")  else List()
    val fireCols = List("alert__date") ::: confCols

    val cols =
      if (!wdpa)
        groupByCols ::: fireCols ::: "wdpa_protected_area__iucn_cat" :: contextualLayers
      else
        groupByCols ::: fireCols ::: contextualLayers

    df.filter($"alert__date".isNotNull)
      .groupBy(cols.head, cols.tail: _*)
      .agg(
        sum(aggCol) as aggCol
      )
  }

  def aggChangeWeekly(cols: List[String],
                      aggCol: String,
                      wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val confCols = if (!aggCol.equals("burned_area__ha")) List($"confidence__cat")  else List()

    val fireCols = List(
      year($"alert__date") as "alert__year",
      weekofyear($"alert__date") as "alert__week",
    ) ::: confCols

    _aggChangeWeekly(df.filter($"alert__date".isNotNull), cols, fireCols, aggCol, wdpa)
  }

  def aggChangeWeekly2(cols: List[String],
                       aggCol: String,
                       wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val confCols = if (!aggCol.equals("burned_area__ha")) List($"confidence__cat")  else List()
    val fireCols = List($"alert__year", $"alert__week") ::: confCols
    _aggChangeWeekly(df, cols, fireCols, aggCol, wdpa)
  }

  private def _aggChangeWeekly(df: DataFrame,
                               cols: List[String],
                               fireCols: List[Column],
                               aggCol: String,
                               wdpa: Boolean = false): DataFrame = {
    val spark = df.sparkSession
    import spark.implicits._

    val confCols = if (!aggCol.equals("burned_area__ha")) List("confidence__cat")  else List()
    val fireCols2 = List("alert__year", "alert__week") ::: confCols
    val aggCols = List(col(aggCol))

    val contextLayers: List[String] =
      if (!wdpa) "wdpa_protected_area__iucn_cat" :: contextualLayers
      else contextualLayers

    val selectCols: List[Column] = cols.foldRight(Nil: List[Column])(
      col(_) :: _
    ) ::: fireCols ::: contextLayers
      .foldRight(Nil: List[Column])(col(_) :: _) ::: aggCols

    val groupByCols = cols ::: fireCols2 ::: contextLayers

    df.select(selectCols: _*)
      .groupBy(groupByCols.head, groupByCols.tail: _*)
      .agg(
        sum(aggCol) as aggCol
      )
  }

  def whitelist(groupByCols: List[String],
                wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val defaultAggCols = List(
      max("is__umd_regional_primary_forest_2001") as "is__umd_regional_primary_forest_2001",
      max("is__birdlife_alliance_for_zero_extinction_site") as "is__birdlife_alliance_for_zero_extinction_site",
      max("is__birdlife_key_biodiversity_area") as "is__birdlife_key_biodiversity_area",
      max("is__landmark_land_right") as "is__landmark_land_right",
      max(length($"gfw_plantation__type"))
        .cast("boolean") as "gfw_plantation__type",
      max("is__gfw_mining") as "is__gfw_mining",
      max("is__gfw_managed_forest") as "is__gfw_managed_forest",
      max(length($"rspo_oil_palm__certification_status"))
        .cast("boolean") as "rspo_oil_palm__certification_status",
      max("is__gfw_wood_fiber") as "is__gfw_wood_fiber",
      max("is__peatland") as "is__peatland",
      max("is__idn_forest_moratorium") as "is__idn_forest_moratorium",
      max("is__gfw_oil_palm") as "is__gfw_oil_palm",
      max(length($"idn_forest_area__type"))
        .cast("boolean") as "idn_forest_area__type",
      max(length($"per_forest_concession__type"))
        .cast("boolean") as "per_forest_concession__type",
      max("is__gfw_oil_gas") as "is__gfw_oil_gas",
      max("is__gmw_mangroves_2016") as "is__gmw_mangroves_2016",
      max("is__ifl_intact_forest_landscape_2016") as "is__ifl_intact_forest_landscape_2016",
      max(length($"bra_biome__name")).cast("boolean") as "bra_biome__name"
    )

    val aggCols =
      if (!wdpa)
        (max(length($"wdpa_protected_area__iucn_cat"))
          .cast("boolean") as "wdpa_protected_area__iucn_cat") :: defaultAggCols
      else defaultAggCols

    df.groupBy(groupByCols.head, groupByCols.tail: _*)
      .agg(aggCols.head, aggCols.tail: _*)
  }

  def whitelist2(groupByCols: List[String],
                 wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val defaultAggCols = List(
      max("is__umd_regional_primary_forest_2001") as "is__umd_regional_primary_forest_2001",
      max("is__birdlife_alliance_for_zero_extinction_site") as "is__birdlife_alliance_for_zero_extinction_site",
      max("is__birdlife_key_biodiversity_area") as "is__birdlife_key_biodiversity_area",
      max("is__landmark_land_right") as "is__landmark_land_right",
      max("gfw_plantation__type") as "gfw_plantation__type",
      max("is__gfw_mining") as "is__gfw_mining",
      max("is__gfw_managed_forest") as "is__gfw_managed_forest",
      max("rspo_oil_palm__certification_status") as "rspo_oil_palm__certification_status",
      max("is__gfw_wood_fiber") as "is__gfw_wood_fiber",
      max("is__peatland") as "is__peatland",
      max("is__idn_forest_moratorium") as "is__idn_forest_moratorium",
      max("is__gfw_oil_palm") as "is__gfw_oil_palm",
      max("idn_forest_area__type") as "idn_forest_area__type",
      max("per_forest_concession__type") as "per_forest_concession__type",
      max("is__gfw_oil_gas") as "is__gfw_oil_gas",
      max("is__gmw_mangroves_2016") as "is__gmw_mangroves_2016",
      max("is__ifl_intact_forest_landscape_2016") as "is__ifl_intact_forest_landscape_2016",
      max("bra_biome__name") as "bra_biome__name"
    )

    val aggCols =
      if (!wdpa)
        (max("wdpa_protected_area__iucn_cat") as "wdpa_protected_area__iucn_cat") :: defaultAggCols
      else defaultAggCols

    df.groupBy(groupByCols.head, groupByCols.tail: _*)
      .agg(aggCols.head, aggCols.tail: _*)
  }
}