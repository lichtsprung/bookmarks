package models

import collection.mutable.ListBuffer
import java.io.{FileOutputStream, File}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import collection.mutable
import sun.awt.ScrollPaneWheelScroller


/**
 * A simple bookmark data representation
 * @param url the url of the bookmark
 * @param name the name of the bookmark
 * @param tags the tags associated with the bookmark
 */
case class Bookmark(url: String, name: String, tags: String)

/**
 *
 * @param tag
 */
case class Tag(tag: String)

object Bookmark {

  val base = "http://www.openplexus.net/documents"
  val NS = base + "#"

  var bookmarks = ListBuffer[Bookmark]()

  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF)
  loadOntologyTo(model)


  val bookmarkClass = model.getOntClass(NS + "Bookmark")
  val tagClass = model.getOntClass(NS + "Tag")

  val urlProperty = model.getDatatypeProperty(NS + "url")
  val taggedProperty = model.getObjectProperty(NS + "isTaggedWith")
  val nameProperty = model.getDatatypeProperty(NS + "name")


  def all(): List[Bookmark] = {
    val bookmarkList = SparqlQueries.getBookmarks(model)



    bookmarkList
  }

  def tagList() = {
    val tags = SparqlQueries.getTags(model)
    val map = mutable.HashMap[Tag, List[Bookmark]]()

    tags.foreach(tag => {
      val bookmarks = SparqlQueries.getBookmarksForTag(tag, model)
      map += tag -> bookmarks.toList
    })
    map.toMap
  }

  def create(url: String, name: String, tags: String) {

    val newBookmark = model.createIndividual(NS + "Bookmark_" + url.hashCode(), bookmarkClass)

    val urlStatement = model.createStatement(newBookmark, urlProperty, url)
    model.add(urlStatement)
    val nameStatement = model.createStatement(newBookmark, nameProperty, name)
    model.add(nameStatement)


    for (s <- tags.split(",")) {
      val newTag = model.createIndividual(NS + s.trim, tagClass)
      val taggedStatement = model.createStatement(newBookmark, taggedProperty, newTag)
      model.add(taggedStatement)
    }

    writeModel()
    bookmarks += Bookmark(url, name, tags)
  }

  def delete(url: String) {
    bookmarks = bookmarks.filterNot(b => b.url.equalsIgnoreCase(url))
  }

  private def writeModel() {
    model.write(new FileOutputStream(new File("public/ontologies/documents.owl")), "TURTLE", base)
  }

  private def loadOntologyTo(model: OntModel) {
    model.read(scala.io.Source.fromFile(new File("public/ontologies/documents.owl")).bufferedReader(), base, "TURTLE")
  }
}
