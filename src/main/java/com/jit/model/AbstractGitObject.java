package com.jit.model;

import com.jit.utils.HashUtil;
import java.nio.charset.StandardCharsets;

/**
 * 모든 Git 객체(Blob, Tree, Commit, Tag)가 상속받는 추상 클래스.
 * <p><b>1. 공통 상태 관리:</b>
 * 모든 Git 객체는 자신의 고유한 ID(SHA-1 해시)와 내용물(Content)을 가집니다.
 * 이를 상위 클래스에서 일괄 관리하여 중복 코드를 제거합니다.</p>
 * <p><b>2. 직렬화 로직 통합:</b>
 * Git 객체의 직렬화 방식({@code [type] [size]\0[content]})은 모든 타입에서 동일합니다.
 * 이 공통 로직을 이곳에 구현하여 일관성을 보장합니다.</p>
 */
public abstract sealed class AbstractGitObject implements GitObject permits Blob {
  protected final ObjectId id;
  protected final byte[] content;

  /**
   * Git 객체를 생성하고 ID를 계산.
   * <p>생성 시점에 헤더를 포함한 전체 데이터를 구성하여 SHA-1 해시(ID)를 즉시 계산하고 캐싱합니다.</p>
   * @param type 객체 타입 ("blob", "tree", "commit", "tag")
   * @param content 객체의 실제 내용물 (헤더 제외)
   */
  protected AbstractGitObject(String type, byte[] content) {
    this.content = content;
    // [중복 제거] 모든 Git 객체는 똑같은 방식으로 헤더를 붙임
    byte[] fullData = pack(type, content);
    // [성능 최적화] 생성 시점에 한 번만 해시 계산하여 캐싱
    this.id = HashUtil.calculate(fullData);
  }

  /**
   * 타입과 내용물을 합쳐 Git 표준 포맷의 바이트 배열을 생성.
   * @param type 객체 타입
   * @param content 객체 내용
   * @return {@code [type] [size]\0[content]} 형태의 바이트 배열
   */
  private byte[] pack(String type, byte[] content) {
    byte[] header = String.format("%s %d\0", type, content.length).getBytes(StandardCharsets.UTF_8);
    byte[] fullData = new byte[header.length + content.length];
    System.arraycopy(header, 0, fullData, 0, header.length);
    System.arraycopy(content, 0, fullData, header.length, content.length);
    return fullData;
  }

  @Override
  public ObjectId getId() { return id; }

  @Override
  public byte[] getContent() { return content; }

  /**
   * 객체를 Git 저장소에 저장할 수 있는 최종 바이너리 형태로 직렬화합니다.
   * <p><b>1. 역할 (The Envelope):</b>
   * 이 메서드는 모든 Git 객체가 공유하는 <b>"표준 포장 방식"</b>을 담당합니다.
   * 자식 클래스가 준비한 내용물({@code content})에 {@code [type] [size]\0} 헤더를 붙여
   * 저장소 엔진이 인식할 수 있는 최종 형태를 만듭니다.</p>
   *
   * <p><b>2. 자식 클래스와의 관계 (The Content):</b>
   * 자식 클래스(Blob, Commit 등)는 생성자에서 자신만의 방식대로 <b>"편지 내용(content)"</b>을 만들어 부모에게 전달합니다.
   * (예: Blob은 파일 원본, Commit은 메타데이터 문자열 등)
   * 하지만 그 내용을 최종적으로 포장하여 디스크에 쓸 준비를 하는 것은 이 메서드의 책임입니다.</p>
   *
   * @return {@code [type] [size]\0[content]} 형식의 완성된 바이트 배열
   */
  @Override
  public byte[] serialize() {
    return pack(getType(), content);
  }
}
