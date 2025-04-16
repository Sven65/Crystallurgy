# 💎 Crystallurgy

**Crystallurgy** is a mid-to-late-game Minecraft tech mod focused on ore synthesis through the power of **RF energy** and **catalyst crystals**. Harness raw matter, stabilize it with powerful crystals, and shape it into precious materials — all through cutting-edge crystallurgic technology.

---

## 🚀 Mod Goals

- Introduce a **unique, modular ore generation system**
- Use **degradable catalyst crystals** to define synthesis outputs
- Integrate with **RF-based power networks** (via LibEnergy or similar)
- Encourage **automation**, **exploration**, and **progression**
- Remain **balanced and configurable** for modpacks

---

## 🔬 Core Concepts

### ⚙️ Crystal Synthesizer
A powerful, configurable machine that synthesizes ores using:
- **Raw material** (e.g., coal, cobblestone)
- **Catalyst crystal** (defines the output type)
- **RF energy** (power requirement per operation)

### 💎 Catalyst Crystals
Custom items with durability that degrade over time:
| Name               | Output            | Uses | Crafted With                            |
|--------------------|-------------------|------|------------------------------------------|
| Diamond Resonator  | 1 Diamond         | 128  | Obsidian, Blaze Powder, End Crystal Shard |
| Iron Matrix Core   | 4 Iron Ingots     | 256  | Iron Block, Redstone, Quartz             |
| Embergold Seed     | 2 Gold Ingots     | 192  | Gold Nugget, Glowstone, Magma Cream      |
| Witherbit Node     | 1 Netherite Scrap | 32   | Wither Rose, Nether Star Fragment        |

Crystals may also be found as **rare loot**, **mob drops**, or **crafted with exotic materials**.

### ⚡ RF Energy
- Machines require RF to operate.
- Internal buffer (e.g., 100,000 RF)
- Per-operation cost (e.g., 20,000 RF per diamond synthesis)
- Energy accepted from any RF-compatible source

---

## 🛠 Planned Features

### ✅ MVP Implementation
- [ ] Basic mod setup with Fabric + LibEnergy
- [ ] `CrystalSynthesizerBlock` + BlockEntity + ScreenHandler
- [ ] Custom `CatalystCrystalItem` (with durability)
- [ ] RF energy storage + consumption
- [ ] Inventory slots: raw input, catalyst input, output
- [ ] Ticking + processing logic
- [ ] Example crystal types + recipes
- [ ] Configurable energy cost, processing time, durability

### 🔄 Automation Support
- [ ] Hopper + pipe compatibility
- [ ] Energy cable support (RF)
- [ ] Auto output toggle

### 💻 GUI
- [ ] Progress bar
- [ ] Energy bar
- [ ] Catalyst durability
- [ ] Slots for input/output

### 🧪 Advanced Plans
- [ ] Machine upgrades (faster processing, better yield, efficiency)
- [ ] Crystal instability / overheating effects
- [ ] Rare crystal drops from bosses or dungeons
- [ ] Multi-block synthesizer structure
- [ ] Integration with other tech mods (optional)

---

## 🧠 Design Philosophy

- No infinite resources: synthesis always costs **energy, materials, and durability**
- Players must invest in **power infrastructure** and **rare crafting components**
- Expandable: future support for **data-driven crystal types** and recipes
- Clean, immersive tech aesthetic — blending futuristic science with mystical resonance

---

## 💬 Feedback & Contributions

Suggestions? Want to contribute? Open an issue or PR on GitHub!  
Balancing feedback is especially welcome as the mod evolves.

---

Created with 💎 by Mackan
