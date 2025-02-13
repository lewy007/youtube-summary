package pl.lewandowski.youtubesummary;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class YtController {

    private final YTSubtitlesDownloaderService ytSubtitlesDownloaderService;
    private final PdfReportService pdfReportService;

    public YtController(PdfReportService pdfReportService, YTSubtitlesDownloaderService ytSubtitlesDownloaderService) {
        this.pdfReportService = pdfReportService;
        this.ytSubtitlesDownloaderService = ytSubtitlesDownloaderService;
    }



    @GetMapping("/get-subtitles")
    public ResponseEntity<byte[]> generateReport(@RequestParam("videoId") String videoId) throws DocumentException, IOException, InterruptedException {

        YtChatResponse ytChatResponse = ytSubtitlesDownloaderService.getYtChatResponse(videoId);

        //tworzymy strumien wyjsciowy do przechowywania danych
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        //zapisujemy dokument w strumieniu wyjsciowym
        PdfWriter.getInstance(document, byteArrayOutputStream);


        document.open();
        pdfReportService.addImage(document);
        pdfReportService.addSummary(document, ytChatResponse);
        pdfReportService.addHomework(document, ytChatResponse);
        pdfReportService.addFooter(document);
        document.close();

        //tworzymy naglowki dla odpowiedzi
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "Report.pdf");


        return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
    }
}
