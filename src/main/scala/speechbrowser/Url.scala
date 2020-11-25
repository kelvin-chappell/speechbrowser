package speechbrowser

import java.io.File

import org.jsoup.Jsoup
import software.amazon.awssdk.services.polly.PollyClient

object Url {

  private def document(url: String) =
    Jsoup.connect(url).get()

  def writeToAudioFile(client: PollyClient, outputFile: File)(url: String): Unit =
    Doc.writeToAudioFile(client, outputFile)(document(url))
}
