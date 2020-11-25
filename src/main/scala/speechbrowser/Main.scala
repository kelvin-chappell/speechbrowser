package speechbrowser

import java.io.File

import software.amazon.awssdk.regions.Region.EU_WEST_1
import software.amazon.awssdk.services.polly.PollyClient

object Main extends App {

  val outputFileName = "speech.mp3"

  val client = PollyClient.builder.region(EU_WEST_1).build()

//  Text.writeToAudioFile(client, new File(outputFileName))(args(0))

  Url.writeToAudioFile(client, new File(outputFileName))(args(0))
}
