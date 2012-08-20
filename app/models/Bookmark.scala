package models

import collection.mutable.ListBuffer
import play.api.data._
import play.api.data.Forms._

/**
 * A Bookmark!
 */
case class Bookmark(url: String, title: String, author: String, description: String, tags: String)


object Bookmark {

  var bookmarks = ListBuffer[Bookmark]()

  def all(): List[Bookmark] = bookmarks.toList

  def create(url: String, title: String, author: String, description: String, tags: String) {
    bookmarks += Bookmark(url, title, author, description, tags)
  }

  def delete(url: String){
    bookmarks = bookmarks.filterNot(b => b.url.equalsIgnoreCase(url))
  }
}
