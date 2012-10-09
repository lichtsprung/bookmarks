package controllers.actors

import akka.actor.Actor
import org.apache.lucene.index.{FieldInfo, IndexWriterConfig, IndexWriter}
import org.apache.lucene.util.Version
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.FSDirectory
import java.io.File
import org.apache.lucene.document._

case class IndexMessage(pageText: String, url: String)

/**
 * Actor for parallel website indexing.
 */
class IndexActor extends Actor {
  val indexFolder = "data/index/"
  val config = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40))
  val directory = FSDirectory.open(new File(indexFolder))
  val indexWriter = new IndexWriter(directory, config)

  private def addDocument(document: Document) = {
    indexWriter.addDocument(document)
  }

  override def postStop() = {
    indexWriter.close()
  }

  def receive = {
    case IndexMessage(text, url) =>
      // Adding the text to the lucene index. probably a good idea to store the url of the text in the same document
      val document = new Document()
      val contentFieldType = new FieldType()
      contentFieldType.setIndexed(true)
      contentFieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS)
      contentFieldType.setTokenized(true)
      contentFieldType.setStored(true)
      contentFieldType.setStoreTermVectors(true)
      contentFieldType.freeze()

      val urlFieldType = new FieldType()
      urlFieldType.setTokenized(false)
      urlFieldType.setIndexed(true)
      urlFieldType.setStored(true)
      urlFieldType.freeze()

      val contentField = new Field("content", text, contentFieldType)
      val urlField = new Field("url", url, urlFieldType)
      document.add(urlField)
      document.add(contentField)
      println("Adding Document to Database...")
      addDocument(document)
  }
}
