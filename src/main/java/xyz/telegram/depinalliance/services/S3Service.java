package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * @author holden on 04-Sep-2024
 */
@ApplicationScoped
public class S3Service {
  @Inject
  S3Client s3Client;

  @ConfigProperty(name = "bucket.name")
  String bucketName;

  @ConfigProperty(name = "folder.name")
  String folderName;

  public String uploadFile(String folderName, String keyName, FileUpload file) throws Exception {
    keyName = keyName + "." + getExtensionFile(file);
    File fileUpload = convertFileUploadToFile(keyName, file.uploadedFile().toFile());
    String fullName = this.folderName + "/" + folderName + "/" + keyName;
    PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fullName)
      .contentType(file.contentType()).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromFile(fileUpload));
    fileUpload.delete();
    return fullName;
  }

  private String getExtensionFile(FileUpload file) {
    return FilenameUtils.getExtension(file.fileName());
  }

  public String getFileType(FileUpload file) {
    return file.contentType();
  }

  public boolean validateFileType(FileUpload file, List<String> lstFileType) throws BusinessException {
    return validateFileType(getFileType(file), lstFileType);
  }

  public boolean validateFileType(String fileType, List<String> lstFileType) throws BusinessException {
    if (!lstFileType.contains(fileType)) {
      throw new BusinessException(ResponseMessageConstants.UPLOAD_WRONG_FORMAT);
    }
    return true;
  }

  private File convertFileUploadToFile(String name, File fileUpload) throws IOException {
    File convertFile = new File(name);
    FileOutputStream fos = new FileOutputStream(convertFile);
    byte[] bytes = Files.readAllBytes(fileUpload.toPath());
    fos.write(bytes);
    fos.close();
    return convertFile;
  }
}
