package com.jit.utils;

import com.jit.model.ObjectId;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    // static 메서드: 객체 생성 없이 HashUtil.calculate(...)로 바로 쓸 수 있게 함
    public static ObjectId calculate(byte[] data) {
        try {
            // 1. SHA-1 알고리즘을 수행할 도구(MessageDigest)를 가져옵니다.
            // "SHA-1" 문자열은 자바 표준 라이브러리에 정의된 알고리즘 이름입니다.
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            // 2. 입력받은 데이터(byte[])를 해싱합니다.
            // digest() 메서드는 데이터를 넣으면 20바이트 길이의 해시값을 반환합니다.
            byte[] hash = digest.digest(data);

            // 3. 계산된 20바이트 해시를 우리가 만든 ObjectId 객체로 포장해서 반환합니다.
            return new ObjectId(hash);

        } catch (NoSuchAlgorithmException e) {
            // 4. 예외 처리
            // 자바 환경에 SHA-1 알고리즘이 없으면 발생하는 에러인데,
            // 사실상 모든 자바 환경에 기본으로 있어서 발생할 확률은 거의 0%입니다.
            // 하지만 문법상 체크해야 하므로 RuntimeException으로 감싸서 던집니다.
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }
}