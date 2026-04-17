package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.config.FileUploadConfig;
import hello.instrumenttrade.domain.Instrument;
import hello.instrumenttrade.domain.InstrumentImage;
import hello.instrumenttrade.dto.response.InstrumentImageResponse;
import hello.instrumenttrade.repository.InstrumentImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstrumentImageService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final InstrumentImageRepository imageRepository;
    private final FileUploadConfig fileUploadConfig;

    @Transactional
    public List<InstrumentImageResponse> uploadImages(Instrument instrument, List<MultipartFile> files) {
        List<InstrumentImage> savedImages = new ArrayList<>();
        List<InstrumentImage> existing = imageRepository.findByInstrumentId(instrument.getId());
        boolean hasThumbnail = existing.stream().anyMatch(img -> Boolean.TRUE.equals(img.getIsThumbnail()));
        int sortOrder = existing.size();

        for (MultipartFile file : files) {
            validateImageType(file);
            String imageUrl = storeFile(file, instrument.getId());
            boolean isThumbnail = !hasThumbnail;
            hasThumbnail = true;

            InstrumentImage image = InstrumentImage.of(instrument, imageUrl, isThumbnail, sortOrder++);
            savedImages.add(imageRepository.save(image));
        }

        return savedImages.stream().map(InstrumentImageResponse::from).toList();
    }

    @Transactional
    public void deleteImage(Long imageId, Long instrumentId) {
        InstrumentImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.IMAGE_NOT_FOUND));

        if (!image.getInstrument().getId().equals(instrumentId)) {
            throw new InstrumentTradeException(ErrorCode.IMAGE_NOT_FOUND);
        }

        deleteFile(image.getImageUrl());
        imageRepository.delete(image);
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InstrumentTradeException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
    }

    private String storeFile(MultipartFile file, Long instrumentId) {
        try {
            Path uploadPath = Paths.get(fileUploadConfig.getUploadDir(), String.valueOf(instrumentId))
                    .toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/instruments/" + instrumentId + "/" + fileName;
        } catch (IOException e) {
            throw new InstrumentTradeException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private void deleteFile(String imageUrl) {
        try {
            String relativePath = imageUrl.replaceFirst("^/uploads/instruments/", "");
            Path filePath = Paths.get(fileUploadConfig.getUploadDir(), relativePath)
                    .toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
