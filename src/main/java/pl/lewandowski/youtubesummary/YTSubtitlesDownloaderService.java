package pl.lewandowski.youtubesummary;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class YTSubtitlesDownloaderService {


    private final ChatModel chatModel;

    public YTSubtitlesDownloaderService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    //event listener wywoła metodę downloadSubtitles() automatycznie po uruchomieniu aplikacji
//    @EventListener(ApplicationReadyEvent.class)
    public YtChatResponse getYtChatResponse(String videoId) throws IOException, InterruptedException {

//        String videoId = "YIMvxQaYjGQ";
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

        //OllAma chat model
        ChatResponse summary = chatModel.call(
                new Prompt(
                        "Streść o czym jest ten film: " + subtitlesContent,
                        OllamaOptions.builder().model(OllamaModel.LLAMA3_2)
                                .build()
                )
        );

        ChatResponse homework = chatModel.call(
                new Prompt(
                        "Opracuj konkretne zadanie domowe związane z tematem poruszanym w tej treści." +
                                " Dołącz praktyczne wskazówki, które pomogą odbiorcy skutecznie je wykonać: " +
                                summary,
                        OllamaOptions.builder().model(OllamaModel.LLAMA3_2)
                                .build()
                )
        );


        return new YtChatResponse(
                summary.getResult().getOutput().getContent(),
                homework.getResult().getOutput().getContent()
        );

        //OpenAI chat model
//        ChatResponse chatResponse = chatModel.call(
//                new Prompt("Streść o czym jest ten film: " + subtitlesContent));
//
//        System.out.println(chatResponse.getResult().getOutput());

    }
}
