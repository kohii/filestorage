package com.github.kohii.filestorage.api;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * ファイルストレージのファイルを操作するためのサービス。
 * ファイルを保管するkey-valueストアみたいなイメージ。
 * - key: FileLocation（ファイルの場所を表すオブジェクト）
 * - value: FileStorageObject（ファイルの中身を表すオブジェクト）
 */
public interface FileStorageService {

  /**
   * ファイルを保存する。
   *
   * @param fileLocation ストレージ上の保存先
   * @param inputStream  ファイルの内容
   */
  void putFile(FileLocation fileLocation, InputStream inputStream);

  /**
   * ファイルを保存する。
   *
   * @param fileLocation ストレージ上の保存先
   * @param localFile    保存するファイル
   */
  void putFile(FileLocation fileLocation, Path localFile);

  /**
   * ファイルを削除する。
   * ファイルが存在しない場合は何もしない。
   *
   * @param fileLocation ストレージ上の保存先
   */
  void deleteFile(FileLocation fileLocation);

  /**
   * ファイルを取得する。
   *
   * @param fileLocation ストレージ上の場所
   * @return FileObject
   * @throws FileStorageObjectNotFoundException ファイルが見つからない場合
   */
  FileStorageObject getFile(FileLocation fileLocation) throws FileStorageObjectNotFoundException;

  /**
   * ファイルが存在するかどうかを返す。
   *
   * @param fileLocation ストレージ上の場所
   * @return ファイルが存在するか
   */
  boolean exists(FileLocation fileLocation);
}
