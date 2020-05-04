package com.github.kohii.filestorage.api;

import java.io.Serializable;
import java.util.Objects;

/**
 * ファイルストレージ上のファイルの場所。
 */
public class FileLocation implements Serializable {
  /**
   * パス。
   * "parent/child/file.txt"のような値を想定。
   */
  private final String value;

  private FileLocation(String value) {
    this.value = value.startsWith("/") ? value.substring(1) : value;
  }

  /**
   * 文字列から{@link FileLocation}のインスタンスを作成する。
   *
   * @param value パス
   * @return インスタンス
   */
  public static FileLocation of(String value) {
    return new FileLocation(value);
  }

  /**
   * 複数の文字列から{@link FileLocation}のインスタンスを作成する。
   * 各文字列は"/"で連結される。
   *
   * @param parts パスを構成する複数の文字列
   * @return インスタンス
   */
  public static FileLocation of(String... parts) {
    if (parts.length == 1) {
      return new FileLocation(parts[0]);
    }
    return new FileLocation(String.join("/", parts));
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FileLocation that = (FileLocation) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
