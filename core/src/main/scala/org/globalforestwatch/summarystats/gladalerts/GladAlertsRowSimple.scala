package org.globalforestwatch.summarystats.gladalerts

import org.globalforestwatch.features.SimpleFeatureId

case class GladAlertsRowSimple(id: SimpleFeatureId,
                               dataGroup: GladAlertsDataGroup,
                               data: GladAlertsData)
