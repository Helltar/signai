signai
------

### Prerequisites

Install and configure [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api).

### Run with Docker

```bash
docker run -d \
  --name signai \
  --restart unless-stopped \
  -e BOT_NAME=signai \
  -e BOT_USERNAME=signai \
  -e OPENAI_API_KEY=sk-proj-qwerty \
  -e CHAT_SYSTEM_PROMPT="You are in a Signal group chat." \
  -e GPT_MODEL=gpt-5 \
  -e SIGNAL_API_URL=http://signal-cli-rest-api:8080 \
  -e SIGNAL_PHONE_NUMBER=+380980000000 \
  ghcr.io/helltar/signai:latest
```

Replace the example values with your own data:

- `BOT_NAME`, `BOT_USERNAME` - your desired bot name and username
- `OPENAI_API_KEY` - your OpenAI API key
- `CHAT_SYSTEM_PROMPT` - the system prompt for the chat
- `GPT_MODEL` - the GPT model to use
- `SIGNAL_API_URL` - the URL of your running `signal-cli-rest-api` instance
- `SIGNAL_PHONE_NUMBER` - the phone number linked to bot Signal account
