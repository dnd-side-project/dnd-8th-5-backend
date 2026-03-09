# Agent Team Tools Reference

## Prerequisites

Enable agent teams via settings.json:

```json
{
  "env": {
    "CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS": "1"
  }
}
```

## Core Tools

### TeamCreate

Create a new team. Creates team config + task list.

```
TeamCreate(team_name="my-team", description="Working on feature X")
```

- Creates `~/.claude/teams/{team-name}/config.json`
- Creates `~/.claude/tasks/{team-name}/`

### TeamDelete

Remove team and task directories. Fails if active members remain.

```
TeamDelete()
```

Always shutdown all teammates first, then delete.

### Agent (with team_name)

Spawn a teammate into a team.

```
Agent(
  name="researcher",
  team_name="my-team",
  subagent_type="general-purpose",
  prompt="Research the auth module...",
  mode="auto"  # or "plan" for plan approval required
)
```

Key parameters:
- `name`: Human-readable name (used for messaging and task assignment)
- `team_name`: Team to join
- `subagent_type`: Agent type (determines available tools)
- `mode`: "plan" requires leader approval before implementation
- `isolation`: "worktree" for isolated git worktree

### SendMessage

Communication between team members.

**Direct message:**
```
SendMessage(type="message", recipient="researcher", content="...", summary="Brief summary")
```

**Broadcast (use sparingly - expensive):**
```
SendMessage(type="broadcast", content="...", summary="Critical update")
```

**Shutdown request:**
```
SendMessage(type="shutdown_request", recipient="researcher", content="Task complete")
```

**Shutdown response (teammate approves):**
```
SendMessage(type="shutdown_response", request_id="abc-123", approve=true)
```

**Plan approval:**
```
SendMessage(type="plan_approval_response", request_id="abc-123", recipient="researcher", approve=true)
```

### Task Tools

**TaskCreate** - Create tasks for the shared task list:
```
TaskCreate(subject="Implement auth module", description="...", activeForm="Implementing auth")
```

**TaskUpdate** - Assign, update status, set dependencies:
```
TaskUpdate(taskId="1", owner="researcher", status="in_progress")
TaskUpdate(taskId="2", addBlockedBy=["1"])  # task 2 waits for task 1
```

**TaskList** - View all tasks and their status.

**TaskGet** - Get full details of a specific task.

## Display Modes

| Mode | Setting | Requirements |
|------|---------|--------------|
| `in-process` | Default, all in one terminal | None |
| `tmux` | Split panes | tmux or iTerm2 + it2 CLI |
| `auto` | Uses tmux if available | tmux (optional) |

Configure in settings.json:
```json
{ "teammateMode": "in-process" }
```

Or per-session: `claude --teammate-mode in-process`

### In-Process Navigation

- `Shift+Down`: Cycle through teammates
- `Enter`: View teammate's session
- `Escape`: Interrupt current turn
- `Ctrl+T`: Toggle task list

## Permissions

Teammates inherit leader's permission settings. Individual modes can be changed after creation but not at spawn time.

## Hooks for Quality Gates

- `TeammateIdle`: Runs when teammate goes idle. Exit code 2 sends feedback and keeps teammate working.
- `TaskCompleted`: Runs when task marked complete. Exit code 2 prevents completion and sends feedback.
