package com.example.refundwebdistibue;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class refundService {
    @Autowired
    private refundRepo refundRepository;

    public refund addRefund(refund refund) {
        return refundRepository.save(refund);
    }

    public List<refund> getAll() {
        return refundRepository.findAll();
    }

    public refund updateRefund(int id, refund newRefund) {
        Optional<refund> optionalRefund = refundRepository.findById(id);
        if (optionalRefund.isPresent()) {
            refund existingRefund = optionalRefund.get();
            existingRefund.setAmount(newRefund.getAmount());
            existingRefund.setStatus(newRefund.getStatus());
            existingRefund.setRequestDate(newRefund.getRequestDate());
            existingRefund.setProcessedDate(newRefund.getProcessedDate());
            return refundRepository.save(existingRefund);
        } else {
            return null;
        }
    }

    public String deleteRefund(int id) {
        if (refundRepository.findById(id).isPresent()) {
            refundRepository.deleteById(id);
            return "Refund deleted";
        } else {
            return "Refund not deleted";
        }
    }
    


    public byte[] generateRefundPdf() throws Exception {
        List<refund> refunds = refundRepository.findAll();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("table { width: 100%; border-collapse: collapse; }")
                .append("th, td { border: 1px solid black; padding: 8px; }")
                .append("</style></head><body>")
                .append("<h2>Liste des remboursements</h2>")
                .append("<table><tr><th>ID</th><th>Montant</th><th>Status</th><th>Date Demande</th><th>Date Traitement</th></tr>");

        for (refund r : refunds) {
            html.append("<tr>")
                    .append("<td>").append(r.getId()).append("</td>")
                    .append("<td>").append(r.getAmount()).append("</td>")
                    .append("<td>").append(r.getStatus()).append("</td>")
                    .append("<td>").append(r.getRequestDate()).append("</td>")
                    .append("<td>").append(r.getProcessedDate() != null ? r.getProcessedDate() : "").append("</td>")
                    .append("</tr>");
        }

        html.append("</table></body></html>");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html.toString(), null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }
    public byte[] generateRefundPdfByStatus(String status) throws Exception {
        List<refund> refunds;

        if (status != null && !status.isEmpty()) {
            refunds = refundRepository.findAll().stream()
                    .filter(r -> r.getStatus().equalsIgnoreCase(status))
                    .toList();
        } else {
            refunds = refundRepository.findAll();
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("table { width: 100%; border-collapse: collapse; }")
                .append("th, td { border: 1px solid black; padding: 8px; }")
                .append("</style></head><body>")
                .append("<h2>Remboursements")
                .append(status != null ? " - Statut: " + status.toUpperCase() : "")
                .append("</h2>")
                .append("<table><tr><th>ID</th><th>Montant</th><th>Statut</th><th>Date Demande</th><th>Date Traitement</th></tr>");

        for (refund r : refunds) {
            html.append("<tr>")
                    .append("<td>").append(r.getId()).append("</td>")
                    .append("<td>").append(r.getAmount()).append("</td>")
                    .append("<td>").append(r.getStatus()).append("</td>")
                    .append("<td>").append(r.getRequestDate()).append("</td>")
                    .append("<td>").append(r.getProcessedDate() != null ? r.getProcessedDate() : "").append("</td>")
                    .append("</tr>");
        }

        html.append("</table></body></html>");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html.toString(), null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }



}
