package controllers.util

import sys.process._

/**
 * Creates thumbnail previews of a website.
 */
object ThumbnailGenerator {

  def thumbnail(url: String, filename: String) {
    val outFile = "public/images/thumbnails/tmp/" + filename + ".png"
    Seq("cutycapt", "--url=" + url, "--out=" + outFile).!
    Seq("convert", outFile, "-crop", "700x800+0+100", "public/images/thumbnails/" + filename + ".png").!
  }
}
