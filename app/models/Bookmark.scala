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


object Bookmark {

  var bookmarks = ListBuffer[Bookmark]()

  val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF)
  loadOntologyTo(model)

  val base = "http://www.openplexus.net/documents"
  val NS = base + "#"

  val bookmarkClass = model.getOntClass(NS + "Bookmark")

  val urlProperty = model.getDatatypeProperty(NS + "url")
  val nameProperty = model.getDatatypeProperty(NS + "name")
  val taggedProperty = model.getDatatypeProperty(NS + "tagged")

  val queryAllBookmarks =
    """
      PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>
      PREFIX plexus:<http://www.openplexus.net/documents#>

      SELECT ?name ?url ?tag
      |WHERE {
      |  ?bookmark rdf:type plexus:Bookmark .
      |  ?bookmark plexus:name ?name .
      |  ?bookmark plexus:url ?url .
      |  ?bookmark plexus:tagged ?tag
      |}
    """.stripMargin

  val query = QueryFactory.create(queryAllBookmarks)



  def all(): List[Bookmark] = {
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

    for (s <- tags.split(",")){
      val taggedStatement = model.createStatement(newBookmark, taggedProperty, s.trim)
      model.add(taggedStatement)
    }

    //writeModel()
    bookmarks += Bookmark(url, name, tags)
  }

  def delete(url: String) {
    bookmarks = bookmarks.filterNot(b => b.url.equalsIgnoreCase(url))
  }

  private def writeModel(){
    val writer = model.getWriter("RDF/XML")
    writer.setProperty("showXmlDeclaration","true")
    writer.setProperty("tab","4")
    val out = new FileOutputStream("public/ontologies/documents.owl")
    writer.write(model, out, base)
    out.close()
  }

  private def loadOntologyTo(model: OntModel){
    model.read(scala.io.Source.fromFile(new File("public/ontologies/documents.owl")).bufferedReader(), "RDF/XML")
  }
}
