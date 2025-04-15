package com.ms.candidat.feedbackwebdistribue;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;






import javax.swing.text.Document;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class FeedBackService {

    @Autowired
    private FeedBackRepo feedBackRepo;

    public FeedBack addFeedback(FeedBack feedback) {
        feedback.setSubmissionDate(java.time.LocalDateTime.now());
        return feedBackRepo.save(feedback);
    }

    public List<FeedBack> getAll() {
        return feedBackRepo.findAll();
    }

    public FeedBack updateFeedback(Long id, FeedBack updated) {
        Optional<FeedBack> optional = feedBackRepo.findById(id);
        if (optional.isPresent()) {
            FeedBack existing = optional.get();
            existing.setDescription(updated.getDescription());
            existing.setSubmissionDate(updated.getSubmissionDate()); // facultatif
            return feedBackRepo.save(existing);
        } else {
            return null;
        }
    }

    public String deleteFeedback(Long id) {
        if (feedBackRepo.findById(id).isPresent()) {
            feedBackRepo.deleteById(id);
            return "Feedback supprimé";
        } else {
            return "Feedback non trouvé";
        }
    }




    public byte[] generatePdf() throws Exception {
        List<FeedBack> feedbacks = feedBackRepo.findAll();

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("table { width: 100%; border-collapse: collapse; }")
                .append("th, td { border: 1px solid #000; padding: 8px; }")
                .append("</style></head><body>");
        html.append("<h2>Liste des Feedbacks</h2>");
        html.append("<table><tr><th>ID</th><th>Date</th><th>Description</th></tr>");

        for (FeedBack fb : feedbacks) {
            html.append("<tr>")
                    .append("<td>").append(fb.getFeedbackId()).append("</td>")
                    .append("<td>").append(fb.getSubmissionDate()).append("</td>")
                    .append("<td>").append(fb.getDescription()).append("</td>")
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
