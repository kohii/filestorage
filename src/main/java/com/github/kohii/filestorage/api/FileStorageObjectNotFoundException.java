package com.github.kohii.filestorage.api;

/**
 * ファイルストレージ上のオブジェクトが見つからないことを表す例外。
 */
public class FileStorageObjectNotFoundException extends RuntimeException {

  public FileStorageObjectNotFoundException(FileLocation fileLocation) {
    super("File Not Found: " + fileLocation.toString());
  }
}
