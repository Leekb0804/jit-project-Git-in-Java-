package com.jit.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectIdTest {

    @Test
    @DisplayName("20바이트 배열로 ObjectId가 정상 생성되어야 한다")
    void shouldCreateObjectIdWith20Bytes() {
        // given
        byte[] bytes = new byte[20];
        for (int i = 0; i < 20; i++) {
            bytes[i] = (byte) i;
        }

        // when
        ObjectId objectId = new ObjectId(bytes);

        // then
        assertNotNull(objectId);
        assertArrayEquals(bytes, objectId.bytes());
    }

    @Test
    @DisplayName("20바이트가 아니면 IllegalArgumentException이 발생해야 한다")
    void shouldThrowExceptionWhenLengthIsNot20() {
        // given
        byte[] shortBytes = new byte[19];
        byte[] longBytes = new byte[21];

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new ObjectId(shortBytes));
        assertThrows(IllegalArgumentException.class, () -> new ObjectId(longBytes));
    }

    @Test
    @DisplayName("16진수 문자열로 올바르게 변환되어야 한다")
    void shouldConvertToHexString() {
        // given
        // 0x00, 0x01, ... 0x13 (0~19)
        byte[] bytes = new byte[20];
        for (int i = 0; i < 20; i++) {
            bytes[i] = (byte) i;
        }
        ObjectId objectId = new ObjectId(bytes);

        // when
        String hexString = objectId.toString();

        // then
        // 000102030405060708090a0b0c0d0e0f10111213
        assertEquals("000102030405060708090a0b0c0d0e0f10111213", hexString);
    }

    @Test
    @DisplayName("디렉토리명(앞2글자)과 파일명(나머지)을 올바르게 분리해야 한다")
    void shouldSplitDirectoryAndFileName() {
        // given
        // "aa" + "bb..." 패턴을 만들기 위해
        byte[] bytes = new byte[20];
        bytes[0] = (byte) 0xaa;
        bytes[1] = (byte) 0xbb;
        // 나머지는 0
        ObjectId objectId = new ObjectId(bytes);

        // when
        String dirName = objectId.getDirectoryName();
        String fileName = objectId.getFileName();

        // then
        assertEquals("aa", dirName);
        assertEquals("bb000000000000000000000000000000000000", fileName);
    }
}
