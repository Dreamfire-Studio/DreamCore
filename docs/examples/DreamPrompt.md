# DreamPrompt — Interactive Chat Prompts

DreamPrompt is a flexible system for running interactive, single-step chat prompts with **start**, **response**, **restart**, and **end** events. It wraps the Bukkit `Conversation` API in a much simpler developer-friendly interface.

---

## Why Use DreamPrompt?

* 🗨️ Easy player input collection via chat.
* 🔄 Automatic events for lifecycle stages (start, response, restart, end).
* 🧹 Safe cleanup when prompts are abandoned.
* 🪄 Built-in `Builder` for quick ad-hoc prompts.
* 🔧 Extensible with `IDreamPrompt` for reusable flows.

---

## Quick Start Example

```java
IDreamPrompt myPrompt = new IDreamPrompt() {
    @Override
    public String promptText(Player player) {
        return "&aType your favorite color:";
    }

    @Override
    public Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onResponseCallback() {
        return t -> t.player().sendMessage("You chose: " + t.input());
    }

    @Override
    public Consumer<DreamPromptTriplet<Player, String, ConversationContext>> onEndConversationCallback() {
        return t -> t.player().sendMessage("Prompt ended!");
    }
};

DreamPrompt.start(player, myPrompt, true);
```

---

## Core Concepts

### Prompt Lifecycle

A DreamPrompt goes through these stages:

1. **Started** → `PromptStartedEvent` fires.
2. **Response** → player sends input, `PromptResponseEvent` fires.
3. **Restart** → prompt repeats (if not ended), `PromptRestartEvent` fires.
4. **Ended** → conversation ends (normal or abandoned), `PromptEndedEvent` fires.

### End Condition

To end a prompt during response handling, set:

```java
context.setSessionData(DreamPrompt.END_KEY, true);
```

This will trigger `PromptEndedEvent` on the next cycle.

---

## Using the Builder

The `DreamPrompt.Builder` makes it quick to define inline prompts:

```java
DreamPrompt.builder()
    .promptText("&eSay something:")
    .onResponse(t -> t.player().sendMessage("You said: " + t.input()))
    .onEnd(t -> t.player().sendMessage("Done!"))
    .startConversation(player, true);
```

You can also add default session data:

```java
.addDefaultData("attempts", 0)
```

---

## API Reference

### `DreamPrompt.start(Player, IDreamPrompt, boolean)`

Launches a prompt using a reusable `IDreamPrompt` adapter.

* `player` → target player.
* `prompt` → adapter providing text and callbacks.
* `overrideExisting` → if false, ignores call when a conversation already exists.

### `DreamPrompt.Builder`

Ad-hoc builder for inline prompts.

* `promptText(String)` → sets displayed text.
* `onResponse(Consumer)` → callback when input is received.
* `onRestart(Consumer)` → callback when step repeats.
* `onEnd(Consumer)` → callback when prompt ends.
* `clearOnStart/Restart/End(boolean)` → clears chat at lifecycle stages.
* `startConversation(Player, boolean)` → launches prompt.
* `cancelConversation(Player)` → cancels an active prompt.

### `IDreamPrompt`

Interface for reusable prompt flows.

* `promptText(Player)` → text to show.
* `defaultData(Player)` → optional session data.
* `clearPlayerChatOnStart/Restart/End` → chat-clearing behavior.
* `onResponseCallback()` → required input handler.
* `onConversationRestartCallback()` → optional restart handler.
* `onEndConversationCallback()` → required end handler.

### Events

* `PromptStartedEvent` → fired before prompt begins (cancellable).
* `PromptResponseEvent` → fired after input received.
* `PromptRestartEvent` → fired when prompt repeats.
* `PromptEndedEvent` → fired when prompt ends (normal/abandoned).

---

## Example: Cancel Login Prompt

```java
DreamPrompt.builder()
    .promptText("&cType /cancel to abort")
    .onResponse(t -> {
        if (t.input().equalsIgnoreCase("/cancel")) {
            t.context().setSessionData(DreamPrompt.END_KEY, true);
        }
    })
    .onEnd(t -> t.player().sendMessage("Prompt closed."))
    .startConversation(player, true);
```

---

## Suggestions for Future Improvements

* ✅ **Multi-step prompts**: currently only single-step; adding multi-step flow support would help build wizards.
* ✅ **Async input processing**: allow `onResponse` to run off-thread for heavy logic.
* ✅ **Timeout support**: automatically end prompt after X seconds of inactivity.
* ✅ **Better formatting hooks**: allow MiniMessage or Adventure Components natively in prompt text.
* ✅ **Tab completion integration**: suggest possible responses inline.

---