package com.github.kohii.filestorage.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import com.github.kohii.filestorage.api.FileLocation;
import com.github.kohii.filestorage.api.FileStorageObject;
import com.github.kohii.filestorage.api.FileStorageObjectNotFoundException;
import com.github.kohii.filestorage.api.FileStorageService;
import org.apache.commons.io.IOUtils;

/**
 * ローカルのファイルストレージにファイルを保管する{@link FileStorageService}の実装。
 */
public class LocalFileStorageService implements FileStorageService {

  private final Path rootDirPath;

  public LocalFileStorageService(Path rootDirPath) {
    this.rootDirPath = Objects.requireNonNull(rootDirPath);
  }

  public LocalFileStorageService(String rootDir) {
    this(Paths.get(rootDir));
  }

  @Override
  public void putFile(FileLocation targetLocation, InputStream inputStream) {
    Path target = rootDirPath.resolve(targetLocation.toString());
    ensureDirectoryExists(target.getParent());

    try (InputStream is = inputStream) {
      Files.write(target, IOUtils.toByteArray(inputStream));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void putFile(FileLocation targetLocation, Path localFile) {
    Path target = rootDirPath.resolve(targetLocation.toString());
    ensureDirectoryExists(target.getParent());

    try {
      Files.copy(localFile, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void deleteFile(FileLocation targetLocation) {
    Path path = rootDirPath.resolve(targetLocation.toString());
    if (!Files.exists(path)) {
      return;
    }
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public FileStorageObject getFile(FileLocation fileLocation) {
    Path path = rootDirPath.resolve(fileLocation.toString());
    if (!Files.exists(path)) {
      throw new FileStorageObjectNotFoundException(fileLocation);
    }
    return new LocalFileStorageObject(path);
  }

  @Override
  public boolean exists(FileLocation fileLocation) {
    Path path = rootDirPath.resolve(fileLocation.toString());
    return Files.exists(path);
  }

  private void ensureDirectoryExists(Path directory) {
    if (!Files.exists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private static class LocalFileStorageObject implements FileStorageObject {
    private final Path path;

    private LocalFileStorageObject(Path path) {
      this.path = path;
    }

    @Override
    public InputStream getInputStream() {
      try {
        return Files.newInputStream(path);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
