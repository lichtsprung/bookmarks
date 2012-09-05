package controllers.actors

import akka.actor.Actor
import models.util.ThumbnailGenerator

case class ThumbnailMessage(url: String, filename: String)

/**
 * Opens a websites and creates a thumbnail.
 */
class ThumbnailActor extends Actor {

  def receive = {
    case ThumbnailMessage(url, filename) => ThumbnailGenerator.thumbnail(url, filename)
  }
}
