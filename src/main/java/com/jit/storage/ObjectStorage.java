package com.jit.storage;

import com.jit.model.GitObject;
import com.jit.model.ObjectId;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;

public class ObjectStorage {
  private final Path rootPath;

  public ObjectStorage(String rootPath) {
    this.rootPath = Path.of(rootPath);
  }

  /**
   * Git 객체를 저장소에 저장.
   * <p>1. 객체를 직렬화 (헤더 + 내용)</p>
   * <p>2. SHA-1 해시로 경로 생성 (.git/objects/ab/cde...)</p>
   * <p>3. Zlib 압축하여 파일 쓰기</p>
   * @param object 저장할 Git 객체
   */
  public void save(GitObject object) {
    ObjectId id = object.getId();
    String dirName = id.getDirectoryName();
    String fileName = id.getFileName();

    // .git/objects/ab/
    Path directory = rootPath.resolve("objects").resolve(dirName);

    try {
      // 디렉토리가 없으면 생성 (이미 있으면 무시함, mkdirs보다 안전)
      Files.createDirectories(directory);

      // .git/objects/ab/cde...
      Path filePath = directory.resolve(fileName);

      // 파일 쓰기 (CREATE, TRUNCATE_EXISTING 옵션이 기본값)
      try (OutputStream fos = Files.newOutputStream(filePath);
           DeflaterOutputStream dos = new DeflaterOutputStream(fos)) {
      
        dos.write(object.serialize());
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to save object: " + id, e);
    }
  }
}
