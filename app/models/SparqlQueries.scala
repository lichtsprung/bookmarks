package models

import com.hp.hpl.jena.query.{QueryExecutionFactory, QueryFactory}
import com.hp.hpl.jena.ontology.OntModel
import collection.mutable

/**
 * Spaqrql queries needed for the bookmark management
 */
object SparqlQueries {

  def getBookmarks(model: OntModel): List[Bookmark] = {
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?name
        |WHERE{
        | ?bookmark rdf:type plexus:Bookmark .
        | ?bookmark plexus:name ?name
        |}
      """.stripMargin

    val jenaQuery = QueryFactory.create(query)

    val execution = QueryExecutionFactory.create(jenaQuery, model)
    val results = execution.execSelect()

    val bookmarks = mutable.Buffer[Bookmark]()
    while (results.hasNext) {
      val r = results.next()
      val name = r.getLiteral("name").getString
      val tags = getTagsForBookmark(name, model)
      var tagString = ""
      tags.foreach(tag => tagString = tagString + " " + tag.tag)
      val url = getURLForBookmark(name, model)
      bookmarks += Bookmark(url, name, tagString)
    }

    execution.close()
    bookmarks.toList
  }


  def getBookmarksForTag(tag: Tag, model: OntModel) = {
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?name ?url
        |WHERE{
        | ?bookmark rdf:type plexus:Bookmark .
        | ?bookmark plexus:name ?name .
        | ?bookmark plexus:url ?url .
        | ?bookmark plexus:isTaggedWith plexus:TAG
        |}
      """.stripMargin.replaceAllLiterally("TAG", tag.tag)

    val jenaQuery = QueryFactory.create(query)

    val execution = QueryExecutionFactory.create(jenaQuery, model)
    val results = execution.execSelect()

    val bookmarks = mutable.Buffer[Bookmark]()
    while (results.hasNext) {
      val r = results.next()
      bookmarks += Bookmark(r.getLiteral("url").getString, r.getLiteral("name").getString, null)
    }
    execution.close()

    bookmarks.toList
  }

  def getTags(model: OntModel): List[Tag] = {
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?tag
        |WHERE{
        | ?tag rdf:type plexus:Tag
        |}
      """.stripMargin

    val jenaQuery = QueryFactory.create(query)

    val execution = QueryExecutionFactory.create(jenaQuery, model)
    val results = execution.execSelect()

    val tags = mutable.Buffer[Tag]()
    while (results.hasNext) {
      val r = results.next()
      tags += Tag(r.getResource("tag").getLocalName)
    }

    tags.toList
  }

  def getTagsForBookmark(name: String, model: OntModel): List[Tag] = {
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?tag
        |WHERE{
        | ?bookmark rdf:type plexus:Bookmark .
        | ?bookmark plexus:name NAME .
        | ?bookmark plexus:isTaggedWith ?tag
        |}
      """.stripMargin.replaceAllLiterally("NAME", "\"" + name + "\"")


    val jenaQuery = QueryFactory.create(query)

    val execution = QueryExecutionFactory.create(jenaQuery, model)
    val results = execution.execSelect()

    val tags = mutable.Buffer[Tag]()
    while (results.hasNext) {
      val r = results.next()
      tags += Tag(r.getResource("tag").getLocalName)
    }

    execution.close()
    tags.toList
  }

  def deleteBookmark(id: String, model: OntModel){
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?bookmark
        |WHERE{
        | ?bookmark rdf:type plexus:Bookmark .
        | ?bookmark plexus:id URL
        |}
      """.stripMargin.replaceAllLiterally("URL", "\"" + id + "\"")

    val jenaQuery = QueryFactory.create(query)
    val execution = QueryExecutionFactory.create(jenaQuery,model)
    val results = execution.execSelect()

    val bookmark = results.next().getResource("bookmark")
    model.remove(model.listStatements(bookmark, null, null))
    execution.close()
  }

  def getURLForBookmark(name: String, model: OntModel): String = {
    val query =
      """
        |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        |PREFIX plexus: <http://www.openplexus.net/documents#>
        |SELECT ?url
        |WHERE{
        | ?bookmark rdf:type plexus:Bookmark .
        | ?bookmark plexus:name NAME .
        | ?bookmark plexus:url ?url
        |}
      """.stripMargin.replaceAllLiterally("NAME", "\"" + name + "\"")


    val jenaQuery = QueryFactory.create(query)

    val execution = QueryExecutionFactory.create(jenaQuery, model)
    val results = execution.execSelect()
    val url = results.next().getLiteral("url").getString

    execution.close()
    url
  }

}
