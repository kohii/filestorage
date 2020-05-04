package com.github.kohii.filestorage.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;

/**
 * ストレージ上のファイルの中身を表すオブジェクト。
 */
public interface FileStorageObject {

  /**
   * ファイルの内容を{@link InputStream}で取得する。
   *
   * @return {@link InputStream}
   */
  InputStream getInputStream();

  /**
   * 与えられたパスにファイルを書き込む。
   *
   * @param path パス
   */
  default void writeTo(Path path) {
    try (InputStream inputStream = getInputStream()) {
      Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * ファイルの内容を{@code byte[]}で取得する。
   *
   * @return byteの配列
   */
  default byte[] getByteArray() {
    try (InputStream inputStream = getInputStream()) {
      return IOUtils.toByteArray(inputStream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
