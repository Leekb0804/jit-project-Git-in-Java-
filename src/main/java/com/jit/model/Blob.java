package com.jit.model;

/**
 * 파일의 내용을 저장하는 Git 객체 (Binary Large Object).
 * <p><b>특징:</b>
 * 1. 파일의 이름이나 메타데이터(권한, 생성일 등)는 저장하지 않고, 오직 <b>"파일의 내용"</b>만 저장합니다.
 * 2. 내용이 같은 파일은 파일명이 달라도 동일한 Blob으로 취급(동일한 ID)됩니다.</p>
 */
public final class Blob extends AbstractGitObject {

  /**
   * Blob 객체 생성.
   * <p>파일의 원본 바이트 배열을 받아 상위 클래스에 전달합니다.
   * 상위 클래스에서 자동으로 "blob" 헤더를 붙이고 ID를 계산합니다.</p>
   * @param content 파일의 원본 내용 (byte array)
   */
  public Blob(byte[] content) {
    super("blob", content);
  }

  @Override
  public String getType() {
    return "blob";
  }
}
