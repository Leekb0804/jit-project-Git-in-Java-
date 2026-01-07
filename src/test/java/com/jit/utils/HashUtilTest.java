package com.jit.utils;

import com.jit.model.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {

    @Test
    @DisplayName("문자열 'hello'의 SHA-1 해시가 정확해야 한다")
    void shouldCalculateCorrectHashForHello() {
        // given
        byte[] input = "hello".getBytes(StandardCharsets.UTF_8);
        // known SHA-1 for "hello"
        String expectedHash = "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d";

        // when
        ObjectId objectId = HashUtil.calculate(input);

        // then
        assertEquals(expectedHash, objectId.toString());
    }

    @Test
    @DisplayName("빈 바이트 배열의 SHA-1 해시도 정확해야 한다")
    void shouldCalculateCorrectHashForEmptyInput() {
        // given
        byte[] input = new byte[0];
        // known SHA-1 for empty string
        String expectedHash = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

        // when
        ObjectId objectId = HashUtil.calculate(input);

        // then
        assertEquals(expectedHash, objectId.toString());
    }
}
