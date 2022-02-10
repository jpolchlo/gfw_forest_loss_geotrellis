package org.globalforestwatch.summarystats.carbon_sensitivity

import cats.implicits._
import geotrellis.layer.{LayoutDefinition, SpatialKey}
import geotrellis.raster.Raster
import org.globalforestwatch.grids.{GridSources, GridTile}
import org.globalforestwatch.layers._
import org.globalforestwatch.util.Util.getAnyMapValue

/**
  * @param gridTile top left corner, padded from east ex: "10N_010E"
  */
case class CarbonSensitivityGridSources(gridTile: GridTile, kwargs:  Map[String, Any]) extends GridSources {

  val model: String = getAnyMapValue[String](kwargs,"sensitivityType")

  val treeCoverLoss = TreeCoverLoss(gridTile)
  val treeCoverDensity2000 = TreeCoverDensityThreshold2000(gridTile)
  val biomassPerHectar = BiomassPerHectar(gridTile)
  val grossCumulAbovegroundRemovalsCo2 = GrossCumulAbovegroundRemovalsCo2(gridTile, model)
  val grossCumulBelowgroundRemovalsCo2 = GrossCumulBelowgroundRemovalsCo2(gridTile, model)
  val netFluxCo2 = NetFluxCo2e(gridTile, model)
  val agcEmisYear = AgcEmisYear(gridTile, model)
  val soilCarbonEmisYear = SoilCarbonEmisYear(gridTile, model)
  val grossEmissionsCo2eNonCo2 = GrossEmissionsNonCo2Co2e(gridTile, model)
  val grossEmissionsCo2eCo2Only = GrossEmissionsCo2OnlyCo2e(gridTile, model)
  val jplTropicsAbovegroundBiomassDensity2000 = JplTropicsAbovegroundBiomassDensity2000(gridTile)

  val fluxModelExtent = FluxModelExtent(gridTile, model)
  val removalForestType = RemovalForestType(gridTile, model)
  val treeCoverGain = TreeCoverGain(gridTile)
  val mangroveBiomassExtent = MangroveBiomassExtent(gridTile)
  val treeCoverLossDrivers = TreeCoverLossDrivers(gridTile)
  val ecozones = Ecozones(gridTile)
  val protectedAreas = ProtectedAreas(gridTile)
  val landmark = Landmark(gridTile)
  val intactForestLandscapes = IntactForestLandscapes(gridTile)
  val plantationsTypeFluxModel = PlantationsTypeFluxModel(gridTile)
  val intactPrimaryForest = IntactPrimaryForest(gridTile)
  val peatlandsExtentFluxModel = PeatlandsExtentFluxModel(gridTile)
  val forestAgeCategory = ForestAgeCategory(gridTile, model)
  val jplTropicsAbovegroundBiomassExtent2000 = JplTropicsAbovegroundBiomassExtent2000(gridTile)
  val fiaRegionsUsExtent = FiaRegionsUsExtent(gridTile)
  val brazilBiomes = BrazilBiomes(gridTile)
  val riverBasins = RiverBasins(gridTile)
  val primaryForest = PrimaryForest(gridTile)
  val treeCoverLossLegalAmazon = TreeCoverLossLegalAmazon(gridTile)
  val prodesLegalAmazonExtent2000 = ProdesLegalAmazonExtent2000(gridTile)
  val tropicLatitudeExtent = TropicLatitudeExtent(gridTile)
  val burnYearHansenLoss = BurnYearHansenLoss(gridTile)
  val grossEmissionsNodeCodes = GrossEmissionsNodeCodes(gridTile, model)

  def readWindow(windowKey: SpatialKey, windowLayout: LayoutDefinition): Either[Throwable, Raster[CarbonSensitivityTile]] = {

    for {
      // Failure for any of these reads will result in function returning Left[Throwable]
      // These are effectively required fields without which we can't make sense of the analysis
      lossTile <- Either.catchNonFatal(treeCoverLoss.fetchWindow(windowKey, windowLayout)).right
      gainTile <- Either.catchNonFatal(treeCoverGain.fetchWindow(windowKey, windowLayout)).right
      tcd2000Tile <- Either
        .catchNonFatal(treeCoverDensity2000.fetchWindow(windowKey, windowLayout))
        .right

    } yield {
      // Failure for these will be converted to optional result and propagated with TreeLossTile
      val biomassTile = biomassPerHectar.fetchWindow(windowKey, windowLayout)
      val grossCumulAbovegroundRemovalsCo2Tile = grossCumulAbovegroundRemovalsCo2.fetchWindow(windowKey, windowLayout)
      val grossCumulBelowgroundRemovalsCo2Tile = grossCumulBelowgroundRemovalsCo2.fetchWindow(windowKey, windowLayout)
      val netFluxCo2Tile = netFluxCo2.fetchWindow(windowKey, windowLayout)
      val agcEmisYearTile = agcEmisYear.fetchWindow(windowKey, windowLayout)
      val soilCarbonEmisYearTile = soilCarbonEmisYear.fetchWindow(windowKey, windowLayout)
      val grossEmissionsCo2eNonCo2Tile = grossEmissionsCo2eNonCo2.fetchWindow(windowKey, windowLayout)
      val grossEmissionsCo2eCo2OnlyTile = grossEmissionsCo2eCo2Only.fetchWindow(windowKey, windowLayout)
      val jplTropicsAbovegroundBiomassDensity2000Tile = jplTropicsAbovegroundBiomassDensity2000.fetchWindow(windowKey, windowLayout)

      val fluxModelExtentTile = fluxModelExtent.fetchWindow(windowKey, windowLayout)
      val removalForestTypeTile = removalForestType.fetchWindow(windowKey, windowLayout)
      val mangroveBiomassExtentTile = mangroveBiomassExtent.fetchWindow(windowKey, windowLayout)
      val driversTile = treeCoverLossDrivers.fetchWindow(windowKey, windowLayout)
      val ecozonesTile = ecozones.fetchWindow(windowKey, windowLayout)
      val landmarkTile = landmark.fetchWindow(windowKey, windowLayout)
      val wdpaTile = protectedAreas.fetchWindow(windowKey, windowLayout)
      val intactForestLandscapesTile = intactForestLandscapes.fetchWindow(windowKey, windowLayout)
      val plantationsTypeFluxTile = plantationsTypeFluxModel.fetchWindow(windowKey, windowLayout)
      val intactPrimaryForestTile = intactPrimaryForest.fetchWindow(windowKey, windowLayout)
      val peatlandsExtentFluxTile = peatlandsExtentFluxModel.fetchWindow(windowKey, windowLayout)
      val forestAgeCategoryTile = forestAgeCategory.fetchWindow(windowKey, windowLayout)
      val jplTropicsAbovegroundBiomassExtent2000Tile = jplTropicsAbovegroundBiomassExtent2000.fetchWindow(windowKey, windowLayout)
      val fiaRegionsUsExtentTile = fiaRegionsUsExtent.fetchWindow(windowKey, windowLayout)
      val braBiomesTile = brazilBiomes.fetchWindow(windowKey, windowLayout)
      val riverBasinsTile = riverBasins.fetchWindow(windowKey, windowLayout)
      val primaryForestTile = primaryForest.fetchWindow(windowKey, windowLayout)
      val treeCoverLossLegalAmazonTile = treeCoverLossLegalAmazon.fetchWindow(windowKey, windowLayout)
      val prodesLegalAmazonExtent2000Tile = prodesLegalAmazonExtent2000.fetchWindow(windowKey, windowLayout)
      val tropicLatitudeExtentTile = tropicLatitudeExtent.fetchWindow(windowKey, windowLayout)
      val burnYearHansenLossTile = burnYearHansenLoss.fetchWindow(windowKey, windowLayout)
      val grossEmissionsNodeCodesTile = grossEmissionsNodeCodes.fetchWindow(windowKey, windowLayout)

      val tile = CarbonSensitivityTile(
        lossTile,
        gainTile,
        tcd2000Tile,
        biomassTile,
        grossCumulAbovegroundRemovalsCo2Tile,
        grossCumulBelowgroundRemovalsCo2Tile,
        netFluxCo2Tile,
        agcEmisYearTile,
        soilCarbonEmisYearTile,
        grossEmissionsCo2eNonCo2Tile,
        grossEmissionsCo2eCo2OnlyTile,
        jplTropicsAbovegroundBiomassDensity2000Tile,

        fluxModelExtentTile,
        removalForestTypeTile,
        mangroveBiomassExtentTile,
        driversTile,
        ecozonesTile,
        landmarkTile,
        wdpaTile,
        intactForestLandscapesTile,
        plantationsTypeFluxTile,
        intactPrimaryForestTile,
        peatlandsExtentFluxTile,
        forestAgeCategoryTile,
        jplTropicsAbovegroundBiomassExtent2000Tile,
        fiaRegionsUsExtentTile,
        braBiomesTile,
        riverBasinsTile,
        primaryForestTile,
        treeCoverLossLegalAmazonTile,
        prodesLegalAmazonExtent2000Tile,
        tropicLatitudeExtentTile,
        burnYearHansenLossTile,
        grossEmissionsNodeCodesTile
      )

      Raster(tile, windowKey.extent(windowLayout))
    }
  }
}

object CarbonSensitivityGridSources {

  @transient
  private lazy val cache =
    scala.collection.concurrent.TrieMap.empty[String, CarbonSensitivityGridSources]

  def getCachedSources(gridTile: GridTile, kwargs:  Map[String, Any]): CarbonSensitivityGridSources = {

    cache.getOrElseUpdate(gridTile.tileId, CarbonSensitivityGridSources(gridTile: GridTile, kwargs:  Map[String, Any]))

  }

}