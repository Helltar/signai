signai
------

### Prerequisites

Install and configure [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api).

### Docker

```bash
docker run -d \
  --name signai \
  --restart unless-stopped \
  -e BOT_NAME=signai \
  -e BOT_USERNAME=signai \
  -e OPENAI_API_KEY=sk-proj-qwerty \
  -e CHAT_SYSTEM_PROMPT="You are in a Signal group chat." \
  -e GPT_MODEL=gpt-5.2 \
  -e REQUESTS_PER_USER_PER_HOUR=10 \
  -e SIGNAL_API_URL=http://signal-cli-rest-api:8080 \
  -e SIGNAL_PHONE_NUMBER=+380980000000 \
  ghcr.io/helltar/signai:latest
```

| Variable | Description |
| --- | --- |
| `BOT_NAME` | Visible Signal profile name for the bot account |
| `BOT_USERNAME` | Signal username to set for the bot account |
| `OPENAI_API_KEY` | API key used for OpenAI requests |
| `CHAT_SYSTEM_PROMPT` | System message injected into each new dialog context |
| `GPT_MODEL` | OpenAI model name used for chat completions |
| `REQUESTS_PER_USER_PER_HOUR` | Per-user limit for `chat` requests. Defaults to `30` |
| `SIGNAL_API_URL` | URL to your `signal-cli-rest-api` service |
| `SIGNAL_PHONE_NUMBER` | Bot account phone number in Signal |

### Bot Commands

- `chat <text>`: Ask the bot (example: `chat How are you?`)
- `chatctx`: Show current dialog history
- `chatrm`: Clear dialog history

**Tip**: If you reply directly to a bot message, it will be processed as a `chat` command automatically.
