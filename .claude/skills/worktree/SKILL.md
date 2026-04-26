---
name: worktree
description: 새 git worktree를 생성하고 해당 디렉토리로 이동하여 작업을 시작한다. "워크트리", "worktree 만들어", "새 워크트리", "/worktree", "worktree 생성", "분리된 브랜치에서 작업", "별도 워크트리에서" 같은 표현이 나오면 반드시 이 스킬을 사용한다. 사용자가 이름을 지정하지 않아도 자동 생성으로 처리할 수 있다.
---

# Worktree

새로운 git worktree를 만들고 해당 경로로 이동하여 사용자가 바로 작업에 들어갈 수 있게 한다.

## 실행 순서

1. **워크트리 생성** — `EnterWorktree` 도구를 호출한다.
   - 사용자가 이름을 지정한 경우 (예: `/worktree my-feature`) → `name` 파라미터로 그 값을 전달한다.
   - 이름 미지정 시 → 파라미터 없이 호출하여 자동 생성된 이름을 사용한다.

2. **이동** — 생성된 워크트리 경로로 `Bash` 도구를 사용해 `cd` 한다. `EnterWorktree`가 이미 세션 작업 디렉토리를 워크트리로 옮기지만, 셸 컨텍스트가 어긋날 수 있으므로 명시적으로 한 번 더 맞춰준다.

3. **최신 원격 main 동기화** — `EnterWorktree`는 현재 HEAD를 기준으로 새 브랜치를 만들기 때문에, 로컬 main이 origin/main 보다 뒤처져 있거나 푸시되지 않은 로컬 커밋이 섞여 있으면 PR이 의도치 않은 변경을 포함할 수 있다. 이를 막기 위해 워크트리 생성 직후 `Bash` 도구로 다음을 실행해 최신 `origin/main`에 맞춘다.
   ```bash
   git fetch origin main && git rebase origin/main
   ```
   - 새로 분기한 워크트리 전용 브랜치라 보통 충돌은 드물지만, 위 사유로 충돌이 발생할 수 있다. 충돌 시 자동으로 진행하지 말고 사용자에게 알리고 멈춘다.
   - 이 단계는 항상 수행한다. 사용자가 자연어로 "동기화 없이 진행해줘"처럼 명시적으로 거부할 때만 생략한다 (`EnterWorktree` 도구의 옵션이 아니라 스킬 차원의 사용자 지시 처리).

4. **`gradle.properties` 복사** — modutime 프로젝트라면 메인 저장소의 `gradle.properties`를 워크트리 루트로 항상 복사한다. 워크트리는 `.claude/worktrees/<name>/` 에 생성되므로 저장소 루트는 `../../../` 로 접근한다.
   ```bash
   cp ../../../gradle.properties ./gradle.properties \
     || cp ../../../gradle.properties.example ./gradle.properties
   ```
   - `gradle.properties` 자체는 `.gitignore` 처리되어 있어 신규 합류 머신에는 없을 수 있다. 그 경우 위 fallback 으로 `gradle.properties.example` 을 복사하고, 사용자에게 본인 머신의 JDK 17 경로(`org.gradle.java.home`)를 채워달라고 안내한다.

5. **요약 보고** — 사용자에게 다음 세 가지를 알려준다.
   - 워크트리 절대 경로
   - 새로 만들어진 브랜치 이름
   - rebase로 끌어온 최신 main 커밋 범위 (예: `fd12e5e..4e808f4`)

6. **다음 작업 묻기** — "이 워크트리에서 무슨 작업부터 시작할까요?" 같은 짧은 질문으로 사용자의 의도를 확인하고 거기서부터 이어간다.

## 왜 이 흐름인가

워크트리는 작업을 격리하는 수단이다. 만들기만 하고 사용자가 직접 `cd` 하도록 두면 메인 디렉토리에서 실수로 편집할 위험이 생긴다. 그래서 도구가 옮겨주는 위치에 셸도 맞추고, 경로/브랜치를 명시적으로 보여줘서 "지금 어디에 있는지"를 사용자가 인지하게 한다.

## 인자 처리 예

- 사용자: "/worktree" → `EnterWorktree({})` (이름 자동 생성)
- 사용자: "/worktree fix-login" → `EnterWorktree({ name: "fix-login" })`
- 사용자: "워크트리 만들어서 결제 모듈 리팩터 시작하자" → 이름 없이 생성한 뒤, 5단계에서 "결제 모듈 리팩터"를 작업 컨텍스트로 잡고 진행

## 주의

- 이미 워크트리 안에 있는 세션이라면 `EnterWorktree`가 거부한다. 그 경우 새 워크트리는 만들지 말고, 현재 워크트리에서 계속 작업할지 사용자에게 묻는다.
- `EnterWorktree`는 git 저장소 안에서만 동작한다. 이 프로젝트(`modutime`)는 git 저장소이므로 일반적으로 문제없다.
