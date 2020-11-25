package speechbrowser

import java.io.{File, FileOutputStream}

import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.polly.PollyClient
import software.amazon.awssdk.services.polly.model.{
  OutputFormat,
  SynthesizeSpeechRequest,
  SynthesizeSpeechResponse,
  TextType,
  VoiceId
}

object Text {

  val maxChars = 3000

  private def synthesizeSpeech(
      client: PollyClient,
      textType: TextType,
      text: String
  ): ResponseInputStream[SynthesizeSpeechResponse] =
    client.synthesizeSpeech(
      SynthesizeSpeechRequest.builder
        .outputFormat(OutputFormat.MP3)
//        .voiceId(VoiceId.BRIAN)
        .voiceId(VoiceId.AMY)
        .textType(textType)
        .text(text)
        .build()
    )

  private def transferToFile(
      response: ResponseInputStream[SynthesizeSpeechResponse],
      outputFile: File
  ): Unit = response.transferTo(new FileOutputStream(outputFile))

  def writeToAudioFile(
      client: PollyClient,
      outputFile: File
  )(textType: TextType, text: String): Unit = {
    assert(text.length <= maxChars, s"Text > $maxChars chars")
    println(s"Transcript:\n$text")
    transferToFile(synthesizeSpeech(client, textType, text), outputFile)
  }
}
