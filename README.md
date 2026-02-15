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

Replace the example values with your own data:

- `BOT_NAME`, `BOT_USERNAME` - your desired bot name and username
- `OPENAI_API_KEY` - your OpenAI API key
- `CHAT_SYSTEM_PROMPT` - the system prompt for the chat
- `GPT_MODEL` - the GPT model to use
- `REQUESTS_PER_USER_PER_HOUR` - per-user hourly request limit for the chat command
- `SIGNAL_API_URL` - the URL of your running `signal-cli-rest-api` instance
- `SIGNAL_PHONE_NUMBER` - the phone number linked to bot Signal account

### Usage

- `chat` - Chat with the bot (Example: `chat How are you?`)
- `chatctx` - View dialogue history
- `chatrm` - Clear history
