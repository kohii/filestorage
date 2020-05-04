package com.github.kohii.filestorage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.github.kohii.filestorage.api.FileLocation;
import com.github.kohii.filestorage.api.FileStorageObject;
import com.github.kohii.filestorage.api.FileStorageObjectNotFoundException;
import com.github.kohii.filestorage.api.FileStorageService;
import org.apache.commons.io.FileUtils;

/**
 * S3でファイルを保管する{@link FileStorageService}の実装。
 */
public class S3FileStorageService implements FileStorageService {

  private final AmazonS3 s3Client;
  private final String bucketName;

  public S3FileStorageService(AmazonS3 s3Client, String bucketName) {
    this.s3Client = Objects.requireNonNull(s3Client);
    this.bucketName = Objects.requireNonNull(bucketName);
  }

  @Override
  public void putFile(FileLocation targetLocation, InputStream inputStream) {
    Path scratchFile = null;
    try (InputStream is = inputStream) {
      // InputStreamで直接アップロードしようとするとContentLengthをセットしなければならないので、一旦ファイルに書き出す
      // パフォーマンスを気にする場合はputFile(FileLocation, InputStream, int contentLength)とか用意してもいいかも
      scratchFile = Files.createTempFile("s3put", ".tmp");
      Files.copy(inputStream, scratchFile);
      putFile(targetLocation, scratchFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      if (scratchFile != null) {
        FileUtils.deleteQuietly(scratchFile.toFile());
      }
    }
  }

  @Override
  public void putFile(FileLocation targetLocation, Path localFile) {
    if (!Files.exists(localFile)) {
      throw new IllegalArgumentException(localFile + " does not exists.");
    }
    s3Client.putObject(new PutObjectRequest(bucketName, targetLocation.toString(), localFile.toFile()));
  }

  @Override
  public void deleteFile(FileLocation targetLocation) {
    s3Client.deleteObject(bucketName, targetLocation.toString());
  }

  @Override
  public FileStorageObject getFile(FileLocation fileLocation) {
    S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileLocation.toString()));
    if (s3Object == null) {
      throw new FileStorageObjectNotFoundException(fileLocation);
    }
    return new S3FileStorageObject(s3Object);
  }

  @Override
  public boolean exists(FileLocation fileLocation) {
    return s3Client.doesObjectExist(bucketName, fileLocation.toString());
  }

  private static class S3FileStorageObject implements FileStorageObject {
    private final S3Object s3Object;

    private S3FileStorageObject(S3Object s3Object) {
      this.s3Object = s3Object;
    }

    @Override
    public InputStream getInputStream() {
      return s3Object.getObjectContent();
    }
  }
}
