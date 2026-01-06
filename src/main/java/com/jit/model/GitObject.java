package com.jit.model;

/**
 * Git의 객체(GitObject)가 준수해야 하는 최상위 인터페이스
 * <p><b>1. Sealed Hierarchy를 사용한 이유:</b>
 * {@code sealed}를 통해 무분별한 상속을 방지하고,
 * Java 17+의 패턴 매칭을 활용해 컴파일 타임에 모든 타입을 체크하여
 * 타입 안정성을 확보.</p>
 */
public sealed interface GitObject permits AbstractGitObject {

  /**
   * 객체의 고유 타입을 반환.
   * @return "blob", "tree", "commit", "tag" 중 하나
   */
  String getType();

  /**
   * 객체의 순수 본문 데이터(Payload)를 반환.
   * <p><b>주의:</b> 이 데이터는 헤더를 포함하지 않은 순수 내용.</p>
   * @return 본문 바이트 배열
   */
  byte[] getContent();

  /**
   * 헤더를 포함한 전체 데이터의 SHA-1 해시값(ID)을 계산.
   * @return 40글자의 16진수 SHA-1 해시 문자열
   * <p><b>1. 40글자인 이유:</b>
   * 20바이트 크기의 바이트 배열을 반환하는데 1바이트는 8비트이므로 16진수 2개로 표현할 수 있기 때문</p>
   */
  ObjectId getId();

  /**
   * 데이터를 Git 공식 규격 {@code [type] [size]\0[content]} 형태로 직렬화.
   * "[Format] Git 객체 직렬화와 바이너리 프로토콜" 문서 참고
   * @return 직렬화된 바이트 배열
   */
  byte[] serialize();
}
