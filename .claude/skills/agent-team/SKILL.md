---
name: agent-team
description: >
  에이전트 팀 구성 및 조율 스킬. 여러 Claude Code 인스턴스를 팀으로 구성하여 병렬 작업을 수행한다.
  공유 태스크 목록, 에이전트 간 메시징, 파일 소유권 분리, 계획 승인 워크플로우를 지원한다.
  사용 시점: (1) "팀 구성", "에이전트 팀", "agent team" 키워드 감지 시,
  (2) 병렬 코드 리뷰, 멀티 레이어 기능 구현, 경쟁 가설 디버깅 등 독립적 병렬 작업이 필요할 때,
  (3) 3개 이상의 독립적 작업을 동시에 수행해야 할 때,
  (4) 팀원들이 서로 토론하고 결과를 공유해야 할 때.
  subagent와 달리, 팀원 간 직접 통신과 공유 태스크 목록을 통한 자체 조율이 가능하다.
---

# Agent Team Composition

## Prerequisites

settings.json에 실험적 기능 활성화 필수:

```json
{ "env": { "CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS": "1" } }
```

## When to Use Teams vs Subagents

| Condition | Use |
|-----------|-----|
| 워커 간 통신 불필요, 결과만 수집 | Subagent |
| 워커 간 토론/공유/도전 필요 | Agent Team |
| 동일 파일 편집, 순차 종속성 많음 | 단일 세션 or Subagent |
| 3+ 독립 영역을 병렬 탐색 | Agent Team |

## Workflow

### 1. Create Team

```
TeamCreate(team_name="feature-x", description="Implement feature X")
```

### 2. Create Tasks

태스크를 먼저 생성하고 종속성을 설정한다. 팀원당 5-6개 태스크가 적절하다.

```
TaskCreate(subject="Implement auth API", description="...")
TaskCreate(subject="Write auth tests", description="...")
TaskUpdate(taskId="2", addBlockedBy=["1"])  # tests wait for API
```

### 3. Spawn Teammates

각 팀원에게 명확한 역할과 파일 소유권을 부여한다. **파일 충돌 방지가 핵심.**

```
Agent(
  name="backend-dev",
  team_name="feature-x",
  subagent_type="general-purpose",
  prompt="You own src/api/ and src/services/. Implement the auth module...",
  mode="auto"
)
```

계획 승인이 필요한 위험한 작업:
```
Agent(name="architect", team_name="feature-x", ..., mode="plan")
```

### 4. Assign Tasks

```
TaskUpdate(taskId="1", owner="backend-dev")
```

또는 팀원들이 미할당 태스크를 자체 요청(claim)하도록 허용.

### 5. Monitor & Coordinate

- 팀원 메시지는 자동 전달됨 (폴링 불필요)
- 유휴 알림은 정상 동작 (오류 아님)
- 작업이 막히면 직접 메시지로 재지정

### 6. Shutdown & Cleanup

```
SendMessage(type="shutdown_request", recipient="backend-dev", content="All done")
# 모든 팀원 종료 후:
TeamDelete()
```

## Team Sizing Guide

| Task Count | Recommended Teammates |
|------------|----------------------|
| 3-6 | 1-2 |
| 6-15 | 3-5 |
| 15+ | 5+ (수익 감소 주의) |

3-5명이 대부분 워크플로우의 최적 범위.

## Critical Rules

1. **파일 충돌 방지**: 각 팀원에게 서로 다른 파일/디렉토리 소유권 할당
2. **리더만 정리**: TeamDelete는 반드시 리더가 실행 (팀원이 하면 상태 불일치)
3. **broadcast 자제**: 팀원 수에 비례해 비용 증가. 긴급 상황에만 사용
4. **팀원 대기**: 리더가 직접 구현하지 말고, 팀원 완료를 기다릴 것
5. **세션당 하나의 팀**: 새 팀 시작 전 현재 팀 정리 필수

## References

- **Use case patterns**: See [references/use-cases.md](references/use-cases.md) for detailed examples (parallel review, debugging, cross-layer implementation, etc.)
- **Tools API reference**: See [references/tools-reference.md](references/tools-reference.md) for tool parameters, display modes, hooks, and permissions
