package models

import collection.mutable.ListBuffer
import controllers.{routes, Assets}
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import java.io.File
import org.semanticweb.owlapi.util.DefaultPrefixManager


/**
 * A Bookmark!
 */
case class Bookmark(url: String, title: String, author: String, description: String, tags: String)


object Bookmark {

  var bookmarks = ListBuffer[Bookmark]()

  val ontManager = OWLManager.createOWLOntologyManager()
  val prefix = new DefaultPrefixManager("http://www.openplexus.net/documents.owl#")
  val factory = ontManager.getOWLDataFactory



  def all(): List[Bookmark] = {

    bookmarks.toList
  }

  def create(url: String, title: String, author: String, description: String, tags: String) {

    bookmarks += Bookmark(url, title, author, description, tags)
  }

  def delete(url: String){
    bookmarks = bookmarks.filterNot(b => b.url.equalsIgnoreCase(url))
  }
}
