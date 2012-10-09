package controllers.actors

import akka.actor.Actor
import edu.uci.ics.crawler4j.crawler.{CrawlController, CrawlConfig, Page, WebCrawler}
import edu.uci.ics.crawler4j.url.WebURL
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.{RobotstxtServer, RobotstxtConfig}

case class CrawlMessage(url: String, tags: String)

class BookmarkPageCrawler extends WebCrawler {

  override def shouldVisit(url: WebURL): Boolean = {
    val href = url.getURL.toLowerCase
    href.endsWith("html") || href.endsWith("htm")
  }

  import controllers.Application._

  override def visit(page: Page) {
    page.getParseData match {
      case data: HtmlParseData =>
        indexActor ! IndexMessage(data.getText, page.getWebURL.getURL)
    }
  }
}

class CrawlActor extends Actor {
  def receive = {
    case CrawlMessage(url, tags) =>
      val config = new CrawlConfig()
      config.setCrawlStorageFolder("tmp/crawl")
      config.setMaxDepthOfCrawling(0)
      config.setPolitenessDelay(1000) // Delay in ms
      config.setResumableCrawling(false)
      val pageFetcher = new PageFetcher(config)
      val txtConfig = new RobotstxtConfig()
      val txtServer = new RobotstxtServer(txtConfig, pageFetcher)
      val controller = new CrawlController(config, pageFetcher, txtServer)
      controller.addSeed(url)
      controller.start(classOf[BookmarkPageCrawler], 1)
  }
}



