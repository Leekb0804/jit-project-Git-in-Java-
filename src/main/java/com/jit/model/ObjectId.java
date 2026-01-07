package com.jit.model;

public record ObjectId(
    byte[] bytes
) {
  /**
   * ObjectId를 생성.
   * @param bytes 20바이트 길이의 SHA-1 해시 배열
   * @throws IllegalArgumentException 배열이 20바이트가 아닌 경우 발생
   */
  public ObjectId {
    if (bytes.length != 20) {
      throw new IllegalArgumentException("SHA-1 hash must be 20 bytes");
    }

    // 배열 내부의 불변성을 유지하기 위해서
    // record 의 final 필드가 보호하는건 참조하는 주소지 주소 내부의 내용(값)이 아님.
    // 값만 복사한 새로운 배열을 bytes 에 할당
    bytes = bytes.clone();
  }

  /**
   * 바이트 배열을 16진수 문자열로 변환 (표현용)
   * @return 16진수 문자열
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  /**
   * Git 저장 경로(디렉토리) 반환
   * @return 앞의 두 글자
   */
  public String getDirectoryName() {
    return toString().substring(0, 2);
  }

  /**
   * Git 저장 파일 명 반환
   * @return 3번째 글자부터 끝까지
   */
  public String getFileName() {
    return toString().substring(2);
  }
}
