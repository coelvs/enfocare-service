package com.enfocareservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.enfocareservice.entity.MedicalFileEntity;
import com.enfocareservice.model.MedicalFile;
import com.enfocareservice.model.mapper.MedicalFileMapper;
import com.enfocareservice.repository.MedicalFileRepository;

@Service
public class MedicalFileService {

	private static final Logger logger = LoggerFactory.getLogger(MedicalFileService.class);

	@Autowired
	private MedicalFileRepository medicalFileRepository;

	@Autowired
	private MedicalFileMapper medicalFileMapper;

	@Value("${medicalfile.dir:/app/data/images}")
	private String diagnosisDir;

	public List<String> getRecipentEmailsList(String doctorEmail) {
		return medicalFileRepository.findDistinctPatientEmailsByDoctorEmail(doctorEmail);
	}

	public void protectPdfFile(String paramFile) {
		File file = new File(paramFile);
		logger.info("🔒 Encrypting file: {} ELIF", file.getAbsolutePath());

		try (PDDocument document = Loader.loadPDF(file)) {
			AccessPermission ap = new AccessPermission();
			StandardProtectionPolicy spp = new StandardProtectionPolicy("123456", "123456", ap);
			spp.setEncryptionKeyLength(128);
			spp.setPermissions(ap);
			document.protect(spp);
			document.save(paramFile); // ✅ Save directly to the same file
			logger.info("✅ Document encrypted successfully ELIF");
		} catch (IOException e) {
			logger.error("❌ Error encrypting PDF file: {} ELIF", paramFile, e);
		}
	}

	public void uploadDiagnosisFile(String patientEmail, String doctorEmail, MultipartFile file, Long consultationId) {
		try {
			logger.info("📤 Uploading file for Patient: {} by Doctor: {} ELIF", patientEmail, doctorEmail);

			if (file == null || file.isEmpty()) {
				logger.error("❌ No file received! ELIF");
				return;
			}

			String modifiedEmail = patientEmail.replaceAll("@|\\.", "");
			String directoryPath = diagnosisDir + File.separator + modifiedEmail;
			File directory = new File(directoryPath);
			if (!directory.exists()) {
				boolean created = directory.mkdirs();
				logger.info("📂 Created directory: {} (Success: {}) ELIF", directoryPath, created);
			}

			String originalFilename = file.getOriginalFilename();
			String filePath = Paths.get(directoryPath, originalFilename).toString();
			Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
			logger.info("✅ File successfully saved to: {} ELIF", filePath);

			// ✅ Verify that file exists before storing metadata
			File savedFile = new File(filePath);
			if (!savedFile.exists()) {
				logger.error("❌ File saving failed, not storing in DB! ELIF");
				return;
			}

			String password = generatePassword();
			MedicalFileEntity medicalFileEntity = new MedicalFileEntity();
			medicalFileEntity.setPatientEmail(patientEmail);
			medicalFileEntity.setDoctorEmail(doctorEmail);
			medicalFileEntity.setFilePath(filePath);
			medicalFileEntity.setPassword(password);
			medicalFileEntity.setConsultationId(consultationId);

			MedicalFileEntity savedEntity = medicalFileRepository.save(medicalFileEntity);
			logger.info("✅ File metadata saved in database! File ID: {} ELIF", savedEntity.getId());

		} catch (IOException e) {
			logger.error("❌ Error uploading file for patient: {} ELIF", patientEmail, e);
		}
	}

	private String generatePassword() {
		return "generatedPassword";
	}

	public List<String> getFilePathsForPatient(String patientEmail) {
		return medicalFileRepository.findFilePathsByPatientEmail(patientEmail);
	}

	public Optional<MedicalFile> getFileByPatientAndFileName(String patientEmail, String fileName) {
		return medicalFileRepository.getFileByPatientAndFileName(patientEmail, fileName).map(medicalFileMapper::map); // ✅
																														// Convert
																														// Entity
																														// to
																														// Model
	}

	public Optional<MedicalFile> getFileById(Long fileId) {
		return medicalFileRepository.getFileById(fileId).map(medicalFileMapper::map); // ✅ Convert Entity to Model
	}

	public Resource loadFileAsResource(Long fileId) throws IOException {
		try {
			Optional<MedicalFileEntity> fileEntityOptional = medicalFileRepository.findById(fileId);
			if (fileEntityOptional.isPresent()) {
				Path filePath = Paths.get(fileEntityOptional.get().getFilePath()).normalize();
				Resource resource = new UrlResource(filePath.toUri());

				// ✅ Check if the file exists and is readable
				if (resource.exists() && resource.isReadable()) {
					logger.info("✅ Successfully loaded file: {} ELIF", filePath);

					// ✅ Now apply encryption after verifying file exists
					protectPdfFile(filePath.toString());

					return resource;
				} else {
					logger.error("❌ Unable to read file: {} ELIF", filePath);
					throw new IOException("File cannot be read: " + fileId);
				}
			} else {
				logger.error("❌ File entity not found for ID: {} ELIF", fileId);
				throw new IOException("File entity not found: " + fileId);
			}
		} catch (Exception e) {
			logger.error("❌ Error loading file as resource for ID: {} ELIF", fileId, e);
			throw new IOException("Error loading file: " + fileId, e);
		}
	}

	public List<MedicalFile> getFilesByConsultationId(Long consultationId) {
		logger.info("Fetching files for consultation ID: {} ELIF", consultationId);

		String baseUrl = "https://enfocare-service-production.up.railway.app/enfocare/medical-file/download/";

		return medicalFileRepository.findByConsultationId(consultationId).stream().map(medicalFileEntity -> {
			MedicalFile file = medicalFileMapper.map(medicalFileEntity);

			// ✅ Ensure fileUrl is set
			file.setFileUrl(baseUrl + file.getId());

			return file;
		}).collect(Collectors.toList());
	}

	public List<MedicalFile> getFilesByConsultationAndDoctor(Long consultationId, String doctorEmail) {
		logger.info("Fetching files for Consultation ID: {} and Doctor: {} ELIF", consultationId, doctorEmail);

		String baseUrl = "https://enfocare-service-production.up.railway.app/enfocare/medical-file/download/";

		return medicalFileRepository.findByConsultationIdAndDoctorEmail(consultationId, doctorEmail).stream()
				.map(medicalFileEntity -> {
					MedicalFile file = medicalFileMapper.map(medicalFileEntity);

					// ✅ Ensure fileUrl is set
					file.setFileUrl(baseUrl + file.getId());

					return file;
				}).collect(Collectors.toList());
	}

	public List<MedicalFile> getFilesByConsultationAndPatient(Long consultationId, String patientEmail) {
		logger.info("📄 Fetching files for Consultation ID: {} and Patient: {} ELIF", consultationId, patientEmail);

		String baseUrl = "https://enfocare-service-production.up.railway.app/enfocare/medical-file/download/";

		return medicalFileRepository.findByConsultationIdAndPatientEmail(consultationId, patientEmail).stream()
				.map(medicalFileEntity -> {
					MedicalFile file = medicalFileMapper.map(medicalFileEntity);

					// ✅ Ensure fileUrl is set
					file.setFileUrl(baseUrl + file.getId());

					return file;
				}).collect(Collectors.toList());
	}

}