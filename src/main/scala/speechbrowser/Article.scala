package speechbrowser

import java.io.File

import org.jsoup.nodes.Document
import software.amazon.awssdk.services.polly.PollyClient
import software.amazon.awssdk.services.polly.model.TextType
import scala.jdk.CollectionConverters._

object Article {

  private def isArticle(doc: Document) =
    doc.select("meta[property=article:section]").attr("content") != "Opinion"

  private def headlineText(doc: Document) =
    Option(doc.select("[itemprop=headline]").first)
      .map(_.text)
      .getOrElse(doc.select("h1").first.text)

  private def mainContent(doc: Document) =
    Option(doc.select("[itemprop=articleBody]").first)
      .getOrElse(doc.select("main").first)

  private def ssml(doc: Document): String = {
    val content = mainContent(doc)
    val paras   = content.select("p")
    val contentText = paras.asScala
      .map(_.text)
      .take(7)
      .mkString("<p>", "</p><p>", "</p>")
      .replaceAll("&", "&amp;")
      .replaceAll("'", "&apos;")
      .replaceAll(
        "(?i)(covid(?:-19)?|coronavirus)",
        """<break time="500ms"/><amazon:effect name="whispered">$1</amazon:effect><break time="500ms"/>"""
      )
      .replaceAll(
        "((?i)christmas)",
        """<prosody volume="x-loud" rate="x-slow">$1</prosody>"""
      )
      .replaceAll(
        "December",
        """<sub alias="the last month of the year">December</sub>"""
      )
      .replaceAll(
        """“([^”]+)”""",
        """<prosody rate="x-slow">&quot;$1&quot;</prosody>"""
      )
    s"""<speak>${headlineText(doc)}<break time="1s"/>$contentText</speak>"""
  }

  def writeToAudioFile(client: PollyClient, outputFile: File)(doc: Document): Unit = {
    assert(isArticle(doc), "Doc isn't an article")
    Text.writeToAudioFile(client, outputFile)(TextType.SSML, ssml(doc))
  }
}
