package com.healthcare.controller.document;

import com.healthcare.dto.DocumentDTO;
import com.healthcare.models.Document;
import com.healthcare.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/documents")
public class HealthVaultController {

    @Autowired
    private DocumentService documentService;

//    @GetMapping("/my")
//    @PreAuthorize("hasRole('PATIENT')")
//    public String showVault(Model model, Principal principal) {
//        // Ensure username is available for the layout
//        model.addAttribute("username", principal.getName());
//        model.addAttribute("documents", documentService.getDocumentsForPatient(principal.getName()));
//
//        // Mock data for the UI - Replace with your DocumentService call
//        // List<Document> docs = documentService.findByPatientEmail(principal.getName());
//        // model.addAttribute("documents", docs);
//
//        return "dashboard/patient/documents";
//    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public String showVault(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("documents", documentService.getDocumentsForPatient(principal.getName()));
        return "dashboard/patient/documents";
    }



    // --- DOWNLOAD ACTION ---
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DocumentDTO doc = documentService.getFileById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getTitle() + "\"")
                .contentType(MediaType.parseMediaType(doc.getDocumentType()))
                .body(doc.getData()); // Byte array from DB or Storage
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'PROVIDER')")
    @ResponseBody
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long id) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            DocumentDTO doc = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER"))
                    ? documentService.getByConsentId(id)
                    : documentService.getFileById(id);

            if (doc == null || doc.getData() == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = (doc.getDocumentType() != null) ? doc.getDocumentType() : "application/pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getTitle() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(doc.getData());

        } catch (Exception e) {
            System.err.println("Error serving document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/upload")
    @PreAuthorize("hasRole('PATIENT')")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 @RequestParam("category") String category,
                                 Principal principal) throws IOException {

        documentService.saveDocument(file, category, principal.getName());
        return "redirect:/documents/my";
    }

}