# SparseMC-API

SparseMC-API is a Spigot plugin hoping to provide useful Kotlin APIs for Bukkit/Spigot.

## Modules

Module             | Description
------------------ | ---------------------------------------------
Core               | loads other modules and plugins
Commands           | an API to create commands
Scheduler          | an API for scheduled tasks and listeners
Configuration      | an API for configurations
Math               | a lot of math-related additions
Inventory          | an API for managing inventories
Persistent         | an API for persistent data on many things
Scripting          | scripting support
Placeholders **?** | an API for creating and using placeholders
NMS                | APIs for some `net.minecraft.server` features
Experimental **?** | Experimental features, _not recommended_

### Core

- SparseMC module loading
- Kotlin plugin loading
- Data (NBT, etc.)
- Global `server`

### Commands

- Annotation-based command creation
- Argument parsing

### Scheduler

- Global `scheduler`
- `delayed`
- `repeating`

### Configurations

- Annotation-based config creation

### Math

- 2D, 3D, 4D, float, double, int vectors
- 4D Matrix
- Quaternion
- Geometry (cube, cylinder, sphere, etc.)

### Inventory

- Item manipulation
- Basic inventories
- Complex inventories (menus/GUIs with complex behavior)

### Persistent

- Persistent data API for:

  - Online/Offline players
  - Block
  - World
  - Chunk
  - Server

### Scripting

- Automatically load and execute scripts
- APIs (`listen`, etc.) specifically made for scripts

### Placeholders

- _Unconfirmed_
- _Might just be in the NMS module or require the NMS module_
- Register placeholders to replace things in almost any text sent to clients.

### NMS

- Particles/Effects
- Titles/action bar
- NPCs **?**
- Advancements
- NBT

  - Items
  - Block Entities _(Formerly called "Tile Entities")_
