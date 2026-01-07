package com.jit.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BlobTest {

  @Test
  @DisplayName("파일 내용으로부터 Blob을 생성하고 SHA-1 해시를 올바르게 계산해야 한다")
  void createBlobAndCheckHash() {
    // given
    byte[] content = "hello world".getBytes(StandardCharsets.UTF_8);

    // when
    Blob blob = new Blob(content);

    // then
    // Git 공식 해시값: blob 11\0hello world -> SHA-1 해싱
    // 'hello world'의 git hash는 95d09f2b10159347eece71399a7e2e907ea3df4f
    assertEquals("95d09f2b10159347eece71399a7e2e907ea3df4f", blob.getId().toString());
  }

  @Test
  @DisplayName("Blob 객체는 올바른 Git 포맷으로 직렬화되어야 한다")
  void shouldSerializeCorrectly() {
    // given
    String text = "hello world";
    byte[] content = text.getBytes(StandardCharsets.UTF_8);
    Blob blob = new Blob(content);

    // when
    byte[] serialized = blob.serialize();

    // then
    // 예상되는 포맷: "blob 11\0hello world"
    String expectedHeader = "blob 11\0";
    byte[] headerBytes = expectedHeader.getBytes(StandardCharsets.UTF_8);
    
    // 전체 길이 확인
    assertEquals(headerBytes.length + content.length, serialized.length);

    // 앞부분(헤더) 확인
    byte[] actualHeader = new byte[headerBytes.length];
    System.arraycopy(serialized, 0, actualHeader, 0, headerBytes.length);
    assertArrayEquals(headerBytes, actualHeader);

    // 뒷부분(내용) 확인
    byte[] actualContent = new byte[content.length];
    System.arraycopy(serialized, headerBytes.length, actualContent, 0, content.length);
    assertArrayEquals(content, actualContent);
  }

  @Test
  @DisplayName("Blob의 타입은 'blob'이어야 한다")
  void shouldHaveCorrectType() {
    Blob blob = new Blob(new byte[0]);
    assertEquals("blob", blob.getType());
  }
}
