package org.globalforestwatch.grids

trait NinetyByNinety1kmGrid[T <: GridSources] extends Grid[T] {
  val pixelSize = (90.0 / 9984.0)
  val gridSize = 90
  val rowCount = 9984
  val blockSize = 416
}
