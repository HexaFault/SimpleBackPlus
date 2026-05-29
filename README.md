# SimpleBackPlus

A lightweight Paper plugin that restores the classic `/back` command with **persistent, reliable location history**.  
SimpleBackPlus tracks teleports, deaths, and world changes so players can safely return to where they were — even after server restarts.

---

##  Features

- **Reliable `/back` command** that behaves exactly how players expect  
- **Persistent location history** stored across restarts  
- Tracks **teleports, deaths, world changes**, and plugin‑initiated moves  
- **Configurable history size** via `history.yml`  
- **Lightweight and fast** — minimal overhead for busy servers  
- Designed for **Paper / Purpur** servers  
- Zero dependencies — **drop‑in and go**

---

##  Installation

1. Download the latest release:  
   **https://github.com/HexaFault/SimpleBackPlus/releases/latest**
2. Place the `.jar` into your server’s `plugins/` folder  
3. Start or restart your server  
4. (Optional) Edit `history.yml` to customize history size

---

##  Commands

| Command | Description |
|--------|-------------|
| `/back` | Returns the player to their previous location |

---

##  Configuration

`history.yml` allows you to control how many locations are stored per player.

Example:

```yaml
max-history: 10

