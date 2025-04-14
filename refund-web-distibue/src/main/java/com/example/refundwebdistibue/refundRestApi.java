package com.example.refundwebdistibue;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/refund")
@RestController
public class refundRestApi {
    @Autowired
    private refundService refundService;

        @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public refund addRefund(@RequestBody refund refund) {
        return refundService.addRefund(refund);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<refund> getAllRefunds() {
        return refundService.getAll();
    }

    @PutMapping(value = "/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public refund updateRefund(@PathVariable int id, @RequestBody refund newRefund) {
        return refundService.updateRefund(id, newRefund);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteRefund(@PathVariable int id) {
        return refundService.deleteRefund(id);
    }


    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf() throws Exception {
        byte[] pdfBytes = refundService.generateRefundPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=refunds.pdf")
                .body(pdfBytes);
    }

    @GetMapping(value = "/export/pdf/tri", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdfByStatus(@RequestParam(required = false) String status) throws Exception {
        byte[] pdfBytes = refundService.generateRefundPdfByStatus(status);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=refunds.pdf")
                .body(pdfBytes);
    }

}
