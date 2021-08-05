package org.globalforestwatch.summarystats.annualupdate_minimal

import com.github.mrpowers.spark.daria.sql.DataFrameHelpers.validatePresenceOfColumns
import org.apache.spark.sql.functions.{length, max, sum}
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object AnnualUpdateMinimalDF {

  val contextualLayers = List(
    "umd_tree_cover_density_2000__threshold",
    "tsc_tree_cover_loss_drivers__type",
    "esa_land_cover_2015__class",
    "is__birdlife_alliance_for_zero_extinction_site",
    "gfw_plantations__type",
    "is__gmw_mangroves_1996",
    "is__gmw_mangroves_2016",
    "ifl_intact_forest_landscapes__year",
    "is__umd_regional_primary_forest_2001",
    "is__gfw_tiger_landscapes",
    "is__landmark_indigenous_and_community_lands",
    "is__gfw_land_rights",
    "is__birdlife_key_biodiversity_areas",
    "is__gfw_mining",
    "is__gfw_peatlands",
    "is__gfw_oil_palm",
    "is__idn_forest_moratorium",
    "is__gfw_wood_fiber",
    "is__gfw_resource_rights",
    "is__gfw_managed_forest",
    "is__umd_tree_cover_gain"
  )

  def unpackValues(cols: List[Column],
                   wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark: SparkSession = df.sparkSession
    import spark.implicits._

    def defaultUnpackCols =
      List(
        $"data_group.lossYear" as "umd_tree_cover_loss__year",
        $"data_group.threshold" as "umd_tree_cover_density_2000__threshold",
        $"data_group.drivers" as "tsc_tree_cover_loss_drivers__type",
        $"data_group.globalLandCover" as "esa_land_cover_2015__class",
        $"data_group.primaryForest" as "is__umd_regional_primary_forest_2001",
        $"data_group.aze" as "is__birdlife_alliance_for_zero_extinction_site",
        $"data_group.plantations" as "gfw_plantations__type",
        $"data_group.mangroves1996" as "is__gmw_mangroves_1996",
        $"data_group.mangroves2016" as "is__gmw_mangroves_2016",
        $"data_group.intactForestLandscapes" as "ifl_intact_forest_landscapes__year",
        $"data_group.tigerLandscapes" as "is__gfw_tiger_landscapes",
        $"data_group.landmark" as "is__landmark_indigenous_and_community_lands",
        $"data_group.landRights" as "is__gfw_land_rights",
        $"data_group.keyBiodiversityAreas" as "is__birdlife_key_biodiversity_areas",
        $"data_group.mining" as "is__gfw_mining",
        $"data_group.peatlands" as "is__gfw_peatlands",
        $"data_group.oilPalm" as "is__gfw_oil_palm",
        $"data_group.idnForestMoratorium" as "is__idn_forest_moratorium",
        $"data_group.woodFiber" as "is__gfw_wood_fiber",
        $"data_group.resourceRights" as "is__gfw_resource_right",
        $"data_group.logging" as "is__gfw_managed_forest",
        $"data_group.isGain" as "is__umd_tree_cover_gain",
        $"data.treecoverExtent2000" as "umd_tree_cover_density_2000__ha",
        $"data.treecoverExtent2010" as "umd_tree_cover_density_2010__ha",
        $"data.totalArea" as "area__ha",
        $"data.totalGainArea" as "umd_tree_cover_gain__ha",
        $"data.totalBiomass" as "whrc_aboveground_biomass_stock_2000__Mg",
        $"data.treecoverLoss" as "umd_tree_cover_loss__ha",
        $"data.biomassLoss" as "whrc_aboveground_biomass_loss__Mg",
        $"data.co2Emissions" as "whrc_aboveground_co2_emissions__Mg",
        $"data.totalCo2" as "whrc_aboveground_co2_stock_2000__Mg",
        $"data.totalGrossCumulAbovegroundRemovalsCo2" as "gfw_forest_carbon_gross_removals_aboveground__Mg_CO2e",
        $"data.totalGrossCumulBelowgroundRemovalsCo2" as "gfw_forest_carbon_gross_removals_belowground__Mg_CO2e",
        $"data.totalGrossCumulAboveBelowgroundRemovalsCo2" as "gfw_forest_carbon_gross_removals__Mg_CO2e",
        $"data.totalNetFluxCo2" as "gfw_forest_carbon_net_flux__Mg_CO2e",
        $"data.totalGrossEmissionsCo2eCo2Only" as "gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e",
        $"data.totalGrossEmissionsCo2eNonCo2" as "gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e",
        $"data.totalGrossEmissionsCo2e" as "gfw_forest_carbon_gross_emissions__Mg_CO2e"
      )

    val unpackCols = {
      if (!wdpa) {
        defaultUnpackCols ::: List(
          $"data_group.wdpa" as "wdpa_protected_area__iucn_cat"
        )
      } else defaultUnpackCols
    }

    validatePresenceOfColumns(df, Seq("id", "data_group", "data"))

    df.select(cols ::: unpackCols: _*)
  }

  def aggSummary(groupByCols: List[String],
                 wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val cols =
      if (!wdpa)
        groupByCols ::: contextualLayers ::: List(
          "wdpa_protected_area__iucn_cat"
        )
      else groupByCols ::: contextualLayers

    df.groupBy(cols.head, cols.tail: _*)
      .agg(
        sum("umd_tree_cover_density_2000__ha") as "umd_tree_cover_density_2000__ha",
        sum("umd_tree_cover_density_2010__ha") as "umd_tree_cover_density_2010__ha",
        sum("area__ha") as "area__ha",
        sum("umd_tree_cover_gain__ha") as "umd_tree_cover_gain__ha",
        sum("whrc_aboveground_biomass_stock_2000__Mg") as "whrc_aboveground_biomass_stock_2000__Mg",
        sum("whrc_aboveground_co2_stock_2000__Mg") as "whrc_aboveground_co2_stock_2000__Mg",
        sum("umd_tree_cover_loss__ha") as "umd_tree_cover_loss__ha",
        sum("whrc_aboveground_biomass_loss__Mg") as "whrc_aboveground_biomass_loss__Mg",
        sum("gfw_forest_carbon_gross_removals_aboveground__Mg_CO2e") as "gfw_forest_carbon_gross_removals_aboveground__Mg_CO2e",
        sum("gfw_forest_carbon_gross_removals_belowground__Mg_CO2e") as "gfw_forest_carbon_gross_removals_belowground__Mg_CO2e",
        sum("gfw_forest_carbon_gross_removals__Mg_CO2e") as "gfw_forest_carbon_gross_removals__Mg_CO2e",
        sum("gfw_forest_carbon_net_flux__Mg_CO2e") as "gfw_forest_carbon_net_flux__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions__Mg_CO2e") as "gfw_forest_carbon_gross_emissions__Mg_CO2e",
      )
  }

  def aggSummary2(groupByCols: List[String],
                  wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val cols =
      if (!wdpa)
        groupByCols ::: contextualLayers ::: List(
          "wdpa_protected_area__iucn_cat"
        )
      else groupByCols ::: contextualLayers

    df.groupBy(cols.head, cols.tail: _*)
      .agg(
        sum("umd_tree_cover_density_2000__ha") as "umd_tree_cover_density_2000__ha",
        sum("umd_tree_cover_density_2010__ha") as "umd_tree_cover_density_2010__ha",
        sum("area__ha") as "area__ha",
        sum("umd_tree_cover_gain__ha") as "umd_tree_cover_gain__ha",
        sum("whrc_aboveground_biomass_stock_2000__Mg") as "whrc_aboveground_biomass_stock_2000__Mg",
        sum("whrc_aboveground_co2_stock_2000__Mg") as "whrc_aboveground_co2_stock_2000__Mg",
        sum("umd_tree_cover_loss__ha") as "umd_tree_cover_loss__ha",
        sum("whrc_aboveground_biomass_loss__Mg") as "whrc_aboveground_biomass_loss__Mg",
        sum("gfw_forest_carbon_gross_removals_aboveground__Mg_CO2e") as "gfw_forest_carbon_gross_removals_aboveground__Mg_CO2e",
        sum("gfw_forest_carbon_gross_removals_belowground__Mg_CO2e") as "gfw_forest_carbon_gross_removals_belowground__Mg_CO2e",
        sum("gfw_forest_carbon_gross_removals__Mg_CO2e") as "gfw_forest_carbon_gross_removals__Mg_CO2e",
        sum("gfw_forest_carbon_net_flux__Mg_CO2e") as "gfw_forest_carbon_net_flux__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions__Mg_CO2e") as "gfw_forest_carbon_gross_emissions__Mg_CO2e",
      )
  }

  def aggChange(groupByCols: List[String],
                wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val cols =
      if (!wdpa)
        groupByCols ::: List("umd_tree_cover_loss__year") ::: contextualLayers ::: List(
          "wdpa_protected_area__iucn_cat"
        )
      else groupByCols ::: List("umd_tree_cover_loss__year") ::: contextualLayers

    df.groupBy(cols.head, cols.tail: _*)
      .agg(
        sum("umd_tree_cover_loss__ha") as "umd_tree_cover_loss__ha",
        sum("whrc_aboveground_biomass_loss__Mg") as "whrc_aboveground_biomass_loss__Mg",
        sum("whrc_aboveground_co2_emissions__Mg") as "whrc_aboveground_co2_emissions__Mg",
        sum("gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_co2_only__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e") as "gfw_forest_carbon_gross_emissions_non_co2__Mg_CO2e",
        sum("gfw_forest_carbon_gross_emissions__Mg_CO2e") as "gfw_forest_carbon_gross_emissions__Mg_CO2e",
      )
  }

  def whitelist(groupByCols: List[String],
                wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val defaultAggCols = List(
      max(length($"tsc_tree_cover_loss_drivers__type")).cast("boolean") as "tsc_tree_cover_loss_drivers__type",
      max(length($"esa_land_cover_2015__class"))
        .cast("boolean") as "esa_land_cover_2015__class",
      max($"is__umd_regional_primary_forest_2001") as "is__umd_regional_primary_forest_2001",
      max($"is__birdlife_alliance_for_zero_extinction_sites") as "is__birdlife_alliance_for_zero_extinction_sites",
      max(length($"gfw_plantations__type"))
        .cast("boolean") as "gfw_plantations__type",
      max($"is__gmw_mangroves_1996") as "is__gmw_mangroves_1996",
      max($"is__gmw_mangroves_2016") as "is__gmw_mangroves_2016",
      max(length($"ifl_intact_forest_landscapes__year"))
        .cast("boolean") as "ifl_intact_forest_landscapes__year",
      max($"is__gfw_tiger_landscape") as "is__gfw_tiger_landscape",
      max($"is__landmark_land_right") as "is__landmark_land_right",
      max($"is__gfw_land_right") as "is__gfw_land_right",
      max($"is__birdlife_key_biodiversity_area") as "is__birdlife_key_biodiversity_area",
      max($"is__gfw_mining") as "is__gfw_mining",
      max($"is__peatland") as "is__peatland",
      max($"is__gfw_oil_palm") as "is__gfw_oil_palm",
      max($"is__idn_forest_moratorium") as "is__idn_forest_moratorium",
      max($"is__gfw_wood_fiber") as "is__gfw_wood_fiber",
      max($"is__gfw_resource_right") as "is__gfw_resource_right",
      max($"is__gfw_managed_forest") as "is__gfw_managed_forest",
      max($"is__umd_tree_cover_gain_2000-2012") as "is__umd_tree_cover_gain_2000-2012"
    )

    val aggCols =
      if (!wdpa)
        defaultAggCols ::: List(
          max(length($"wdpa_protected_area__iucn_cat"))
            .cast("boolean") as "wdpa_protected_area__iucn_cat"
        )
      else defaultAggCols

    df.groupBy(groupByCols.head, groupByCols.tail: _*)
      .agg(aggCols.head, aggCols.tail: _*)

  }

  def whitelist2(groupByCols: List[String],
                 wdpa: Boolean = false)(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    val defaultAggCols: List[Column] = List(
      max($"tsc_tree_cover_loss_drivers__type") as "tsc_tree_cover_loss_drivers__type",
      max($"esa_land_cover_2015__class") as "esa_land_cover_2015__class",
      max($"is__umd_regional_primary_forest_2001") as "is__umd_regional_primary_forest_2001",
      max($"is__birdlife_alliance_for_zero_extinction_site") as "is__birdlife_alliance_for_zero_extinction_site",
      max($"gfw_plantation__type") as "gfw_plantation__type",
      max($"is__gmw_mangroves_1996") as "is__gmw_mangroves_1996",
      max($"is__gmw_mangroves_2016") as "is__gmw_mangroves_2016",
      max($"ifl_intact_forest_landscapes__year") as "ifl_intact_forest_landscapes__year",
      max($"is__gfw_tiger_landscapes") as "is__gfw_tiger_landscapes",
      max($"is__landmark_indigenous_and_community_lands") as "is__landmark_indigenous_and_community_lands",
      max($"is__gfw_land_rights") as "is__gfw_land_rights",
      max($"is__birdlife_key_biodiversity_areas") as "is__birdlife_key_biodiversity_areas",
      max($"is__gfw_mining") as "is__gfw_mining",
      max($"is__gfw_peatland") as "is__gfw_peatland",
      max($"is__gfw_oil_palm") as "is__gfw_oil_palm",
      max($"is__idn_forest_moratorium") as "is__idn_forest_moratorium",
      max($"is__gfw_wood_fiber") as "is__gfw_wood_fiber",
      max($"is__gfw_resource_rights") as "is__gfw_resource_rights",
      max($"is__gfw_managed_forests") as "is__gfw_managed_forests",
      max($"is__umd_tree_cover_gain") as "is__umd_tree_cover_gain"
    )

    val aggCols = if (!wdpa)
      defaultAggCols ::: List(
        max($"wdpa_protected_area__iucn_cat") as "wdpa_protected_area__iucn_cat"
      )
    else defaultAggCols

    df.groupBy(groupByCols.head, groupByCols.tail: _*)
      .agg(aggCols.head, aggCols.tail: _*)

  }
}
