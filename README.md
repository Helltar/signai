# signai

A chatbot for Signal that answers with OpenAI models.

## Commands

- `chat <text>` — ask the bot, e.g. `chat How are you?`
- `chatctx` — show the current dialog history
- `chatrm` — clear the dialog history

Replying directly to one of the bot's messages counts as `chat`, so the command can be left out.

## Installation

```bash
mkdir signai && cd signai && \
  curl -fsSL \
    -o compose.yaml https://raw.githubusercontent.com/Helltar/signai/master/compose.yaml \
    -o .env https://raw.githubusercontent.com/Helltar/signai/master/.env.example
```

Set `OPENAI_API_KEY` and `SIGNAL_PHONE_NUMBER` in `.env`.

### Signal account

The bot reaches Signal through [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api), which runs
alongside it. Before starting the bot, link that service to the Signal account the bot will use.

Link the account as a second device:

1. Start a one-off instance with a temporary port bound to localhost:

   ```bash
   docker compose run --rm -p 127.0.0.1:8080:8080 signal-cli-rest-api
   ```

2. Open <http://localhost:8080/v1/qrcodelink?device_name=signal-api> in a browser.
3. On the phone holding the account: **Settings → Linked devices → +**, and scan the QR code.
4. Wait for `signal-api` to appear under **Linked devices**, then press `Ctrl+C` in the terminal. The one-off container
   is removed and its host port is closed; the linked account remains in the shared Docker volume.
5. Start everything:

   ```bash
   docker compose up -d
   ```

The bot uses the name, username, and profile picture configured for this account on the phone.
