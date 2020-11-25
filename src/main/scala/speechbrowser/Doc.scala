package speechbrowser

import java.io.File

import org.jsoup.nodes.Document
import software.amazon.awssdk.services.polly.PollyClient
import software.amazon.awssdk.services.polly.model.TextType

object Doc {

  private def mainContent(doc: Document) =
    doc.select("[itemprop=articleBody]").first

  private def ssml(doc: Document): String = {
    val content = mainContent(doc)
    println(content)
    val contentText = content.text
      .replaceAll(
        "(Covid(?:-19)?)",
        """<break time="500ms"/><amazon:effect name="whispered">$1</amazon:effect><break time="500ms"/>"""
      )
      .replaceAll(
        "December",
        """<sub alias="the last month of the year">December</sub>"""
      )
      .replaceAll(
        """(“[^”]+”)""",
        """<lang xml:lang="en-US">$1</lang>"""
      )
    s"<speak>${contentText.take(Text.maxChars - 15)}</speak>"
  }

  def writeToAudioFile(client: PollyClient, outputFile: File)(doc: Document): Unit =
    Text.writeToAudioFile(client, outputFile)(TextType.SSML, ssml(doc))
}
