package com.github.kohii.filestorage.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.kohii.filestorage.api.FileLocation;
import com.github.kohii.filestorage.api.FileStorageObject;
import com.github.kohii.filestorage.api.FileStorageObjectNotFoundException;
import com.github.kohii.filestorage.api.FileStorageService;
import org.apache.commons.io.IOUtils;

/**
 * メモリ上にファイルを保持する{@link FileStorageService}の実装。
 */
public class InMemoryFileStorageService implements FileStorageService {

  private final Map<FileLocation, byte[]> files = new ConcurrentHashMap<>();

  @Override
  public void putFile(FileLocation targetLocation, InputStream inputStream) {
    try (InputStream is = inputStream) {
      files.put(targetLocation, IOUtils.toByteArray(inputStream));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void putFile(FileLocation targetLocation, Path localFile) {
    try {
      byte[] bytes = Files.readAllBytes(localFile);
      files.put(targetLocation, bytes);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void deleteFile(FileLocation targetLocation) {
    files.remove(targetLocation);
  }

  @Override
  public FileStorageObject getFile(FileLocation fileLocation) {
    byte[] bytes = files.get(fileLocation);
    if (bytes == null) {
      throw new FileStorageObjectNotFoundException(fileLocation);
    }
    return new InMemoryFileStorageObject(bytes);
  }

  @Override
  public boolean exists(FileLocation fileLocation) {
    return files.containsKey(fileLocation);
  }

  private static class InMemoryFileStorageObject implements FileStorageObject {
    private final byte[] bytes;

    private InMemoryFileStorageObject(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(bytes);
    }
  }
}
