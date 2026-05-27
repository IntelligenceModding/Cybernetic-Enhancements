Chromeform

**A NeoForge 1.21.1 cyberware/android progression mod concept**

Chromeform is a Minecraft mod concept where players gradually replace parts of their body with cybernetic and android components. The inspiration is the feeling of Cyberpunk-style body enhancement: installing better eyes, stronger arms, faster legs, subdermal armor, a stronger core, and eventually becoming something that is no longer fully human.

The goal is not to create a bloated body simulation. The goal is to create cyberware that is useful in everyday Minecraft gameplay: mining, exploring, building, fighting, surviving, and progressing through a modpack.

> You install better eyes, stronger arms, faster legs, and eventually an android core. You become faster, stronger, and more efficient, but also more dependent on power, cooling, and maintenance.

---

## Core Fantasy

Chromeform should make the player feel like their own body is becoming part of the tech progression.

Instead of only building machines, cables, reactors, armor, and tools, the player upgrades themselves.

The player starts as a normal vanilla character, then slowly becomes:

1. **Organic**
2. **Augmented**
3. **Cybernetic**
4. **Synthetic**
5. **Full Android**

The mod should fit well into kitchen-sink modpacks, tech packs, exploration packs, and survival-focused packs.

---

## Design Goals

### 1. Everyday Usefulness

Every cyberware upgrade should be useful during normal Minecraft gameplay.

Good upgrades should help with things like:

- Mining
- Building
- Fighting mobs
- Exploring caves
- Traveling
- Surviving in the Nether
- Working around a base
- Handling large modpack infrastructure

The player should feel the benefit immediately.

---

### 2. Simple but Meaningful Systems

The mod should not require players to manage dozens of body organs, fluids, timers, and hidden stats.

The main systems should be:

- **Cyberware slots**
- **Chrome**
- **Power**
- **Heat**
- **Integrity**
- **Cyberstrain**
- **Overclocking**

That is enough depth without becoming annoying.

---

### 3. Cyberpunk-Style Tradeoffs

Cyberware should be powerful, but not free.

Every strong upgrade should create at least one cost:

- More energy use
- More heat generation
- More maintenance
- Higher Chrome cost
- Reduced biological benefits
- EMP weakness
- Lower mobility
- Higher repair cost

The player should think:

> "Do I want this extra power badly enough to deal with the downside?"

---

### 4. Modular Progression

The player should not transform instantly.

They should enhance themselves piece by piece.

Example progression:

- Install night vision optics.
- Add mining arms.
- Add dash legs.
- Upgrade to an internal battery.
- Add subdermal armor.
- Install a reactor core.
- Replace more of the body with synthetic systems.
- Eventually become a full android.

---

## Main Player Stat: Chrome

**Chrome** represents how much cybernetic and synthetic hardware is installed in the player's body.

Every cyberware part has a Chrome cost.

The more Chrome the player has, the further they move away from being fully organic.

| Chrome Level | Stage | Gameplay Meaning |
|---:|---|---|
| 0-20% | Organic | Mostly vanilla survival |
| 20-40% | Augmented | Useful cyberware, low risk |
| 40-70% | Cybernetic | Strong builds, needs power |
| 70-95% | Synthetic | Major boosts, major dependencies |
| 100% | Android | No longer biological |

Chrome should not be only cosmetic. It should affect survival, power dependency, healing, and weaknesses.

---

## Chrome Capacity

Players have a maximum safe Chrome capacity.

Example:

- Base player capacity: **40 Chrome**
- Better cores increase capacity.
- Neural upgrades increase capacity.
- Certain late-game items increase capacity.
- Configs can adjust the base capacity.

Each cyberware item costs Chrome.

Example values:

| Cyberware | Chrome Cost |
|---|---:|
| Basic Optics | 5 |
| Night Optics | 8 |
| Servo Arms | 8 |
| Spring Legs | 8 |
| Internal Battery | 10 |
| Mining Arms | 18 |
| Combat Optics | 15 |
| Reactor Core | 25 |
| Synthetic Frame | 30 |
| Android Core | 40 |

The player can exceed their safe Chrome capacity, but doing so causes Cyberstrain.

---

## Cyberstrain

**Cyberstrain** is the penalty for installing more cyberware than the body can safely handle.

Cyberstrain keeps the mod balanced without needing a complicated mental-health simulation.

At low Cyberstrain:

- Slightly higher energy use
- Slightly higher heat generation

At medium Cyberstrain:

- Energy drains faster
- Heat builds faster
- Cyberware integrity decreases faster
- Some food and potion effects become weaker

At high Cyberstrain:

- Random short glitches
- Temporary cyberware shutdowns
- Higher EMP sensitivity
- Occasional screen distortion
- Overclocking becomes dangerous
- Repairs become more expensive

Cyberstrain should be configurable for modpack makers.

### Cyberpsychosis Direction

Chromeform should treat cyberpsychosis as the fiction behind high Cyberstrain rather than as a separate giant sanity simulator.

Recommended behavior:

- Low Chrome / low Cyberstrain: no major downside
- Medium Cyberstrain: weaker biological healing, more power instability, more visual glitches
- High Cyberstrain: emergency OS instability, random distortion, occasional forced cooldown spikes
- Severe overload: temporary "episode" state with aggression, poor control, and dangerous side effects

Cyberpsychosis should feel like the body and mind struggling to keep up with too much chrome, not like a generic insanity bar.

---

## Main Cyberware Slots

Chromeform should keep the body readable, but the final body map should follow Cyberpunk-style cyberware categories instead of one broad slot per theme.

The player should have **10 main cyberware categories** with multiple install positions where appropriate.

1. **Frontal Cortex** - RAM, cooldown, cognition, and high-level neural support
2. **Operating System** - Cyberdeck, Sandevistan, Berserk, and similar core combat/utility systems
3. **Face** - optics, faceplates, and vision-linked facial hardware
4. **Arms** - Gorilla Arms, Mantis Blades, Monowire, Projectile Launch System, industrial armware
5. **Hands** - Smart Link, Ballistic Coprocessor, recoil, grip, and weapon handling
6. **Skeleton** - frame reinforcement, armor-bearing structure, extra health, knockback resistance
7. **Nervous System** - reflexes, emergency triggers, dodge, speed, attack handling
8. **Circulatory System** - sustain, emergency healing, Blood Pump, Biomonitor, backup-heart style effects
9. **Integumentary System** - Subdermal Armor, stealth skinware, insulation, fire resistance, optical camouflage
10. **Legs** - movement, jump, safe fall, sprinting, silent movement

The slot counts do not need to match Cyberpunk exactly at all times, but the structure should feel recognizable:

- `Frontal Cortex`: 2-3 slots
- `Operating System`: 1 slot
- `Face`: 1 slot
- `Arms`: 1 slot
- `Hands`: 1 slot
- `Skeleton`: 1-2 slots
- `Nervous System`: 2-3 slots
- `Circulatory System`: 2-3 slots
- `Integumentary System`: 2-3 slots
- `Legs`: 1 slot

This keeps the screen understandable while allowing far more Cyberpunk flavor than the earlier 8-slot abstraction.

### Mapping Older Draft Sections

Some older sections later in this document still use broader names because they were written before the body map was tightened.

Treat them like this:

- `Core` now mostly maps to **Operating System** plus internal power hardware and modules
- `Skin / Frame` now maps to **Skeleton** and **Integumentary System**
- `Lungs / Internal System` now maps mostly to **Circulatory System** plus survival-support internals
- `Neural Interface` now maps mostly to **Frontal Cortex** and late-game android control technology

The final implementation should follow the **10-category body map** above.

---

## Cyberware Quality and Iconics

Cyberware should use a clear quality ladder inspired by Cyberpunk.

Recommended baseline:

- **Tier 1** - basic, low-risk, early-game
- **Tier 2** - standard upgrades
- **Tier 3** - specialized mid-game implants
- **Tier 4** - powerful high-end implants
- **Tier 5** - elite late-game implants
- **Iconic** - rare named implants above or beside the normal ladder

The important rule is that tiers are not just bigger numbers.

Higher tiers should usually mean:

- Higher Chrome cost
- Higher power draw
- Higher heat load
- Stronger stats
- Stronger secondary effects
- Better module support
- More demanding repair and maintenance

### Iconic Cyberware

Chromeform should include rare named implants in the same spirit as Cyberpunk's iconic cyberware.

Iconic implants should be:

- Rare loot, blueprint, or boss-structure rewards
- Stronger or stranger than standard versions
- Sometimes sidegrades instead of simple upgrades
- Good late-game chase items

Examples:

- A premium Kiroshi branch with better scanning and reach
- A unique skeleton implant with stronger armor but worse heat behavior
- A named leg implant focused on sprinting or silent movement
- A rare circulatory implant that gives better emergency sustain

---

## Operating System Families

The Operating System slot should be one of the biggest build-defining decisions in the mod.

Chromeform should support three major OS families:

### Cyberdeck

Minecraft-fit role:

- Scanning
- Ore pulse
- Threat detection
- Redstone overlay
- Utility support
- Modpack automation interaction later

### Sandevistan

Minecraft-fit role:

- Burst movement speed
- Better attack handling
- Better dodge/mobility windows
- Short high-impact active ability

### Berserk

Minecraft-fit role:

- Extra hearts
- Melee damage
- Knockback resistance
- Damage resistance
- Tank-style overclocking

These should feel like real archetypes, not just different names for generic stat boosts.

---

## Cyberware Behavior Types

Chromeform should formally divide cyberware behavior into four groups.

### Passive

Always-on bonuses.

Examples:

- Extra hearts
- Armor
- Reach
- Mining speed
- Knockback resistance

### Active

Player-triggered abilities.

Examples:

- Dash
- Optics mode toggle
- Scan pulse
- Overclock
- Optical camouflage

### Triggered

Automatic emergency or conditional behavior.

Examples:

- Heal when low
- Brief reflex burst on danger
- Temporary damage reduction
- Emergency power reserve

### Placeholder Exotic

Installable and visible in the system, but with limited or no special mechanics yet.

Examples:

- Mantis Blades
- Monowire
- Projectile Launch System
- Full cyberdeck hacking packages before deeper gameplay exists

This lets Chromeform include iconic Cyberpunk cyberware early without pretending every exotic system is already complete.

---

## Additional Cyberpunk Categories To Support

The old concept focused heavily on optics, arms, legs, and a general internal core. That is a good Minecraft starting point, but Cyberpunk flavor depends on adding more specific body categories.

### Face

Good Minecraft-fit examples:

- Kiroshi-style optics packages
- Threat-marking optics
- Zoom/scout optics
- Behavioral or disguise faceplates

### Hands

Good Minecraft-fit examples:

- Smart Link
- Ballistic Coprocessor
- Grip and recoil support
- Faster attack handling

### Skeleton

Good Minecraft-fit examples:

- Extra hearts
- Armor
- Knockback resistance
- Better safe fall
- Frame-heavy tank builds

### Circulatory System

Good Minecraft-fit examples:

- Blood Pump
- Biomonitor
- Heal-on-kill style sustain
- Emergency reserve systems
- Android transition support

### Integumentary System

Good Minecraft-fit examples:

- Subdermal Armor
- Fire resistance
- Insulation and EMP resistance
- Stealth skinware
- Optical camouflage

These categories fit Minecraft extremely well because they convert cleanly into combat, exploration, and survival stats.

---

## RED-Style Systems That Should Exist

Cyberpunk 2077 gives the body map and the named implants, but Cyberpunk RED adds the extra body-tech depth that makes the setting feel broader than a simple gear screen.

Chromeform should eventually support these systems as real concept pillars:

- **Cyberaudio** - internal hearing, communication, recording, jamming, and acoustic sensing
- **Chipware / Chipware Sockets** - installable chips that act more like loaded software or temporary expertise than permanent body parts
- **Cyberlimb Option Slots** - arms and legs that can hold internal modules instead of every feature being a separate full implant
- **External Body** - tails, jaws, shoulder systems, and other visible or unusual bodyware
- **Fashionware** - cosmetic chrome that matters to style even when it has little or no combat value
- **Borgware / Full Body Conversion** - late-game extreme conversion with very high cost and very high payoff
- **Bioware / Biosculpting / Exotics** - softer-body enhancement, organic reinforcement, and appearance-changing body tech

This matters because Chromeform should feel like Cyberpunk body culture, not just like equipping combat buffs.

---

## Cyberaudio

Cyberaudio should become its own support system instead of being folded entirely into optics.

Minecraft-fit uses:

- Enhanced mob or player detection through sound
- Better warning range for wardens, sculk, traps, or hidden threats
- Internal recorder / replay utility
- Short-range radio or team communication flavor
- Bug detector / jammer style utility for future structures and security systems

Possible examples:

- Amplified Hearing
- Audio Recorder
- Internal Agent / Radio Communicator equivalents
- Voice Stress Analyzer
- Signal Jammer

Cyberaudio can live under **Face**, **Frontal Cortex**, or a future dedicated submenu, but conceptually it should be remembered as its own system family.

---

## Chipware and Skill Chips

Chromeform should support cyberware that is installed more like software than like a permanent limb.

Conceptually:

- A **Chipware Socket** is installed once
- Individual chips are loaded, swapped, or upgraded
- Chips can grant narrow temporary or situational bonuses

Minecraft-fit chip ideas:

- Mining chip
- Builder chip
- Scout chip
- Translator / trader chip
- Combat stance chip
- Automation interface chip

This is a good way to add utility depth without permanently bloating the body layout.

---

## Cyberlimb Modules and Internal Option Slots

Arms and legs should not stay forever at one-flat-item-per-function.

A better long-term structure:

- Base arm or leg implant defines the major family
- Higher tiers unlock internal module slots
- Modules change role without requiring an entirely new top-level implant every time

Examples:

- Gorilla Arms + mining actuator module
- Gorilla Arms + builder grip module
- Reinforced Tendons + dash module
- Reinforced Tendons + stealth footfall module

This keeps item growth manageable and matches the Cyberpunk idea that big implants often contain internal options.

---

## Bioware, Biosculpting, and Exotics

Not every body enhancement in Cyberpunk is hard chrome.

Chromeform should remember three adjacent categories:

- **Bioware** - biological reinforcement such as muscle, bone, skin, and survival adaptation
- **Biosculpting** - appearance changes, human or near-human cosmetic reconstruction
- **Exotics** - deliberately inhuman body styling and premium body packages

Minecraft-fit bioware examples:

- muscle reinforcement for melee and carrying
- bone lace for extra durability
- skin weave for hidden armor
- thermal or environmental weave for hazard resistance

Minecraft-fit exotic ideas:

- tails, unusual eyes, or visible nonhuman body packages
- mostly cosmetic at first
- some rare late-game packages can include small passive bonuses

This gives the mod a lane for softer-body enhancement and style-heavy progression outside pure machinery.

---

## Borgware and Full Conversion

Chromeform already aims toward android progression, but the concept should explicitly include the more extreme end of Cyberpunk body replacement.

Late-game body tech should include:

- oversized or specialized visual hardware
- external weapon mounts
- reinforced body frames
- additional exotic slot unlocks
- near-total or total body conversion

This should be expensive, infrastructure-heavy, and risky. It is not early progression. It is the point where the player is no longer just augmented, but fundamentally rebuilt.

---

## Consumables, Stims, and Medical Support

Cyberpunk body progression is not only about implants. Medication, inhalers, injectors, boosters, and black-market stims are part of the fantasy and should be in the concept.

Chromeform should support several consumable families:

### Medical Recovery

- **MaxDoc-style** instant heal items
- **Bounce Back-style** heal-plus-regeneration items
- clinic-grade versions with better results and higher cost

### Utility Boosters

- **Health Booster** for temporary extra hearts
- **Stamina Booster** for movement or attack endurance
- **Oxy Booster** for water breathing
- **Capacity Booster** for temporary carrying or work support
- **RAM / systems booster** for cyberdeck energy or cooldown support

### Black-Market Stims

High upside, real downside.

Examples:

- stronger melee but worse max health
- more movement but worse healing
- more cyberdeck output but worse physical durability
- better carry strength but worse stamina recovery

These should feel useful, dirty, and risky.

---

## Immunoblockers, Suppressants, and Therapy

Chromeform should include ways to temporarily manage cyberpsychosis pressure.

Key concept items and mechanics:

- **Immunoblockers** - temporarily reduce effective Cyberstrain / cyberpsychosis pressure, but can cause distortion, hallucination, or control penalties
- **Suppressants** - weaker but safer management medicine
- **Clinic therapy / calibration** - expensive, controlled reduction of long-term strain
- **Emergency stabilizers** - short-duration crisis tools used when a build is running too hot

This creates an actual loop around heavy chrome:

1. install more hardware
2. push your capacity
3. suffer strain
4. manage it through medicine, tuning, or scaling the build back

That loop is much closer to Cyberpunk than simple stat stacking.

---

# 1. Optics

Optics are eye and vision upgrades.

They should be among the first cyberware types the player can access because they are useful in almost every Minecraft world.

## Gameplay Purpose

Optics help with:

- Caving
- Mining
- Fighting
- Exploration
- Redstone
- Building
- Long-distance scouting

## Example Optics

### Basic Optics

A simple early-game implant.

Effects:

- Slightly improved visibility in darkness
- Small HUD element showing energy
- Unlocks the Android/Cyberware HUD

Costs:

- Low Chrome
- Very low energy use

---

### Night Optics

Effects:

- Toggleable night vision
- Works without potions
- Small energy drain while active

Drawbacks:

- Damaged optics can cause flickering vision
- EMP disables the effect temporarily

---

### Zoom Lens

Effects:

- Built-in spyglass-like zoom
- Useful for exploration and combat

Drawbacks:

- Minimal energy use
- Can blur if damaged

---

### Ore Pulse Optics

Effects:

- Sends out a short scan pulse
- Highlights nearby ores briefly
- Has a cooldown

Drawbacks:

- Medium energy cost per pulse
- Cannot be spammed
- Higher tiers may generate heat

Possible balance:

- Early version scans only common ores.
- Mid version scans tagged ores.
- Late version can scan modded ores through item/block tags.

---

### Threat Scanner

Effects:

- Highlights hostile mobs in darkness
- Shows nearby dangerous entities for a short time

Drawbacks:

- Constant energy drain while active
- Can be disrupted by EMP
- May have reduced range when damaged

---

### Redstone Overlay

Effects:

- Shows powered redstone components
- Displays signal strength visually
- Useful for redstone builders and automation players

Drawbacks:

- Low to medium energy drain while active

---

## Optics Design Notes

Optics should be very toggle-based.

The player should not constantly pay energy for every vision mode unless it is active.

Good keybind examples:

- Toggle current optics mode
- Cycle optics mode
- Scan pulse
- Zoom hold

---

# 2. Arms

Arms are one of the most important cyberware categories because they affect mining, combat, building, and interaction range.

## Gameplay Purpose

Arms help with:

- Mining faster
- Fighting stronger
- Building more comfortably
- Interacting with blocks from farther away
- Using tools more efficiently

## Example Arms

### Servo Arms

Early upgrade.

Effects:

- Slightly faster mining
- Slightly stronger melee attacks
- Low power usage

Drawbacks:

- Low maintenance cost
- Low Chrome cost

---

### Mining Arms

Industrial upgrade.

Effects:

- Significant mining speed boost
- Uses power while breaking blocks
- Optional bonus against stone-like blocks

Drawbacks:

- Generates heat during long mining sessions
- Loses integrity from heavy mining
- Less useful in combat than combat arms

---

### Reach Arms

Builder/explorer upgrade.

Effects:

- Increases block reach
- Increases entity interaction reach slightly
- Higher tiers may add +2 blocks reach

Drawbacks:

- Medium Chrome cost
- Energy use when placing or interacting at extended range

---

### Builder Grip

Building-focused upgrade.

Effects:

- Faster block placement
- More stable placement while sneaking
- Optional ability: undo the last placed block within a short time window

Drawbacks:

- Uses power when placing blocks quickly
- Not useful in combat

---

### Combat Actuators

Combat-focused upgrade.

Effects:

- Increased melee damage
- Increased knockback
- Faster shield raising or recovery
- Better weapon handling

Drawbacks:

- Heat generation during repeated attacks
- Higher integrity loss during combat
- Higher Chrome cost

---

### Heavy Hydraulic Arms

Late-game combat/mining hybrid.

Effects:

- Strong melee damage
- High mining force
- Can break certain blocks faster
- Possible special attack: charged punch

Drawbacks:

- Heavy power use
- Heavy heat generation
- Expensive repairs
- May slightly reduce swim speed

---

## Arms Design Notes

Arms should create meaningful choices.

A player should not easily get the best mining, building, and combat arms all at once.

Possible balancing approach:

- One active arm cyberware item
- Modules can specialize it
- Higher-tier arms have module slots
- Different arms have different base strengths

---

# 3. Legs

Legs provide movement upgrades.

They should make travel and exploration feel much better without replacing elytra, horses, boats, or other modded travel too early.

## Gameplay Purpose

Legs help with:

- Terrain traversal
- Escaping danger
- Building vertically
- Reducing fall damage
- Faster base movement
- Exploration

## Example Legs

### Spring Legs

Early mobility upgrade.

Effects:

- Slight jump boost
- Slight fall damage reduction

Drawbacks:

- Low energy cost
- Low Chrome cost

---

### Step Assist Legs

Effects:

- Allows walking up one-block ledges automatically
- Very useful for exploration and base movement

Drawbacks:

- Low energy cost
- Can be disabled with a keybind

---

### Dash Pistons

Effects:

- Short forward dash
- Useful for combat and movement
- Has cooldown

Drawbacks:

- Medium energy cost
- Generates heat
- Disabled at low power

---

### Fall Dampers

Effects:

- Reduces fall damage
- Higher tiers can absorb larger falls
- Uses power on impact

Drawbacks:

- If power is empty, protection is reduced
- Strong impacts reduce leg integrity

---

### Climbing Servos

Effects:

- Faster ladder climbing
- Faster vine climbing
- Faster scaffolding climbing
- Optional wall-kick ability at higher tier

Drawbacks:

- Uses energy while climbing quickly

---

### Runner Legs

Effects:

- Sprint speed boost
- Reduced sprint hunger drain at lower Chrome
- Later tiers use energy instead of hunger

Drawbacks:

- Heat generation while sprinting
- More wear from long-distance travel

---

## Legs Design Notes

Leg upgrades should feel amazing, but they must be balanced by energy and heat.

Movement upgrades are some of the most noticeable cyberware in normal play.

---

# 4. Core

The Core is the player's internal energy and power-management system.

It is one of the most important cyberware slots.

## Gameplay Purpose

The Core determines:

- Energy storage
- Energy generation
- Maximum cyberware support
- Heat production
- Android progression

## Example Cores

### Internal Battery

Early to mid-game upgrade.

Effects:

- Stores energy inside the player
- Powers cyberware away from base
- Can be charged at a Charging Station

Drawbacks:

- Does not generate energy
- Must be recharged

---

### High-Capacity Battery

Effects:

- Larger internal energy buffer
- Supports stronger cyberware

Drawbacks:

- Higher Chrome cost
- Takes longer to recharge

---

### Bioelectric Converter

Hybrid early-game option.

Effects:

- Converts food/saturation into small amounts of energy
- Useful before the player has good power infrastructure
- Keeps biological survival relevant

Drawbacks:

- Inefficient
- Still requires eating
- Not enough for heavy cyberware

---

### Micro Reactor

Mid/late-game upgrade.

Effects:

- Slowly generates energy
- Good for long exploration trips
- Supports stronger builds

Drawbacks:

- Generates heat
- Needs cooling support
- Can become dangerous if damaged

---

### Emergency Reserve Core

Effects:

- Keeps a small emergency power reserve
- Prevents total cyberware shutdown
- Can automatically disable non-critical systems

Drawbacks:

- Lower total energy capacity
- Not as strong as dedicated power cores

---

### Android Core

Endgame core.

Effects:

- Enables full android transformation
- Replaces hunger dependency with energy dependency
- Allows advanced synthetic systems
- Enables backup-body mechanics

Drawbacks:

- No natural regeneration
- Requires repairs instead of normal healing
- Strong EMP weakness
- High Chrome cost

---

## Core Design Notes

The Core should be the center of the mod's balance.

A powerful body needs a powerful Core.

---

# 5. Skin / Frame

Skin and Frame cyberware handle defense and environmental protection.

This slot should not make armor useless, but it can complement armor.

## Gameplay Purpose

Skin and Frame upgrades help with:

- Combat survival
- Nether travel
- Creeper explosions
- Environmental resistance
- Passive charging
- Stealth

## Example Skin / Frame Cyberware

### Subdermal Armor

Effects:

- Small armor bonus
- Works under normal armor

Drawbacks:

- Adds Chrome
- Slightly increases repair cost after damage

---

### Titanium Frame

Effects:

- Higher armor/toughness bonus
- Knockback resistance

Drawbacks:

- Heavy
- Slight movement penalty
- Higher energy use for movement cyberware

---

### Fireproof Layer

Effects:

- Reduces fire damage
- Reduces lava tick damage slightly
- Useful in the Nether

Drawbacks:

- Retains heat
- Makes overheating worse without cooling upgrades

---

### Solar Weave

Effects:

- Slowly charges internal energy in sunlight
- Good for explorers

Drawbacks:

- Weak underground
- Weak in the Nether
- Lower defense than armor plating

---

### Insulated Skin

Effects:

- Reduces lightning/EMP severity
- Optional compatibility with temperature mods
- Reduces some environmental damage

Drawbacks:

- Low armor value
- Not useful for direct combat

---

### Stealth Polymer

Effects:

- Reduces mob detection range
- Optional active camouflage mode

Drawbacks:

- Active camouflage drains energy
- Low armor
- Can fail when damaged or overheated

---

## Skin / Frame Design Notes

This slot is great for build identity.

A tank, explorer, stealth player, and Nether explorer should all choose different options.

---

# 6. Nervous System

The Nervous System slot controls reaction speed, combat precision, and emergency reflexes.

This is one of the most Cyberpunk-inspired categories.

## Gameplay Purpose

Nervous System cyberware helps with:

- Combat
- Fast reaction abilities
- Dodging
- Weapon handling
- Tool switching
- Overclocking

## Example Nervous System Cyberware

### Reflex Booster

Effects:

- Short burst of speed and attack handling
- Improves combat response for a few seconds

Drawbacks:

- High power spike
- Heat generation
- Cooldown

---

### Combat Predictor

Effects:

- Slight chance to reduce incoming projectile damage
- Improves bow/crossbow handling
- Could show incoming projectile warning

Drawbacks:

- Constant low energy drain while active
- EMP disruption

---

### Kinetic Response System

Effects:

- Reduces knockback recovery time
- Helps after being hit
- Good for boss fights

Drawbacks:

- Uses energy when triggered
- Can overheat during repeated damage

---

### Emergency Dodge System

Effects:

- Chance to automatically dodge or reduce a dangerous hit
- Could trigger only when health is low

Drawbacks:

- High cooldown
- High energy cost
- Can damage system integrity

---

### Time-Slice Reflexes

Late-game ability.

Effects:

- Briefly boosts movement, attack handling, and reaction speed
- Not literal time stop
- Feels like a short combat overclock

Drawbacks:

- Very high heat
- High Cyberstrain
- Long cooldown
- Risk of temporary crash if abused

---

## Nervous System Design Notes

This slot should be powerful but cooldown-based.

It should not be a permanent passive stat stick.

---

# 7. Lungs / Internal System

This slot handles breathing, poison filtering, environmental protection, and cooling support.

It should be useful without requiring too much micromanagement.

## Gameplay Purpose

Lungs/Internal cyberware helps with:

- Water exploration
- Poison mobs
- Nether survival
- Cooling
- Long mining sessions
- Biological-to-synthetic transition

## Example Internal Systems

### Filtered Lungs

Effects:

- Reduces poison duration
- Reduces harmful gas effects if other mods add them
- Slightly reduces damage from some biological effects

Drawbacks:

- Filter needs occasional replacement
- Damaged filters reduce effect

---

### Aqua Respirator

Effects:

- Underwater breathing
- Improved underwater vision if combined with optics

Drawbacks:

- Uses energy underwater
- Can fail if integrity is low

---

### Cooling System

Effects:

- Reduces heat buildup
- Improves heat decay
- Supports reactor cores and overclocking

Drawbacks:

- May require coolant cells at higher tiers
- Damaged cooling system makes overheating dangerous

---

### Metabolic Regulator

Effects:

- Reduces hunger drain
- Improves food efficiency
- Helps hybrid players before full android conversion

Drawbacks:

- Less useful at very high Chrome
- Uses small passive energy

---

### Synthetic Circulation

Effects:

- Improves repair efficiency
- Allows nanite repair systems
- Supports synthetic/android progression

Drawbacks:

- Reduces natural healing effectiveness
- Adds Chrome

---

## Internal System Design Notes

This slot bridges biological survival and android survival.

At low Chrome, it improves normal survival.

At high Chrome, it supports power/cooling/repair mechanics.

---

# 8. Neural Interface

The Neural Interface is the late-game brain/system-control slot.

It should unlock advanced cyberware interactions, not just give raw stats.

## Gameplay Purpose

Neural Interface cyberware helps with:

- Managing high Chrome
- Reducing Cyberstrain
- Unlocking android systems
- Overclocking
- Backup bodies
- Advanced HUD features
- Drone or remote shell control later

## Example Neural Interfaces

### Basic Neural Link

Effects:

- Increases Chrome capacity slightly
- Unlocks detailed Cyberware HUD
- Improves cyberware efficiency slightly

Drawbacks:

- EMP causes brief confusion
- Adds Chrome

---

### System Optimizer

Effects:

- Reduces energy cost of installed cyberware
- Reduces heat generation slightly
- Improves calibration

Drawbacks:

- Medium Chrome cost
- Expensive crafting

---

### Overclock Controller

Effects:

- Unlocks safer overclocking
- Reduces overclock damage
- Allows custom overclock profiles

Drawbacks:

- Still dangerous when overheated
- High energy demand

---

### Synthetic Consciousness Interface

Endgame.

Effects:

- Required for full android backup system
- Allows consciousness backup
- Allows spare-body respawn

Drawbacks:

- Huge Chrome cost
- EMP weakness
- Backup corruption risk if damaged

---

## Neural Interface Design Notes

The Neural Interface should be the gate to endgame android mechanics.

It should not be required for basic cyberware.

---

# Cyberware Tiers

Cyberware should use a clear tier system.

Do not create hundreds of almost-identical items.

A smaller number of clear tiers is better.

---

## Tier 1: Basic Cyberware

Early-game, safe, affordable.

Materials:

- Copper
- Iron
- Redstone
- Glass
- Quartz
- Gold

Examples:

- Basic Optics
- Night Optics
- Servo Arms
- Spring Legs
- Step Assist Legs
- Internal Battery
- Filtered Lungs

Gameplay:

- Useful convenience
- Low power use
- Low Chrome cost
- Minimal drawbacks

---

## Tier 2: Industrial Cyberware

Mid-game, stronger, more specialized.

Materials:

- Steel or tag-compatible equivalent
- Diamonds
- Advanced circuits
- Synthetic fiber
- Batteries
- Coolant cells

Examples:

- Mining Arms
- Reach Arms
- Combat Optics
- Runner Legs
- Subdermal Armor
- High-Capacity Battery
- Cooling System

Gameplay:

- Strong noticeable bonuses
- Requires power infrastructure
- Heat starts to matter
- Maintenance becomes relevant

---

## Tier 3: Synthetic Cyberware

Late-game, powerful, android-like.

Materials:

- Netherite
- Advanced processors
- Nanite gel
- Heat sinks
- High-capacity batteries
- Synthetic muscle fiber

Examples:

- Quantum Optics
- Hydraulic Combat Arms
- Synthetic Frame
- Micro Reactor
- Reflex Booster
- Synthetic Circulation
- System Optimizer

Gameplay:

- Big boosts
- Strong energy dependency
- Serious heat management
- High repair cost
- High Chrome cost

---

## Tier 4: Android Systems

Endgame transformation tier.

Materials:

- Nether stars
- Echo shards
- End crystals
- Dragon breath
- Netherite
- Quantum processors
- Consciousness matrix

Examples:

- Android Core
- Synthetic Consciousness Interface
- Self-Repair Frame
- Backup Body System
- Advanced Overclock Controller

Gameplay:

- No hunger dependency
- No drowning
- Poison immunity
- Repair-based survival
- Energy survival
- EMP vulnerability
- Backup respawn

---

# Power System

Cyberware uses NeoForge-compatible energy.

The mod should use the standard NeoForge energy capability so it can work with common tech mods.

## Energy Sources

Players can power themselves using:

- Charging Station
- Battery items
- Internal Battery
- Bioelectric Converter
- Micro Reactor
- Solar Weave
- Wireless charging upgrade
- Android Dock

---

## Energy Progression

### Early Game

The player charges at a Charging Station.

Energy is mostly used for small upgrades like optics or step assist.

### Mid Game

The player carries batteries or has a larger internal battery.

Mining arms, dash legs, and scanners become practical.

### Late Game

The player uses a Micro Reactor or advanced battery system.

Cyberware can be used during long trips.

### Android Stage

Energy replaces hunger as the main survival resource.

Running out of energy becomes dangerous.

---

## Low Energy Effects

If energy gets low:

- Active cyberware disables
- Movement upgrades stop working
- Optics shut off
- Mining/combat boosts stop working

If energy gets critically low at high Chrome:

- Slowness
- Weakness
- HUD warnings
- Emergency mode
- Possible temporary collapse for full androids

---

# Heat System

Heat exists to balance strong cyberware and overclocking.

It should not punish basic upgrades too much.

## Heat Sources

Heat increases from:

- Dashing
- Sprint boosting
- Mining very fast
- Combat actuators
- Reflex boosters
- Reactor cores
- Fire/lava
- Nether environment
- Overclocking

## Heat Reduction

Heat decreases naturally over time.

Cooling can be improved with:

- Cooling System
- Heat Sink module
- Coolant Pump
- Cryo-Gel Cell
- Water immersion
- Android Dock

## Overheating Effects

At medium heat:

- Higher energy use
- Reduced overclock safety
- Warning sounds/HUD effects

At high heat:

- Slowness
- Cyberware abilities shut down
- Integrity loss increases
- Vision distortion

At critical heat:

- Damage over time
- Cyberware failure
- Emergency shutdown
- Reactor instability if using a reactor core

---

# Integrity and Maintenance

Cyberware has **Integrity**, which works like durability for installed parts.

Integrity should matter, but it should not become annoying.

## Integrity Loss

Parts lose integrity from relevant use.

Examples:

- Mining Arms lose integrity while mining.
- Combat Arms lose integrity while fighting.
- Legs lose integrity from sprinting, dashing, jumping, and fall impacts.
- Skin/Frame loses integrity when taking damage.
- Optics lose integrity slowly while advanced vision modes are active.
- Core loses integrity from overheating or power overloads.

## Maintenance Frequency

Recommended balance:

- Normal play: repair every few Minecraft days.
- Heavy mining/combat: repair after a major session.
- Overclocking: damages parts faster.
- Death: damages installed cyberware.

The player should not need to repair every few minutes.

---

## Repair Materials

### Early Materials

- Conductive Paste
- Copper Wiring
- Servo Screws
- Replacement Joints
- Lubricant
- Glass Lens

### Mid-Game Materials

- Synthetic Fiber
- Micro Servos
- Carbon Plates
- Coolant Cells
- Advanced Circuits
- Calibration Chips

### Late-Game Materials

- Nanite Gel
- Quantum Processors
- Regenerative Alloy
- Self-Repair Nanites
- Consciousness Matrix Parts

---

# Overclocking

Overclocking is the high-risk, high-reward system.

It lets players push their cyberware beyond safe limits for a short time.

## General Rules

Overclocking:

- Costs a lot of energy
- Generates heat quickly
- Can damage cyberware integrity
- Has cooldowns
- Becomes safer with better Neural Interface upgrades

---

## Example Overclocks

### Mining Arms Overclock

Duration: 20 seconds

Effects:

- Much faster mining
- Extra reach
- Better block breaking

Costs:

- High energy drain
- Heavy heat generation
- Integrity damage after use

---

### Dash Legs Overclock

Duration: 10 seconds

Effects:

- Reduced dash cooldown
- Faster sprinting
- Higher jumping
- Better fall protection

Costs:

- High heat
- Leg integrity damage
- Crash risk if overheated

---

### Reflex Booster Overclock

Duration: 8 seconds

Effects:

- Faster attack handling
- Better dodge chance
- Faster movement
- Projectile warning

Costs:

- Massive heat spike
- High energy drain
- Long cooldown
- Cyberstrain increase

---

### Optics Overclock

Duration: 15 seconds

Effects:

- Stronger scan range
- Hostile outlines
- Ore pulse enhancement
- Zoom stabilization

Costs:

- High energy use
- Temporary vision distortion afterward

---

# Full Android Transformation

Full android transformation should be an endgame path.

It should not happen too early.

At 100% Chrome, the player can become a full android.

## Android Benefits

- No hunger dependency
- No drowning
- Poison immunity
- Reduced biological effects
- Higher energy capacity
- Advanced cyberware compatibility
- Can use backup body system
- Can use repair-based survival

## Android Penalties

- No natural regeneration
- Must repair instead of heal normally
- Requires energy to function
- EMP weakness
- Some food and potion effects no longer work
- Cyberware damage is more serious
- Backup systems are expensive

---

## Android Survival

At full android status:

- Hunger bar may be hidden or repurposed.
- Energy becomes the main survival resource.
- Health still exists.
- Healing comes from repair items, nanites, or maintenance stations.
- Food may be useless unless configured otherwise.
- Potions may have reduced or changed effects.

Possible config:

- Allow food healing for androids: true/false
- Allow natural regen for androids: true/false
- Energy drain while android: low/medium/high
- Android EMP weakness: true/false

---

# Death and Backup Bodies

This should be late-game only.

## Before Android Stage

Death works mostly like vanilla.

Possible effects:

- Installed cyberware loses integrity.
- Some energy is lost.
- Overclocked parts may be damaged more heavily.

## Full Android Stage

The player can unlock a **Backup Terminal**.

The Backup Terminal allows:

- Saving consciousness data
- Creating a spare android body
- Respawning in a backup shell

## Backup Body Rules

To avoid being overpowered:

- Backup bodies are expensive.
- The terminal must be powered.
- The spare shell must be built in advance.
- Some XP loss can still happen.
- Cyberware may not fully transfer unless duplicated.
- A damaged terminal may corrupt recovery.

## Optional Failure Event

If backup recovery fails badly, a hostile corrupted copy could spawn.

This should be rare and configurable.

---

# Machines

Chromeform should keep machines limited and focused.

The mod does not need 20 different processing blocks.

## 1. Ripper Station

The central cyberware installation machine.

Used to:

- Install cyberware
- Remove cyberware
- View Chrome
- View Cyberstrain
- View installed parts
- View part integrity
- View power and heat data

This is the most important block in the mod.

---

## 2. Charging Station

Used to charge the player's internal battery/core.

Variants:

- Basic Charging Station
- Fast Charger
- Wireless Charger
- Android Dock

The basic station should be available early.

---

## 3. Cyberware Fabricator

Used to craft cyberware components and finished implants.

Crafts:

- Optics
- Arms
- Legs
- Cores
- Frame parts
- Modules
- Repair materials

---

## 4. Maintenance Station

Used to maintain cyberware.

Functions:

- Repair installed parts
- Repair cyberware items
- Replace filters
- Add coolant
- Recalibrate damaged cyberware
- Diagnose malfunctions

---

## 5. Backup Terminal

Endgame machine.

Used to:

- Store consciousness backup
- Build spare shell
- Restore android body after death
- Manage backup cyberware templates

---

# Cyberware Modules

To avoid item bloat, some cyberware should have module slots.

Example:

- Basic cyberware: 0-1 module slots
- Industrial cyberware: 1-2 module slots
- Synthetic cyberware: 2-3 module slots
- Android cyberware: 3+ module slots

## Example Modules

### Optics Modules

- Night Vision Module
- Zoom Module
- Ore Pulse Module
- Threat Scanner Module
- Redstone Overlay Module

### Arm Modules

- Mining Accelerator
- Reach Extender
- Combat Actuator
- Builder Grip
- Shield Stabilizer

### Leg Modules

- Step Assist Module
- Dash Module
- Fall Damper Module
- Sprint Motor
- Climbing Servo

### Core Modules

- Cyberdeck RAM Module
- Sandevistan Cooldown Module
- Berserk Armor Module
- Emergency Reserve Module
- Energy Efficiency Module

### Skeleton / Integumentary Modules

- Armor Plating
- Fireproof Layer
- Insulation Layer
- Optical Camo Layer
- Stealth Polymer

### Frontal Cortex / OS Modules

- Chrome Capacity Booster
- Overclock Controller
- System Optimizer
- Backup Link

---

# Cyberware Loadouts

The mod should encourage builds instead of letting every player install everything.

---

## Miner Build

Recommended cyberware:

- Ore Pulse or scout optics
- Mining Arms
- Dense Marrow or other skeleton reinforcement
- Basic Cyberdeck or internal battery support
- Fall Dampener or Reinforced Tendons legs
- Cooling or circulatory sustain support

Strengths:

- Faster mining
- Easier ore finding
- Better cave survival
- Lower fall damage
- Good underground utility

Weaknesses:

- Uses energy constantly underground
- Mining Arms lose integrity faster
- Dust filters need occasional replacement
- Not optimized for combat

---

## Explorer Build

Recommended cyberware:

- Zoom or scout optics
- Runner Legs
- Step Assist
- Optical camo or light integumentary protection
- Environmental internal support
- Internal battery or light OS support

Strengths:

- Faster travel
- Better terrain movement
- Better long-distance scouting
- Less food pressure
- Good for mapping and exploring

Weaknesses:

- Weak underground
- Lower combat strength
- Solar charging depends on sunlight
- Medium protection

---

## Combat Build

Recommended cyberware:

- Combat optics
- Gorilla Arms or Mantis Blades
- Reflex Booster
- Subdermal Armor or heavy skeleton build
- Berserk or Sandevistan OS
- Cooling or circulatory sustain system

Strengths:

- Strong melee combat
- Better ranged handling
- Good boss fighting
- Knockback resistance
- Emergency reflex abilities

Weaknesses:

- Heat problems
- Expensive repairs
- High energy use
- High Cyberstrain risk

---

## Builder Build

Recommended cyberware:

- Reach Arms
- Smart Link or builder-grip style handware
- Step Assist Legs
- Fall Dampers
- Internal battery or light Cyberdeck support
- Basic Frontal Cortex support implant

Strengths:

- Easier large builds
- Longer placement reach
- Better movement around construction sites
- Less scaffolding needed
- Comfortable base building

Weaknesses:

- Weak combat performance
- Moderate energy use while building
- Not useful for boss fights

---

## Stealth Build

Recommended cyberware:

- Threat Scanner or scout optics
- Stealth Polymer / optical camo
- Silent Runner Legs
- Reflex Booster
- Sandevistan OS
- Zoom Optics

Strengths:

- Avoids mobs more easily
- Good at night
- Strong scouting
- Fast escape options

Weaknesses:

- Low armor
- Active stealth drains energy
- Bad if surrounded
- EMP disruption can be dangerous

---

## Full Android Build

Recommended cyberware:

- Android Core
- Heavy skeleton reinforcement
- Synthetic consciousness / backup tech
- Self-repair system
- High-end optics branch
- Advanced cooling and circulatory support

Strengths:

- No hunger
- No drowning
- Poison immunity
- Very strong survival
- Backup body access
- High cyberware compatibility

Weaknesses:

- Requires energy
- No normal healing
- EMP weakness
- Expensive maintenance
- Backup body infrastructure needed

---

# Visual Progression

The player's appearance should change as Chrome increases.

This should be configurable.

## Visual Stages

| Chrome | Visual Change |
|---:|---|
| 10% | Small glowing eye or subtle implants |
| 25% | Visible cybernetic limb details |
| 50% | Partial synthetic body plating |
| 75% | Clear android-like frame elements |
| 100% | Full android appearance |

## Cosmetic Options

Configurable options:

- Enable/disable visible cyberware
- Hide cyberware under armor
- Show glowing optics
- Show mechanical limbs
- Show synthetic skin
- Allow cosmetic-only cyberware skins
- Allow players to hide cyberware client-side

---

# EMP and Weaknesses

EMP should be the main counter to heavy cyberware.

## EMP Sources

Possible sources:

- EMP grenade item
- Lightning strikes
- Failed machinery
- Redstone overload device
- Certain hostile android mobs
- Rare structure traps

## EMP Effects

EMP can:

- Drain energy
- Disable cyberware temporarily
- Distort optics
- Increase Cyberstrain briefly
- Disable overclocking
- Interrupt reactor cores
- Cause full androids to enter emergency mode

EMP should be scary for high-Chrome players but not ruin early-game players.

---

# Blueprints, Clinics, and Acquisition

Cyberpunk bodyware feels better when not every implant comes from a normal crafting table progression.

Chromeform should use a mixed acquisition model:

- **Basic cyberware** can be crafted directly
- **Advanced cyberware** should require a blueprint or fabrication recipe unlock
- **Iconic cyberware** should come mostly from rare structures, bosses, or special blueprint chains
- **Replacement and downgrade paths** should exist so players can rebuild after damage or death

## Acquisition Types

### Direct Craft

Used for:

- Early optics
- Early arms and legs
- Basic battery and support implants
- General low-tier survival upgrades

### Fabricator Craft

Used for:

- Mid-tier cyberware
- Module-based upgrades
- Specialized combat/exploration builds

### Blueprint Unlock

Used for:

- Named late-game implants
- Experimental operating systems
- Better skeleton and circulatory systems
- Android transition hardware

### Rare Structure or Boss Source

Used for:

- Iconic implants
- Advanced OS branches
- Backup-body technology
- Consciousness systems

## Progression Rule

Higher-end cyberware should often depend on one of these:

- A lower-tier implant in the same family
- A matching blueprint
- A specific machine tier
- A structure-only material

This keeps cyberware progression feeling more like body technology and less like ordinary armor crafting.

---

# Optional World Content

World content should come after the core cyberware system works.

## Abandoned Clinics

Small generated structures.

Contents:

- Damaged cyberware
- Repair materials
- Data shards
- Cyberware blueprints
- Broken charging stations
- Rogue androids

## Crashed Synthetic Pods

Small rare surface structures.

Contents:

- Broken android shell
- Low-tier cyberware parts
- Batteries
- Warning logs

## Server Vaults

Late-game underground structures.

Contents:

- Backup technology
- Neural interface blueprints
- Quantum processors
- Rogue AI enemies
- Corruption hazards

---

# Mobs

Mobs should be limited and useful.

## Rogue Android

Hostile synthetic humanoid.

Variants:

- Scout Android
- Miner Android
- Soldier Android
- Broken Android

Drops:

- Damaged cyberware
- Processors
- Synthetic fiber
- Energy cells
- Rare modules

## Maintenance Drone

Small machine mob.

Can be:

- Neutral in clinics
- Hostile in corrupted facilities
- Repair nearby rogue androids

Drops:

- Servo parts
- Repair components
- Small batteries

## Corrupted Echo

Rare late-game enemy.

Possible spawn condition:

- Failed backup recovery
- Corrupted terminal
- Experimental android core failure

Behavior:

- Mimics some player movement
- Uses limited cyberware-like attacks
- Drops rare consciousness materials

This mob should be optional/configurable.

---

# Items and Materials

## Basic Materials

- Copper Wiring
- Conductive Paste
- Servo Screws
- Sensor Lens
- Micro Battery
- Lubricant
- Replacement Joint
- Basic Circuit Plate

## Mid-Game Materials

- Synthetic Fiber
- Micro Servo
- Carbon Plate
- Coolant Cell
- Advanced Processor
- Calibration Chip
- Hydraulic Pump
- Energy Regulator

## Late-Game Materials

- Nanite Gel
- Quantum Processor
- Regenerative Alloy
- Self-Repair Nanites
- Consciousness Matrix
- Reactor Stabilizer
- Synthetic Muscle Bundle
- Android Shell Frame

---

# Crafting and Tags

The mod should rely heavily on item tags for compatibility.

Recommended tags:

- `c:ingots/steel`
- `c:ingots/tin`
- `c:ingots/silver`
- `c:ingots/lead`
- `c:ingots/bronze`
- `c:plates/iron`
- `c:plates/steel`
- `c:wires/copper`
- `c:dusts/redstone`
- `c:gems/quartz`
- `c:circuits/basic`
- `c:circuits/advanced`
- `c:batteries`
- `c:gears/iron`
- `c:gears/steel`

If another mod provides these materials, Chromeform should use them.

If no other mod provides them, Chromeform can provide fallback recipes using vanilla items.

---

# Compatibility Ideas

## Energy Mods

Because Chromeform should use NeoForge energy capabilities, it can work with many tech mods.

Useful compatibility targets:

- Mekanism
- Powah
- Thermal-style energy mods
- Flux Networks-style systems
- Applied Energistics add-ons
- Immersive Engineering-style power systems

## Curios / Accessories

Possible optional integration:

- Some cyberware controller items can use Curios slots.
- Cosmetic cyberware overlays can be equipped.
- Special external battery packs can be worn.

However, the main cyberware system should not require Curios.

## Armor Mods

Chromeform should not make armor useless.

Subdermal armor should provide small passive protection, but normal armor should still matter.

High-tier synthetic frames can be strong, but they should cost Chrome, power, repair materials, and mobility.

## Food / Hunger Mods

At low Chrome:

- Food still works normally.
- Some cyberware can reduce hunger drain.

At high Chrome:

- Food becomes less effective.

At full Android:

- Food may no longer be required.

This should be configurable for modpacks.

---

# Config Options

Chromeform needs strong configs for pack makers.

## General Config

- Enable Chrome system
- Base Chrome capacity
- Maximum Chrome allowed
- Enable Cyberstrain
- Cyberstrain severity
- Enable visual body changes
- Enable android transformation

## Energy Config

- Energy use multiplier
- Charging speed multiplier
- Internal battery capacity multiplier
- Reactor generation multiplier
- Low-energy penalty severity

## Heat Config

- Heat generation multiplier
- Heat decay speed
- Nether heat modifier
- Overheat penalty severity
- Reactor overheating enabled/disabled

## Maintenance Config

- Integrity loss multiplier
- Repair cost multiplier
- Death cyberware damage
- Maintenance difficulty preset

## Android Config

- Full android hunger disabled
- Full android natural regeneration allowed
- Full android poison immunity
- Full android drowning immunity
- Backup body enabled
- Backup body cost multiplier
- EMP weakness multiplier

## Worldgen Config

- Abandoned clinics enabled
- Crashed pods enabled
- Server vaults enabled
- Rogue android spawn rate
- Structure loot quality

---

# Difficulty Presets

## Casual

- Low maintenance
- Low energy cost
- Low heat generation
- Light Cyberstrain
- Android transformation is mostly fun

## Standard

- Balanced energy and maintenance
- Heat matters for strong cyberware
- Cyberstrain is noticeable
- Android form has real tradeoffs

## Hardcore

- High maintenance
- Strong EMP weakness
- High energy dependency
- Overclocking is dangerous
- Android survival is infrastructure-heavy

## Expert Pack

- Expensive recipes
- Strong Cyberstrain
- Backup bodies are costly
- Advanced cyberware requires rare blueprints
- Intended for large modpacks

---

# User Interface

The mod should have a clean HUD and one main cyberware screen.

## Cyberware HUD

Displays:

- Energy
- Heat
- Chrome
- Cyberstrain warning
- Active cyberware mode
- Critical integrity warnings

The HUD should be minimal and configurable.

## Ripper Station UI

Displays:

- Body slot list
- Installed cyberware
- Chrome total
- Chrome capacity
- Cyberstrain level
- Power use estimate
- Heat generation estimate
- Integrity of each part

## Keybinds

Recommended keybinds:

- Toggle cyberware ability
- Cycle optics mode
- Activate scan pulse
- Dash
- Overclock current system
- Open cyberware status screen

---

# Advancement Ideas

## Early Advancements

### First Cut

Install your first cyberware.

### Seeing Chrome

Install your first optics upgrade.

### More Machine Than Before

Reach 25 Chrome.

---

## Mid-Game Advancements

### Built for Work

Install industrial cyberware.

### Running Hot

Overheat for the first time.

### Chrome Discipline

Use overclocking without damaging cyberware.

### Not Just Armor

Install subdermal plating.

---

## Late-Game Advancements

### Synthetic Threshold

Reach 75 Chrome.

### No Longer Hungry

Install an Android Core.

### Backup Plan

Create your first consciousness backup.

### Full Conversion

Become a full android.

---

# Version Roadmap

## Version 0.1 - Useful Cyberware

Goal:

Create the core gameplay foundation with upgrades players actually want immediately.

Add:

- Chrome stat
- Chrome capacity
- Ripper Station
- Charging Station
- Energy system
- Basic Cyberware HUD
- Cyberpunk-style body map UI
- Basic tier system
- Face slot
- Arms slot
- Legs slot

Cyberware:

- Basic Kiroshi optics branch
- Gorilla Arms / industrial arm branch
- Reinforced Tendons / movement leg branch
- Subdermal Armor
- Dense Marrow
- Basic circulatory sustain implant

This version should already be fun in survival.

---

## Version 0.2 - Builds and Balance

Goal:

Add build variety and basic balancing systems.

Add:

- Operating System slot
- Skeleton slot
- Integumentary slot
- Heat system
- Maintenance Station
- Integrity system
- Cyberdeck family
- Sandevistan family
- Berserk family
- Better skeleton and skinware choices
- Basic blueprint system

---

## Version 0.3 - Combat and Exploration

Goal:

Add stronger specialization.

Add:

- Frontal Cortex slot
- Hands slot
- Nervous System slot
- Circulatory System slot
- Better optics branches
- Better hand implants
- Better reflex systems
- Better healing / sustain systems
- Overclocking
- Triggered cyberware behaviors

---

## Version 0.4 - Android Path

Goal:

Add the actual android transformation.

Add:

- Full iconic and late-tier cyberware support
- Android control tech
- Android Core
- Full Android status
- No hunger mode
- Repair-based healing
- EMP weakness
- Android visual stage

---

## Version 0.5 - Endgame and World Content

Goal:

Add late-game goals and exploration content.

Add:

- Backup Terminal
- Spare android bodies
- Synthetic Consciousness Interface
- Self-Repair Frame
- Quantum Optics
- Abandoned Clinics
- Rogue Androids
- Rare blueprints
- Data shards

---

## Version 1.0 - Complete Chromeform Experience

Goal:

Polish and complete the mod as a full cyberware progression system.

Add/polish:

- Full config system
- JEI/REI/EMI integration
- Patchouli or guidebook integration
- Finalized UI
- Finalized balancing
- Worldgen polish
- Android backup polish
- Cosmetic rendering
- Modpack integration

---

# First Playable Feature Set

The first playable version should not try to include everything.

A good first release should include:

1. Chrome stat
2. Chrome capacity
3. Ripper Station
4. Charging Station
5. Energy storage
6. Face slot
7. Arms slot
8. Legs slot
9. Tiered cyberware items
10. Basic Kiroshi optics
11. Gorilla Arms or industrial arm branch
12. Reinforced Tendons or step-assist style legs
13. Subdermal Armor
14. One skeleton implant
15. One circulatory sustain implant

This gives players useful cyberware for normal gameplay immediately.

---

# Why This Mod Works

Chromeform fills a strong niche in modern Minecraft modding.

Many tech mods let players build machines.

Many magic mods let players gain spells.

Many RPG mods add skills.

But very few modern NeoForge mods make the player's own body the progression system.

Chromeform works because it gives players:

- A reason to build power infrastructure
- A reason to explore for blueprints
- A reason to specialize their character
- A reason to maintain and improve themselves
- A reason to keep progressing after getting good tools and armor
- A Cyberpunk-inspired fantasy that fits Minecraft survival

The key is restraint.

The mod should not simulate every organ.

It should focus on useful cyberware, strong tradeoffs, and clear progression.

---

# One-Sentence Pitch

**Chromeform lets you replace your body piece by piece with cyberware and android systems, gaining powerful Minecraft utility and combat upgrades while managing Chrome, energy, heat, and maintenance.**
