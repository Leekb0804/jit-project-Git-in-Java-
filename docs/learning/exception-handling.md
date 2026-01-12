# 🚨 예외 처리 전략: 왜 RuntimeException으로 감싸는가?

JIT 프로젝트에서는 `IOException`이나 `NoSuchAlgorithmException` 같은 **Checked Exception**을 `RuntimeException`으로 감싸서 던지는 패턴을 자주 사용합니다. 그 이유와 장점을 정리합니다.

## 1. 불필요한 전파 방지 (Avoid Pollution)
Checked Exception을 그대로 던지면(`throws`), 해당 메서드를 호출하는 상위 메서드들도 줄줄이 `throws`를 선언하거나 `try-catch`를 해야 합니다.

*   **Before (Checked Exception):**
    ```java
    // 상위 로직까지 지저분해짐
    public void commit() throws IOException {
        storage.save(blob);
    }
    ```

*   **After (Unchecked Exception):**
    ```java
    // 호출하는 쪽은 깔끔함
    public void commit() {
        storage.save(blob);
    }
    ```

## 2. 복구 불가능한 에러 (Unrecoverable Error)
파일 저장 실패(디스크 꽉 참, 권한 없음)나 해시 알고리즘 오류 등은 **코드 레벨에서 즉시 복구할 수 없는 경우**가 대부분입니다.

이런 경우 억지로 `catch`를 해서 흐름을 이어가기보다는, **빠르게 예외를 터뜨려(Fail Fast)** 프로그램을 중단시키거나 최상위 레벨에서 에러 메시지를 출력하는 것이 더 안전합니다.

## 3. 원인 보존 (Exception Chaining)
단순히 감싸기만 하는 것이 아니라, **원래 발생한 예외(Root Cause)**를 반드시 포함해야 합니다.

```java
try {
    // ...
} catch (IOException e) {
    // 'e'를 두 번째 인자로 넘겨주어 스택 트레이스를 보존함
    throw new RuntimeException("Failed to save object", e);
}
```

이렇게 하면 디버깅 시 "저장 실패"라는 사실뿐만 아니라, "진짜 원인(Caused by)"이 무엇인지까지 추적할 수 있습니다.

## 💡 Future Improvement
프로젝트가 커지면 단순 `RuntimeException` 대신, 의미가 명확한 커스텀 예외(예: `GitStorageException`)를 정의하여 사용하는 것을 고려해야 합니다.