package controllers.actors

import akka.actor.Actor
import controllers.Application._

case class CreateBookmarkMessage(url: String, name: String, tags: String)


/**
 * Actors for the bookmark handling.
 */
class BookmarkActor extends Actor {
  def receive = {
    case CreateBookmarkMessage(url, name, tags) =>
      thumbnailActor ! ThumbnailMessage(url, url.hashCode.toString)
      crawlActor ! CrawlMessage(url)
  }
}

