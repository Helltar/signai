# signai

A chatbot for Signal that answers with OpenAI models.

## Commands

- `chat <text>` — ask the bot, e.g. `chat How are you?`
- `chatctx` — show the current dialog history
- `chatrm` — clear the dialog history

Replying directly to one of the bot's messages counts as `chat`, so the command can be left out.

## Installation

```bash
# the repo carries compose.yaml and the env template
git clone https://github.com/Helltar/signai.git && cd signai

# fill in at least OPENAI_API_KEY and SIGNAL_PHONE_NUMBER
cp .env.example .env
```

Every variable is described in [.env.example](.env.example).

### Signal account

The bot reaches Signal through [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api), which comes up
alongside it. That service needs a Signal account of its own, and nothing works until it has one.

The short path is linking an account you already have, as a second device:

1. Uncomment the `ports` lines under `signal-cli-rest-api` in [compose.yaml](compose.yaml).
2. Start that service on its own:

   ```bash
   docker compose up -d signal-cli-rest-api
   ```

3. Open <http://localhost:8080/v1/qrcodelink?device_name=signal-api> in a browser.
4. On the phone holding the account: **Settings → Linked devices → +**, and scan the QR code.
5. Comment the `ports` lines back. The bot reaches the service over the compose network, and nothing else needs to.
6. Start everything:

   ```bash
   docker compose up -d
   ```

## Profile Picture

The image ships a default avatar, used when `AVATAR_FILE` is unset. To use your own, mount it and point `AVATAR_FILE`
at it — the compose file has the mount ready to uncomment, and no rebuild is needed.

An `AVATAR_FILE` that cannot be read stops the bot on startup rather than quietly keeping the old picture. The startup
log names the one in effect either way.
