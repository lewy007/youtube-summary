package pl.lewandowski.youtubesummary;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class YTSubtitlesDownloader {


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

    }
}
