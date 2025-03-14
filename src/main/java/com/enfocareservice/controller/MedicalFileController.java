package com.enfocareservice.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections; // ✅ Added for empty lists
import java.util.List;
import java.util.Optional; // ✅ Added for optional values

import org.slf4j.Logger; // ✅ Added for logging
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.enfocareservice.model.MedicalFile;
import com.enfocareservice.service.MedicalFileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/enfocare/medical-file")
public class MedicalFileController {

	private static final Logger logger = LoggerFactory.getLogger(MedicalFileController.class);

	@Autowired
	private MedicalFileService medicalFileService;

	/**
	 * Upload a diagnosis file and associate it with a consultation.
	 */

	@PostMapping("/upload/{patientEmail}/{doctorEmail}/{consultationId}")
	public ResponseEntity<String> handleDiagnosisFileUpload(@PathVariable String patientEmail,
			@PathVariable String doctorEmail, @PathVariable Long consultationId,
			@RequestParam("file") MultipartFile file) {

		logger.info("📤 File Upload Requested: {} for Patient: {} by Doctor: {} ELIF", file.getOriginalFilename(),
				patientEmail, doctorEmail);

		try {
			medicalFileService.uploadDiagnosisFile(patientEmail, doctorEmail, file, consultationId);
			logger.info("✅ File uploaded successfully: {} ELIF", file.getOriginalFilename());
			return ResponseEntity.ok("Diagnosis file uploaded successfully");
		} catch (Exception e) {
			logger.error("❌ Failed to upload file for Patient: {} by Doctor: {} ELIF", patientEmail, doctorEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload diagnosis file");
		}
	}

	/**
	 * Get a list of patient email associated with a doctor's records.
	 */

	@GetMapping("/patients/{doctorEmail}")
	public ResponseEntity<List<String>> getPatientEmails(@PathVariable String doctorEmail) {
		try {
			List<String> patientEmails = medicalFileService.getRecipentEmailsList(doctorEmail);
			return patientEmails.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
					: ResponseEntity.ok(patientEmails);
		} catch (Exception e) {
			logger.error("❌ Failed to retrieve patient emails for Doctor: {} ELIF", doctorEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Retrieve file paths for a specific patient.
	 */
	@GetMapping("/files/{patientEmail}")
	public ResponseEntity<List<String>> getPatientFiles(@PathVariable String patientEmail) {
		try {
			List<String> patientFiles = medicalFileService.getFilePathsForPatient(patientEmail);
			return patientFiles.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
					: ResponseEntity.ok(patientFiles);
		} catch (Exception e) {
			logger.error("❌ Failed to retrieve files for Patient: {} ELIF", patientEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Points to the storage where file will be stored.
	 */
	@GetMapping("/download/{patientEmail}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFileByPatient(@PathVariable String patientEmail,
			@PathVariable String fileName) {
		try {
			// ✅ Fetch from DB instead of assuming storage path
			Optional<MedicalFile> fileOptional = medicalFileService.getFileByPatientAndFileName(patientEmail, fileName);
			if (fileOptional.isEmpty()) {
				logger.error("❌ No file found for Patient: {} with name: {} ELIF", patientEmail, fileName);
				return ResponseEntity.notFound().build();
			}

			MedicalFile file = fileOptional.get();
			Path filePath = Paths.get(file.getFilePath()).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists()) {
				logger.error("❌ File not found at path: {} ELIF", filePath);
				return ResponseEntity.notFound().build();
			}

			logger.info("📥 Downloading file: {} for Patient: {} ELIF", fileName, patientEmail);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			logger.error("❌ Error downloading file: {} for Patient: {} ELIF", fileName, patientEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Download a file using its ID.
	 */
	@GetMapping("/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, HttpServletRequest request) {
		try {
			Resource resource = medicalFileService.loadFileAsResource(fileId);

			String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			logger.info("📥 Downloading encrypted file with ID: {} ELIF", fileId);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			logger.error("❌ Error downloading encrypted file with ID: {} ELIF", fileId, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/consultation/{consultationId}/doctor/{doctorEmail}")
	public ResponseEntity<List<MedicalFile>> getFilesByConsultationAndDoctor(@PathVariable Long consultationId,
			@PathVariable String doctorEmail) {
		try {
			List<MedicalFile> files = medicalFileService.getFilesByConsultationAndDoctor(consultationId, doctorEmail);
			if (files.isEmpty()) {
				logger.warn("⚠️ No files found for Consultation ID: {} and Doctor: {} ELIF", consultationId,
						doctorEmail);
			}
			return ResponseEntity.ok(files);
		} catch (Exception e) {
			logger.error("❌ Failed to fetch files for Consultation ID: {} and Doctor: {} ELIF", consultationId,
					doctorEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		}
	}

	@GetMapping("/consultation/{consultationId}/patient/{patientEmail}")
	public ResponseEntity<List<MedicalFile>> getFilesByConsultationAndPatient(@PathVariable Long consultationId,
			@PathVariable String patientEmail) {
		try {
			List<MedicalFile> files = medicalFileService.getFilesByConsultationAndPatient(consultationId, patientEmail);
			if (files.isEmpty()) {
				logger.warn("⚠️ No files found for Consultation ID: {} and Patient: {} ELIF", consultationId,
						patientEmail);
			}
			return ResponseEntity.ok(files);
		} catch (Exception e) {
			logger.error("❌ Failed to fetch files for Consultation ID: {} and Patient: {} ELIF", consultationId,
					patientEmail, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		}
	}

}