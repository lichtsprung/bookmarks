package models

import collection.mutable.ListBuffer
import java.io.{FileOutputStream, File}
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.{OntModel, OntModelSpec}
import com.hp.hpl.jena.query.{ResultSetFormatter, QueryExecutionFactory, QueryFactory}


/**
 * A Bookmark!
 */
case class Bookmark(url: String, name: String, tags: String)

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
    val query = SparqlQueries.getInstancesOfResource(NS + "Bookmark")
    val execution = QueryExecutionFactory.create(query, model)
    val results = execution.execSelect()

    ResultSetFormatter.out(System.out, results, query)
    execution.close()

    bookmarks.toList
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
