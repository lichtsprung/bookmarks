package models

import java.io.{FileOutputStream, File}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import collection.mutable


/**
 * A simple bookmark data representation
 * @param url the url of the bookmark
 * @param name the name of the bookmark
 * @param tags the tags associated with the bookmark
 */
case class Bookmark(url: String, name: String, tags: String, id: String)

/**
 * A simple tag representation
 * @param tag the tag
 */
case class Tag(tag: String)

/**
 * Bookmark Object
 */
object Bookmark {

  val base = "http://www.openplexus.net/documents"
  val NS = base + "#"

  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF)
  loadOntology(model)


  val bookmarkClass = model.getOntClass(NS + "Bookmark")
  val tagClass = model.getOntClass(NS + "Tag")

  val urlProperty = model.getDatatypeProperty(NS + "url")
  val taggedProperty = model.getObjectProperty(NS + "isTaggedWith")
  val nameProperty = model.getDatatypeProperty(NS + "name")
  val idProperty = model.getDatatypeProperty(NS + "id")


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
    val idStatement = model.createStatement(newBookmark, idProperty, url.hashCode().toString)
    model.add(idStatement)

    for (s <- tags.split(",")) {
      val newTag = model.createIndividual(NS + s.trim, tagClass)
      val taggedStatement = model.createStatement(newBookmark, taggedProperty, newTag)
      model.add(taggedStatement)
    }

    writeModel()
  }

  /**
   * Returns all bookmarks that were tagged with a specific tag.
   * @param tag the tag
   * @return a List of Bookmarks that were tagged with "Tag"
   */
  def forTag(tag: String) = {
    SparqlQueries.getBookmarksForTag(Tag(tag), model)
  }

  def delete(hash: String) {
    SparqlQueries.deleteBookmark(hash, model)
    writeModel()
  }

  private def writeModel() {
    model.write(new FileOutputStream(new File("public/ontologies/documents.owl")), "TURTLE", base)
  }

  private def loadOntology(model: OntModel) {
    model.read(scala.io.Source.fromFile(new File("public/ontologies/documents.owl")).bufferedReader(), base, "TURTLE")
  }

  def apply(url: String, name: String, tags: String):Bookmark = apply(url, name, tags, Integer.toString(url.hashCode))
}
