package pl.lewandowski.youtubesummary;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class YTSubtitlesDownloader {


    private final ChatModel chatModel;

    public YTSubtitlesDownloader(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    //narazie dodajemy tylko event listenera, który wywoła metodę downloadSubtitles() po uruchomieniu aplikacji
    @EventListener(ApplicationReadyEvent.class)
    public void downloadSubtitles() throws IOException, InterruptedException {

        String videoId = "YIMvxQaYjGQ";
        //wywołanie zewnętrznego programu youtube-dl z parametrami --write-auto-sub --skip-download
        Runtime.getRuntime().exec(String.format(
                "docker run --rm -v \"%s:/downloads\" jauderho/yt-dlp:latest" +
                        " --write-auto-sub --sub-lang pl " +
                        "--skip-download -o \"/downloads/subtitles.vtt\" " +
                        "https://youtube.com/watch?v=%s", new File("."), videoId
        )).waitFor();


        //Wczytanie pliku z napisami
        FileSystemResource subtitles = new FileSystemResource("subtitles.vtt.pl.vtt");
        String subtitlesContent = StreamUtils.copyToString(subtitles.getInputStream(), StandardCharsets.UTF_8);

//        System.out.println(subtitlesContent);


        ChatResponse chatResponse = chatModel.call(
                new Prompt(
                        "Streść o czym jest ten film: " + subtitlesContent,
                        OllamaOptions.builder().model(OllamaModel.LLAMA3_2)
                                .build()
                )
        );

        System.out.println(chatResponse.getResult().getOutput());

    }
}
