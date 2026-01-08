# ☕ Java I/O와 NIO (File vs Path)

자바에서 파일을 다루는 방식은 크게 Legacy IO (`java.io`)와 Modern NIO (`java.nio`) 두 가지로 나뉩니다. JIT 프로젝트는 최신 표준인 **NIO** 방식을 따릅니다.

## 1. IO vs NIO 비교

| 구분 | IO (`java.io`) | NIO (`java.nio`) |
| :--- | :--- | :--- |
| **출시** | Java 1.0 (1996년) | Java 1.4 (2002년) / Java 7 (NIO.2, 2011년) |
| **핵심 클래스** | `File` | `Path`, `Files` |
| **방식** | 스트림(Stream) 방식 | 버퍼(Buffer) 및 채널(Channel) 방식 |
| **특징** | 블로킹(Blocking)만 지원 | 넌블로킹(Non-blocking) 지원 가능 |
| **경로 처리** | 운영체제 종속적일 수 있음 | 운영체제 독립적이고 유연함 |

> **요약:** 단순히 파일을 읽고 쓰는 작업에서는 NIO의 `java.nio.file` 패키지가 훨씬 강력하고 안전한 기능을 제공합니다.

## 2. File vs Path

### 👴 `java.io.File` (구형)
*   파일의 **경로**와 **파일 시스템 작업(생성, 삭제)**이 하나의 클래스에 섞여 있습니다.
*   에러 처리가 미흡합니다. (실패 시 `false`만 반환하는 경우가 많음)
*   심볼릭 링크(Symbolic Link) 등을 제대로 다루기 어렵습니다.

### 👶 `java.nio.file.Path` (신형)
*   **`Path`**: 오직 **파일의 위치(경로)**만 나타내는 객체입니다. (데이터)
*   **`Files`**: 실제로 파일을 생성, 복사, 이동하는 **유틸리티 클래스**입니다. (행위)
*   역할이 명확하게 분리되어 있으며, 예외 처리가 강력합니다.

```java
// [구형] 문자열 결합으로 경로 생성 (위험)
File file = new File("C:\\git" + File.separator + "objects");

// [신형] 운영체제에 맞는 구분자를 알아서 처리 (안전)
Path path = Path.of("C:", "git", "objects");
```

## 3. 왜 `mkdir()`은 위험한가?

디렉토리를 생성할 때 구형 방식인 `File.mkdir()` 또는 `File.mkdirs()`를 사용하는 것은 디버깅 관점에서 좋지 않습니다.

### ❌ `File.mkdirs()`의 문제점
이 메서드는 성공하면 `true`, 실패하면 `false`를 반환합니다. **문제는 "왜" 실패했는지 알려주지 않는다는 점입니다.**

```java
File dir = new File("path/to/dir");
if (!dir.mkdirs()) {
    // 실패했다는 건 알지만...
    // 권한이 없어서? 디스크가 꽉 차서? 이미 파일이 있어서?
    // 알 방법이 없습니다. 디버깅이 매우 힘듭니다.
    System.out.println("생성 실패!"); 
}
```

### ✅ `Files.createDirectories()`의 장점
NIO 방식은 실패 시 **구체적인 예외(Exception)**를 던집니다.

```java
Path dir = Path.of("path/to/dir");
try {
    Files.createDirectories(dir);
} catch (FileAlreadyExistsException e) {
    // 이미 같은 이름의 파일(디렉토리 아님)이 존재함
} catch (AccessDeniedException e) {
    // 권한 부족
} catch (IOException e) {
    // 기타 IO 에러
}
```

또한 `Files.createDirectories()`는 **이미 디렉토리가 존재하면 에러 없이 그냥 넘어가는** 편리한 기능도 내장되어 있어, 굳이 `if (!exists)` 체크를 할 필요가 없습니다.

## 4. JIT 프로젝트 적용 가이드

우리 프로젝트에서는 다음과 같은 원칙으로 파일 시스템을 다룹니다.

1.  **경로 표현:** 무조건 `Path` 객체를 사용합니다. (`Path.of()`, `path.resolve()`)
2.  **파일 조작:** `Files` 클래스의 정적 메서드를 사용합니다. (`Files.createDirectories()`, `Files.exists()`)
3.  **스트림 생성:** `new FileInputStream` 대신 `Files.newInputStream()`을 사용합니다.
    *   **이유:** `Files` 방식은 실제 디스크 파일뿐만 아니라, 메모리 파일 시스템(테스트용)이나 ZIP 파일 내부의 경로 등 다양한 파일 시스템을 유연하게 지원하기 때문입니다.

```java
// 권장 코드 예시
Path path = rootPath.resolve("objects").resolve(dirName);
Files.createDirectories(path); // 안전한 생성
```