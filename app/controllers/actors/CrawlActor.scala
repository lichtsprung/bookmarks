package controllers.actors

import akka.actor.Actor
import edu.uci.ics.crawler4j.crawler.{CrawlController, CrawlConfig, Page, WebCrawler}
import java.util.regex.Pattern
import edu.uci.ics.crawler4j.url.WebURL
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.{RobotstxtServer, RobotstxtConfig}

case class CrawlMessage(url: String)

class BookmarkPageCrawler extends WebCrawler {
  val filter = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
    + "|png|tiff?|mid|mp2|mp3|mp4"
    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$")

  override def shouldVisit(url: WebURL): Boolean = {
    val href = url.getURL.toLowerCase
    true
  }


  override def visit(page: Page) {
    page.getParseData match {
      case data: HtmlParseData =>
        // TODO Indexing and Term Extraction in separaten Actor
        println("Stopword Removal...")
        println("Term Weighting and Topic Extraction...")
        println("Adding Tags to Ontology...")
    }
  }
}

class CrawlActor extends Actor {
  def receive = {
    case CrawlMessage(url) =>
      val config = new CrawlConfig()
      config.setCrawlStorageFolder("data/crawl")
      config.setMaxDepthOfCrawling(0)
      config.setPolitenessDelay(1000) // Delay in ms
      config.setResumableCrawling(false)
      val pageFetcher = new PageFetcher(config)
      val txtConfig = new RobotstxtConfig()
      val txtServer = new RobotstxtServer(txtConfig, pageFetcher)
      val controller = new CrawlController(config, pageFetcher, txtServer)
      println("adding: " + url)
      controller.addSeed(url)
      controller.start(classOf[BookmarkPageCrawler], 1)
  }
}



