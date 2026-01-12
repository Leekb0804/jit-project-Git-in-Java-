package com.jit.storage;

import com.jit.model.Blob;
import com.jit.model.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectStorageTest {

    @TempDir
    Path tempGitDir; // JUnit이 만들어주는 임시 폴더

    @Test
    @DisplayName("Git 객체를 저장하면 올바른 경로에 파일이 생성되어야 한다")
    void shouldSaveGitObjectToFile() throws IOException {
        // given
        // 임시 폴더 안에 .git/objects 구조를 흉내냄
        // 실제로는 ObjectStorage가 루트 경로를 받아서 처리하도록 설계할 예정
        ObjectStorage storage = new ObjectStorage(tempGitDir.toString());
        
        Blob blob = new Blob("hello world".getBytes(StandardCharsets.UTF_8));
        ObjectId id = blob.getId();

        // when
        storage.save(blob);

        // then
        // 예상 경로: tempDir/objects/ab/cde123...
        String dirName = id.getDirectoryName();
        String fileName = id.getFileName();
        
        File expectedFile = tempGitDir.resolve("objects")
                                      .resolve(dirName)
                                      .resolve(fileName)
                                      .toFile();

        assertTrue(expectedFile.exists(), "객체 파일이 생성되어야 합니다");
        assertTrue(expectedFile.length() > 0, "파일 내용이 비어있지 않아야 합니다");
    }
}
