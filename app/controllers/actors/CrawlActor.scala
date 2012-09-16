package controllers.actors

import akka.actor.Actor
import edu.uci.ics.crawler4j.crawler.{Page, WebCrawler}
import java.util.regex.Pattern
import edu.uci.ics.crawler4j.url.WebURL
import edu.uci.ics.crawler4j.parser.HtmlParseData

case class CrawlMessage(url: String)

class CrawlActor extends Actor{
  def receive = {
    case CrawlMessage(url) =>

  }
}

class BookmarkPageCrawler extends WebCrawler{
  val filter = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
    + "|png|tiff?|mid|mp2|mp3|mp4"
    + "|wav|avi|mov|mpeg|ram|m4v|pdf"
    + "|rm|smil|wmv|swf|wma|zip|rar|gz))$")

  override def shouldVisit(url: WebURL): Boolean = {
    val href = url.getURL.toLowerCase
    filter.matcher(href).matches
  }


  override def visit(page: Page){
    page.getParseData match {
      case data: HtmlParseData =>
        // TODO Indexing and Term Extraction in separaten Actor
        println(data.getText)
    }
  }
}

