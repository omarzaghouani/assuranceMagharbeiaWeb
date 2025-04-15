package com.ms.candidat.feedbackwebdistribue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedBackRestApi {

    @Autowired
    private FeedBackService feedBackService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public FeedBack add(@RequestBody FeedBack feedback) {
        return feedBackService.addFeedback(feedback);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeedBack> getAll() {
        return feedBackService.getAll();
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FeedBack update(@PathVariable Long id, @RequestBody FeedBack feedback) {
        return feedBackService.updateFeedback(id, feedback);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String delete(@PathVariable Long id) {
        return feedBackService.deleteFeedback(id);
    }
    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf() throws Exception {
        byte[] pdfBytes = feedBackService.generatePdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=feedbacks.pdf")
                .body(pdfBytes);
    }

  /*  @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportExcel() throws Exception {
        byte[] excelBytes = feedBackService.generateExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=feedbacks.xlsx")
                .body(excelBytes);
    }*/

}
