package de.artemis.cyberneticenhancements.common.datagen;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableCatalog;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CombatStatusType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffectType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.data.PackOutput;

public final class ModLanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, CyberneticEnhancements.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.cyberneticenhancements", "Cybernetic Enhancements");
        add("itemGroup.cyberneticenhancements.cyberware", "Cyberware");
        add("key.categories.cyberneticenhancements", "Cybernetic Enhancements");
        add("key.cyberneticenhancements.activate_cyberware", "Activate Cyberware");
        add("key.cyberneticenhancements.activate_auxiliary_cyberware", "Activate Auxiliary Cyberware");
        add("key.cyberneticenhancements.activate_arm_cyberware", "Activate Arm Cyberware");
        add("key.cyberneticenhancements.activate_face_cyberware", "Activate Face Cyberware");
        add("key.cyberneticenhancements.toggle_hud", "Toggle HUD");

        add("tooltip.cyberneticenhancements.cyberware_slot", "Slot: %s");
        add("tooltip.cyberneticenhancements.cyberware_tier", "Tier: %s");
        add("tooltip.cyberneticenhancements.chrome_cost", "Chrome Cost: %s");
        add("tooltip.cyberneticenhancements.integrity", "Integrity: %s / %s");
        add("tooltip.cyberneticenhancements.upgrades", "Upgrades: %s/%s");
        add("tooltip.cyberneticenhancements.chip_slots", "Chip Sockets: %s");
        add("tooltip.cyberneticenhancements.module_slots", "Module Bays: %s");
        add("tooltip.cyberneticenhancements.upgrade_chip_slots", "+%s Chip Sockets");
        add("tooltip.cyberneticenhancements.upgrade_module_slots", "+%s Module Bays");
        add("tooltip.cyberneticenhancements.capacity_bonus", "Chrome Capacity: +%s");
        add("tooltip.cyberneticenhancements.placeholder_effect", "Special functionality not implemented yet.");
        add("tooltip.cyberneticenhancements.chipware", "Chip Load: %s");
        add("tooltip.cyberneticenhancements.installed_chipware", "Installed Chipware: %s");
        add("tooltip.cyberneticenhancements.installed_modules", "Installed Modules: %s");
        add("tooltip.cyberneticenhancements.module_category", "Module Bay: %s");
        add("tooltip.cyberneticenhancements.consumable_category", "Category: %s");
        add("tooltip.cyberneticenhancements.consumable_cooldown", "Cooldown: %s s");
        add("tooltip.cyberneticenhancements.consumable_overdose", "Overdose Load: %s");
        add("tooltip.cyberneticenhancements.special.camillo_ram_manager", "Emergency reroute: refunds 20% of the first long cyberware cooldown and adds chrome headroom.");
        add("tooltip.cyberneticenhancements.special.ram_reallocator", "Emergency reroute: refunds 40% of the first long cyberware cooldown and adds more chrome headroom.");
        add("tooltip.cyberneticenhancements.special.bioconductor", "Cuts cyberware cooldowns, extends temporary cyberware buffs, and increases effective cyberstrain.");
        add("tooltip.cyberneticenhancements.special.cox_2_cybersomatic_optimizer", "A stronger, riskier neural overclock with heavier cooldown gains and a larger cyberstrain penalty.");
        add("tooltip.cyberneticenhancements.special.ex_disk", "Adds chrome headroom, extends temporary cyberware buffs, and slightly improves cooldown recovery.");
        add("tooltip.cyberneticenhancements.special.kerenzikov_boost_system", "Boosts reflexware builds, with larger speed gains when paired with Kerenzikov-class implants.");
        add("tooltip.cyberneticenhancements.special.memory_boost", "On kill: trims a flat chunk off your cyberware cooldowns.");
        add("tooltip.cyberneticenhancements.special.newton_module", "On kill: shaves a percentage off all tracked cyberware cooldowns.");
        add("tooltip.cyberneticenhancements.special.axolotl", "On kill: heavily reduces tracked cyberware cooldowns, with bigger resets on elite targets.");
        add("tooltip.cyberneticenhancements.special.quantum_tuner", "Passive cooldown reduction. Auxiliary ability: resets your longest active cyberware cooldown.");
        add("tooltip.cyberneticenhancements.special.ram_upgrade", "Adds chrome headroom and modestly improves cooldown and temporary buff efficiency.");
        add("tooltip.cyberneticenhancements.special.self_ice", "Auxiliary ability: purges suppression, RAM Jolt, and psychosis pressure, then stabilizes your systems.");
        add("tooltip.cyberneticenhancements.special.biodyn_berserk", "Passive combat durability. Active ability: a blunt-force berserk state with stronger melee output and post-rush healing from kills.");
        add("tooltip.cyberneticenhancements.special.militech_berserk", "Aggressive berserk suite. Active ability: front-loads heavy damage resistance, rewards low-health pressure, and cashes out kills into recovery.");
        add("tooltip.cyberneticenhancements.special.moore_tech_berserk", "Sustain-focused berserk suite. Active ability: heavier mitigation with a low-health safety push and the strongest end-of-rush healing.");
        add("tooltip.cyberneticenhancements.special.zetatech_berserk", "Mobile berserk suite. Active ability: mixes melee output with impact damping and a lighter recovery payout.");
        add("tooltip.cyberneticenhancements.special.arasaka_shadow", "Stealth cyberdeck. Passive mobility and headroom. Active ability: cloaks you and pulses soft control into nearby hostiles.");
        add("tooltip.cyberneticenhancements.special.biotech_sigma", "Combat support cyberdeck. Passive sustain and headroom. Active ability: floods nearby enemies with poison pressure while reinforcing your own recovery.");
        add("tooltip.cyberneticenhancements.special.militech_paraline", "Shock-control cyberdeck. Passive attack-speed support and headroom. Active ability: tags nearby enemies for pursuit and close-range aggression.");
        add("tooltip.cyberneticenhancements.special.netwatch_netdriver", "Range-control cyberdeck. Passive resilience and headroom. Active ability: breaches a wider area and continuously weakens marked targets.");
        add("tooltip.cyberneticenhancements.special.raven_microcyber", "Spread-focused cyberdeck. Passive tempo and headroom. Active ability: keeps nearby hostiles poisoned, slowed, and highlighted.");
        add("tooltip.cyberneticenhancements.special.tetratronic_rippler", "Premium disruption cyberdeck. Passive offense and defense. Active ability: sustains a stronger control cascade around you.");
        add("tooltip.cyberneticenhancements.special.militech_canto", "Experimental blackwall deck. Passive power and headroom. Active ability: unleashes the harshest area pulse and keeps it cycling while active.");
        add("tooltip.cyberneticenhancements.special.dynalar_sandevistan", "Baseline reflex OS. Active ability: a short speed-and-damage spike that can be disengaged early.");
        add("tooltip.cyberneticenhancements.special.zetatech_sandevistan", "Mass-market reflex OS. Active ability: gains extra bite while airborne and can be toggled off early.");
        add("tooltip.cyberneticenhancements.special.militech_falcon", "High-end reflex OS. Active ability: kill-driven Sandevistan that extends itself and heals on takedown.");
        add("tooltip.cyberneticenhancements.special.qiant_warp_dancer", "Defensive reflex OS. Active ability: trades some raw offense for mitigation and fire resistance.");
        add("tooltip.cyberneticenhancements.special.militech_apogee", "Elite reflex OS. Active ability: the strongest Sandevistan spike, with kill extensions and extra overflow shielding.");
        add("tooltip.cyberneticenhancements.special.chrome_compressor", "Pure utility operating system. No active ability; it exists to maximize chrome headroom.");
        add("tooltip.cyberneticenhancements.special.gorilla_arms", "Active ability on G: seismic slam that inflicts Trauma, leaving enemies easier to stagger and launch.");
        add("tooltip.cyberneticenhancements.special.electrifying_gorilla_arms", "Active ability on G: seismic slam that inflicts Trauma and Shock, scrambling movement and cyberware recovery.");
        add("tooltip.cyberneticenhancements.special.thermal_gorilla_arms", "Active ability on G: seismic slam that inflicts Trauma and Overheat, turning movement into extra heat damage.");
        add("tooltip.cyberneticenhancements.special.chemical_gorilla_arms", "Active ability on G: seismic slam that inflicts Trauma and Corrosion, weakening sustain and recovery.");
        add("tooltip.cyberneticenhancements.special.mantis_blades", "Active ability on G: forward pounce strike that opens Bleed for punishing pursuit damage.");
        add("tooltip.cyberneticenhancements.special.electrifying_mantis_blades", "Active ability on G: pounce strike that layers Bleed with Shock disruption.");
        add("tooltip.cyberneticenhancements.special.thermal_mantis_blades", "Active ability on G: pounce strike that layers Bleed with stacking Overheat.");
        add("tooltip.cyberneticenhancements.special.toxic_mantis_blades", "Active ability on G: pounce strike that layers Bleed with Corrosion.");
        add("tooltip.cyberneticenhancements.special.maxtac_mantis_blades", "Active ability on G: elite pounce that stacks Bleed, Mark, and Trauma for execution windows.");
        add("tooltip.cyberneticenhancements.special.monowire", "Active ability on G: sweeping arc lash that inflicts Mark, amplifying player follow-up damage.");
        add("tooltip.cyberneticenhancements.special.electrifying_monowire", "Active ability on G: arc lash that combines Mark with Shock control.");
        add("tooltip.cyberneticenhancements.special.thermal_monowire", "Active ability on G: arc lash that combines Mark with Overheat pressure.");
        add("tooltip.cyberneticenhancements.special.toxic_monowire", "Active ability on G: arc lash that combines Mark with Corrosion.");
        add("tooltip.cyberneticenhancements.special.projectile_launch_system", "Active ability on G: targeted micro-missile blast that inflicts Trauma and Mark.");
        add("tooltip.cyberneticenhancements.special.electrifying_projectile_launch_system", "Active ability on G: targeted launcher blast that inflicts Trauma and heavy Shock.");
        add("tooltip.cyberneticenhancements.special.thermal_projectile_launch_system", "Active ability on G: targeted launcher blast that inflicts Trauma and Overheat.");
        add("tooltip.cyberneticenhancements.special.toxic_projectile_launch_system", "Active ability on G: targeted launcher blast that inflicts Trauma and Corrosion.");
        add("tooltip.cyberneticenhancements.special.basic_kiroshi_optics", "Active ability on C: short optics scan that reveals nearby hostiles.");
        add("tooltip.cyberneticenhancements.special.clairvoyant", "Active ability on C: stronger enemy scan with longer-range threat reveal.");
        add("tooltip.cyberneticenhancements.special.doomsayer", "Active ability on C: scans for explosive threats and trap-like hazards.");
        add("tooltip.cyberneticenhancements.special.sentry", "Active ability on C: scans for ranged hostiles and device-like threats.");
        add("tooltip.cyberneticenhancements.special.stalker", "Active ability on C: reveals hidden or obstructed enemies through cover.");
        add("tooltip.cyberneticenhancements.special.the_oracle", "Active ability on C: premium combined scan for enemies, explosives, and emplacements.");
        add("tooltip.cyberneticenhancements.special.cockatrice", "Active ability on C: predator lock that marks and tracks prey while boosting pursuit.");
        add("tooltip.cyberneticenhancements.special.behavioral_imprint_synced_faceplate", "Active ability on C: scrambles nearby aggro and masks you in a short disguise window.");
        add("tooltip.cyberneticenhancements.special.ballistic_coprocessor", "Projectile handling suite: ranged hits gain better stopping power, harder long-range follow-through, and punish chained shots on the same target.");
        add("tooltip.cyberneticenhancements.special.microgenerator", "Contact discharge palm: melee hits spike the target with shock, arc into nearby threats, and briefly overcharge your close-range tempo.");
        add("tooltip.cyberneticenhancements.special.shock_absorber", "Passive stability implant: trims knockback, projectile pressure, explosive splash, and hard landing impact without changing your whole playstyle.");
        add("tooltip.cyberneticenhancements.special.immovable_force", "Heavily suppresses knockback, especially while bracing or crouching.");
        add("tooltip.cyberneticenhancements.special.smart_link", "Ranged hits handshake with the target, marking and outlining them for follow-up fire.");
        add("tooltip.cyberneticenhancements.special.adrenaline_converter", "Taking damage converts panic into a short speed burst for faster repositioning.");
        add("tooltip.cyberneticenhancements.special.adreno_trigger", "Dangerous hits trigger a stronger reflex surge with speed, attack tempo, and brief mitigation.");
        add("tooltip.cyberneticenhancements.special.atomic_sensors", "Passive local threat sweep: nearby hostiles and dangerous projectiles are periodically outlined.");
        add("tooltip.cyberneticenhancements.special.kerenzikov", "Projectile hits kick in a short sidestep window with evasive speed and lighter incoming pressure.");
        add("tooltip.cyberneticenhancements.special.neofiber", "Stability weave: further suppresses knockback and softens fall impacts.");
        add("tooltip.cyberneticenhancements.special.revulsor", "Emergency reflex burst: dangerous hits fire a close shockwave that knocks threats back and hardens you briefly.");
        add("tooltip.cyberneticenhancements.special.stabber", "Close-range hunter implant: melee strikes open Bleed, with bigger payoff from behind or against marked prey.");
        add("tooltip.cyberneticenhancements.special.synaptic_accelerator", "Combat hits trigger a fast neural acceleration window that sharpens movement, attack tempo, and handling.");
        add("tooltip.cyberneticenhancements.special.tyrosine_injector", "On kill: injects a short combat stimulant package with mobility, offense, and recovery.");
        add("tooltip.cyberneticenhancements.special.visual_cortex_support", "Passively tags the hostile you are directly tracking, making target acquisition clearer in normal sightlines.");
        add("tooltip.cyberneticenhancements.special.deep_field_visual_interface", "Upgraded visual support: tracks farther targets and can keep two threats outlined even through light cover.");
        add("tooltip.cyberneticenhancements.special.adrenaline_booster", "Taking a real hit spins up a short regeneration-and-mobility burst to keep you fighting.");
        add("tooltip.cyberneticenhancements.special.biomonitor", "Emergency trigger: below safe health, automatically injects recovery and shielding on cooldown.");
        add("tooltip.cyberneticenhancements.special.black_mamba", "Weaponized bloodstream: your hits inflict Corrosion, with extra bite once the target is already degrading.");
        add("tooltip.cyberneticenhancements.special.blood_pump", "Emergency trigger: deeper health loss forces a stronger recovery surge and temporary hardening.");
        add("tooltip.cyberneticenhancements.special.clutch_padding", "Softens heavy impacts and explosive recoil beyond its baseline knockback resistance.");
        add("tooltip.cyberneticenhancements.special.isometric_stabilizer", "Advanced stabilizer: dangerous hits briefly harden your frame and heavily suppress displacement.");
        add("tooltip.cyberneticenhancements.special.feedback_circuit", "Ranged pressure charges the loop, refunding tracked cyberware cooldowns and building a brief shield buffer.");
        add("tooltip.cyberneticenhancements.special.electromag_recycler", "Upgraded feedback loop: ranged hits and incoming fire recycle more cyberware tempo and layer Shock into follow-up shots.");
        add("tooltip.cyberneticenhancements.special.heal_on_kill", "On kill: converts aggression into immediate healing and a short recovery pulse.");
        add("tooltip.cyberneticenhancements.special.microrotors", "Combat rhythm pump: landing hits briefly sharpens attack tempo and footwork.");
        add("tooltip.cyberneticenhancements.special.second_heart", "Emergency trigger: critical health forces a major lifesaving restart with its own long cooldown.");
        add("tooltip.cyberneticenhancements.special.threatevac", "Panic-response package: dangerous hits trigger a fast escape window with speed, footing, and safer disengage.");
        add("tooltip.cyberneticenhancements.special.carapace", "Braced shell skinware: crouching or getting swarmed hardens the plating and resists displacement.");
        add("tooltip.cyberneticenhancements.special.cellular_adapter", "Adaptive skinware: once you stay out of combat, regeneration and stability ramp back up.");
        add("tooltip.cyberneticenhancements.special.cogito_lattice", "Mind-linked plating: higher cyberstrain pushes the lattice into a stronger protective state.");
        add("tooltip.cyberneticenhancements.special.countershell", "Reactive shell: taking a meaningful hit briefly hardens your armor and trims follow-up damage.");
        add("tooltip.cyberneticenhancements.special.defenzikov", "Defensive response weave: ranged or dangerous hits trigger a short evasive hardening burst.");
        add("tooltip.cyberneticenhancements.special.nano_plating", "Integrity-biased plating: while your health is still high, the skinware holds a tighter defensive envelope.");
        add("tooltip.cyberneticenhancements.special.optical_camo", "Stealth skin system: crouch and stay out of combat to fade from sight; attacking or taking hits breaks the cloak.");
        add("tooltip.cyberneticenhancements.special.pain_editor", "Top-tier pain suppression: smooths incoming spikes and builds a brief buffer when pressure gets serious.");
        add("tooltip.cyberneticenhancements.special.painducer", "Damage-smoothing weave: heavy hits are softened and converted into short recovery and overflow shielding.");
        add("tooltip.cyberneticenhancements.special.proxishield", "Close-threat plating: nearby hostiles reinforce the shield skin and make point-blank pressure less punishing.");
        add("tooltip.cyberneticenhancements.special.peripheral_inverse", "Iconic proximity shell: close threats drive a stronger defensive inversion and heavier anti-displacement protection.");
        add("tooltip.cyberneticenhancements.special.rangeguard", "Spacing-sensitive skinware: incoming ranged pressure is softened and mid-range fights favor your armor profile.");
        add("tooltip.cyberneticenhancements.special.shock_n_awe", "Retaliation plating: close attackers trigger an electrical backlash that shocks and outlines nearby threats.");
        add("tooltip.cyberneticenhancements.special.subdermal_armor", "Classic under-skin armor: further trims direct physical punishment beyond its raw armor value.");
        add("tooltip.cyberneticenhancements.special.chitin", "Iconic hardened shell: performs best under pressure, gaining extra mass and mitigation when enemies close in.");
        add("tooltip.cyberneticenhancements.special.fortified_ankles", "Charged jump hardware: crouch briefly to store a stronger launch, then cash it out into a boosted leap with safer landings and short post-impact recovery.");
        add("tooltip.cyberneticenhancements.special.jenkins_tendons", "Runner tendons: sprinting quickly ramps mobility, light sprint-jumps carry farther, and landings feed back into cleaner movement.");
        add("tooltip.cyberneticenhancements.special.leeroy_ligament_system", "Iconic charge chassis: sprinting ramps into stronger mobility, sprint-jumps lunge harder, shoulder-checks stagger targets, and rough landings preserve your momentum.");
        add("tooltip.cyberneticenhancements.special.lynx_paws", "Silent paws: stealth movement is faster, softer on landings, and can shake nearby mob attention while you move carefully.");
        add("tooltip.cyberneticenhancements.special.reinforced_tendons", "Double-jump tendons: pressing jump again midair fires a real second leap with extra forward carry.");
        add("tooltip.cyberneticenhancements.special.epimorphic_skeleton", "Adaptive skeleton lattice: the more damaged you are, the more it pushes regeneration and stability.");
        add("tooltip.cyberneticenhancements.special.feen_x", "Emergency skeleton failsafe: dropping critical triggers shielding, mobility, and a broad cyberware cooldown refund.");
        add("tooltip.cyberneticenhancements.special.ram_recoup", "Damage-fed skeleton node: taking hits trims active cyberware cooldowns on a short internal cycle.");
        add("tooltip.cyberneticenhancements.special.scar_coalescer", "Emergency coalescence: dangerous hits harden the frame and briefly accelerate recovery.");
        add("tooltip.cyberneticenhancements.special.scarab", "Low-profile combat frame: crouching grants extra armor, mitigation, and sneak-mobility.");

        addMessageTranslations();
        addEffectTranslations();
        addSlotTranslations();
        addTierTranslations();
        addScreenTranslations();
        addMaterialTranslations();
        addConsumableTranslations();
        addChipwareTranslations();
        addModuleTranslations();
        addCyberwareTranslations();
        addBlockTranslations();
    }

    private void addEffectTranslations() {
        add(CyberwareEffectType.MAX_HEALTH.translationKey(), "+%s Hearts");
        add(CyberwareEffectType.ARMOR.translationKey(), "+%s Armor");
        add(CyberwareEffectType.ATTACK_DAMAGE.translationKey(), "+%s Attack Damage");
        add(CyberwareEffectType.ATTACK_SPEED.translationKey(), "+%s Attack Speed");
        add(CyberwareEffectType.MOVEMENT_SPEED.translationKey(), "+%s%% Movement Speed");
        add(CyberwareEffectType.BLOCK_BREAK_SPEED.translationKey(), "+%s%% Mining Speed");
        add(CyberwareEffectType.BLOCK_REACH.translationKey(), "+%s Block Reach");
        add(CyberwareEffectType.ENTITY_REACH.translationKey(), "+%s Entity Reach");
        add(CyberwareEffectType.STEP_HEIGHT.translationKey(), "+%s Step Height");
        add(CyberwareEffectType.SAFE_FALL_DISTANCE.translationKey(), "+%s Safe Fall Distance");
        add(CyberwareEffectType.FALL_DAMAGE_REDUCTION.translationKey(), "%s%% Fall Damage");
        add(CyberwareEffectType.KNOCKBACK_RESISTANCE.translationKey(), "+%s%% Knockback Resistance");
        add(CyberwareEffectType.CHROME_CAPACITY.translationKey(), "+%s Chrome Headroom");
        add(CyberwareEffectType.HEALTH_REGEN.translationKey(), "+%s Hearts/sec");
        add(CyberwareEffectType.DAMAGE_REDUCTION.translationKey(), "+%s%% Damage Reduction");
        add(CyberwareEffectType.BONUS_ABSORPTION.translationKey(), "+%s Absorption Hearts");
        add(CyberwareEffectType.JUMP_POWER.translationKey(), "+%s%% Jump Power");
        add(CyberwareEffectType.NIGHT_VISION.translationKey(), "Night Vision");
        add(CyberwareEffectType.FIRE_RESISTANCE.translationKey(), "Fire Resistance");
        add(CyberwareEffectType.WATER_BREATHING.translationKey(), "Water Breathing");
        add(CombatStatusType.SHOCK.translationKey(), "Shock");
        add(CombatStatusType.OVERHEAT.translationKey(), "Overheat");
        add(CombatStatusType.CORROSION.translationKey(), "Corrosion");
        add(CombatStatusType.TRAUMA.translationKey(), "Trauma");
        add(CombatStatusType.BLEED.translationKey(), "Bleed");
        add(CombatStatusType.MARK.translationKey(), "Mark");
        add(CombatStatusType.SHOCK.descriptionKey(), "Stacks: %s | Scrambles movement and slows cyberware recovery.");
        add(CombatStatusType.OVERHEAT.descriptionKey(), "Stacks: %s | Heat damage ramps harder while the target keeps moving.");
        add(CombatStatusType.CORROSION.descriptionKey(), "Stacks: %s | Cuts healing and corrodes the target over time.");
        add(CombatStatusType.TRAUMA.descriptionKey(), "Stacks: %s | Increases knockback taken and opens the target to heavier hits.");
        add(CombatStatusType.BLEED.descriptionKey(), "Stacks: %s | Physical damage worsens when the target keeps moving.");
        add(CombatStatusType.MARK.descriptionKey(), "Stacks: %s | Player follow-up damage against the target is amplified.");
    }

    private void addMessageTranslations() {
        add("message.cyberneticenhancements.ability.no_operating_system", "No operating system installed.");
        add("message.cyberneticenhancements.ability.no_active_ability", "Installed operating system has no active ability.");
        add("message.cyberneticenhancements.ability.cooldown", "Cyberware cooling down: %s s");
        add("message.cyberneticenhancements.ability.engaged", "%s engaged for %s seconds.");
        add("message.cyberneticenhancements.ability.disengaged", "%s disengaged.");
        add("message.cyberneticenhancements.ability.sandevistan", "Sandevistan engaged for %s seconds.");
        add("message.cyberneticenhancements.ability.berserk", "Berserk engaged for %s seconds.");
        add("message.cyberneticenhancements.arms.no_cyberware", "No arm cyberware installed.");
        add("message.cyberneticenhancements.arms.no_active_ability", "Installed arm cyberware has no active ability.");
        add("message.cyberneticenhancements.arms.cooldown", "%s cooling down: %s s");
        add("message.cyberneticenhancements.arms.activated", "%s activated.");
        add("message.cyberneticenhancements.face.no_cyberware", "No face cyberware installed.");
        add("message.cyberneticenhancements.face.no_active_ability", "Installed face cyberware has no active ability.");
        add("message.cyberneticenhancements.face.cooldown", "%s cooling down: %s s");
        add("message.cyberneticenhancements.face.active", "%s is already active.");
        add("message.cyberneticenhancements.face.scan", "%s scan engaged for %s seconds.");
        add("message.cyberneticenhancements.face.cockatrice", "%s predator lock engaged for %s seconds.");
        add("message.cyberneticenhancements.face.faceplate", "%s disguise engaged for %s seconds.");
        add("message.cyberneticenhancements.aux.no_active_ability", "No auxiliary frontal cortex ability installed.");
        add("message.cyberneticenhancements.aux.cooldown", "%s cooling down: %s s");
        add("message.cyberneticenhancements.aux.self_ice", "Self-ICE purged hostile neural pressure.");
        add("message.cyberneticenhancements.aux.quantum_tuner", "Quantum Tuner rerouted your longest cyberware cooldown.");
        add("message.cyberneticenhancements.aux.quantum_tuner.no_target", "No cyberware cooldown is active enough to reroute.");
        add("message.cyberneticenhancements.trigger.biomonitor", "Biomonitor injected an emergency recovery cycle.");
        add("message.cyberneticenhancements.trigger.blood_pump", "Blood Pump forced emergency circulation.");
        add("message.cyberneticenhancements.trigger.second_heart", "Second Heart restarted your core systems.");
        add("message.cyberneticenhancements.trigger.reflex_tuner", "Reflex Tuner kicked into emergency overdrive.");
        add("message.cyberneticenhancements.consumable.immunoblockers", "Immunoblockers forced your chrome pressure down, but the dose hits hard.");
        add("message.cyberneticenhancements.consumable.chrome_suppressant", "Chrome Suppressant steadied your overloaded systems.");
        add("message.cyberneticenhancements.consumable.ram_jolt", "RAM Jolt tightened your neural recovery cycle.");
        add("message.cyberneticenhancements.consumable.cooldown", "%s is still on cooldown.");
        add("message.cyberneticenhancements.consumable.used", "%s applied.");
        add("message.cyberneticenhancements.consumable.overdose_minor", "Your system is rejecting the chemical stack.");
        add("message.cyberneticenhancements.consumable.overdose_major", "Overdose spike. Back off the stims.");
        add("message.cyberneticenhancements.psychosis.triggered", "Cyberpsychosis episode triggered. You are losing control.");
        add("message.cyberneticenhancements.psychosis.triggered_minor", "Cyberpsychosis surge triggered. Your systems are slipping.");
        add("message.cyberneticenhancements.psychosis.triggered_major", "Major cyberpsychosis episode triggered. You are losing control.");
        add("message.cyberneticenhancements.psychosis.forced", "Forced cyberpsychosis test episode started.");
        add("message.cyberneticenhancements.psychosis.ended", "You regained control of your body.");
        add("message.cyberneticenhancements.psychosis.stabilized", "Your systems were forcibly stabilized.");
        add("message.cyberneticenhancements.hud.enabled", "Cyberware HUD enabled.");
        add("message.cyberneticenhancements.hud.disabled", "Cyberware HUD disabled.");
    }

    private void addSlotTranslations() {
        add(CyberwareSlotType.FRONTAL_CORTEX.translationKey(), "Frontal Cortex");
        add(CyberwareSlotType.OPERATING_SYSTEM.translationKey(), "Operating System");
        add(CyberwareSlotType.ARMS.translationKey(), "Arms");
        add(CyberwareSlotType.FACE.translationKey(), "Face");
        add(CyberwareSlotType.SKELETON.translationKey(), "Skeleton");
        add(CyberwareSlotType.HANDS.translationKey(), "Hands");
        add(CyberwareSlotType.NERVOUS_SYSTEM.translationKey(), "Nervous System");
        add(CyberwareSlotType.CIRCULATORY_SYSTEM.translationKey(), "Circulatory System");
        add(CyberwareSlotType.INTEGUMENTARY_SYSTEM.translationKey(), "Integumentary System");
        add(CyberwareSlotType.LEGS.translationKey(), "Legs");

        for (CyberwareSlotType type : CyberwareSlotType.values()) {
            for (int slotNumber = 1; slotNumber <= type.getBaseSlotCount(); slotNumber++) {
                add(type.translationKey() + "." + slotNumber, humanizeSlot(type) + " Slot " + slotNumber);
            }
        }
    }

    private void addTierTranslations() {
        add(CyberwareTier.TIER_1.translationKey(), "Tier 1");
        add(CyberwareTier.TIER_2.translationKey(), "Tier 2");
        add(CyberwareTier.TIER_3.translationKey(), "Tier 3");
        add(CyberwareTier.TIER_4.translationKey(), "Tier 4");
        add(CyberwareTier.TIER_5.translationKey(), "Tier 5");
    }

    private void addScreenTranslations() {
        add("screen.cyberneticenhancements.ripper_station.body_overview", "Body Overview");
        add("screen.cyberneticenhancements.ripper_station.ripper_matrix", "Ripper Matrix");
        add("screen.cyberneticenhancements.ripper_station.system_summary", "System Summary");
        add("screen.cyberneticenhancements.ripper_station.stage", "Stage");
        add("screen.cyberneticenhancements.ripper_station.stage.organic", "Organic");
        add("screen.cyberneticenhancements.ripper_station.stage.augmented", "Augmented");
        add("screen.cyberneticenhancements.ripper_station.stage.cybernetic", "Cybernetic");
        add("screen.cyberneticenhancements.ripper_station.stage.synthetic", "Synthetic");
        add("screen.cyberneticenhancements.ripper_station.stage.android", "Full Android");
        add("screen.cyberneticenhancements.ripper_station.chrome", "Chrome");
        add("screen.cyberneticenhancements.ripper_station.cyberstrain", "Cyberstrain");
        add("screen.cyberneticenhancements.ripper_station.integrity", "Integrity");
        add("screen.cyberneticenhancements.ripper_station.overview_health", "Health");
        add("screen.cyberneticenhancements.ripper_station.overview_recovery", "Recovery");
        add("screen.cyberneticenhancements.ripper_station.overview_armor", "Armor");
        add("screen.cyberneticenhancements.ripper_station.overview_resistance", "Resistance");
        add("screen.cyberneticenhancements.ripper_station.overview_power", "Power");
        add("screen.cyberneticenhancements.ripper_station.overview_speed", "Speed");
        add("screen.cyberneticenhancements.ripper_station.overview_mobility", "Mobility");
        add("screen.cyberneticenhancements.ripper_station.overview_reach", "Reach");
        add("screen.cyberneticenhancements.ripper_station.overview_utility", "Utility");
        add("screen.cyberneticenhancements.ripper_station.overview_systems", "Systems");
        add("screen.cyberneticenhancements.ripper_station.cyberware_slots", "Cyberware");
        add("screen.cyberneticenhancements.ripper_station.installed_parts", "Installed");
        add("screen.cyberneticenhancements.ripper_station.chrome_profile", "Chrome Profile");
        add("screen.cyberneticenhancements.ripper_station.empty", "Empty");
        add("screen.cyberneticenhancements.ripper_station.slot_hint", "Install matching cyberware here.");
        add("screen.cyberneticenhancements.ripper_station.slot_supported_tier", "Supported Tier: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_cost", "Upgrade Cost: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_hold", "Hold LMB for 5s to unlock %s.");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_missing", "Missing: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_progress", "Upgrade Progress: %s");
        add("screen.cyberneticenhancements.ripper_station.slot_upgrade_maxed", "Slot already supports Tier 5.");
        add("screen.cyberneticenhancements.ripper_station.chipware", "Chipware");
        add("screen.cyberneticenhancements.ripper_station.chip_slot_hint", "Install a compatible skillchip here.");
        add("screen.cyberneticenhancements.ripper_station.chip_slot_locked", "Install a chipware socket to unlock this bay.");
        add("screen.cyberneticenhancements.ripper_station.no_chip_socket", "No socket installed");
        add("screen.cyberneticenhancements.ripper_station.arm_modules", "Arm Modules");
        add("screen.cyberneticenhancements.ripper_station.leg_modules", "Leg Modules");
        add("screen.cyberneticenhancements.ripper_station.module_slot_hint", "Install a compatible limb module here.");
        add("screen.cyberneticenhancements.ripper_station.module_slot_locked", "Higher-tier limb cyberware unlocks this module bay.");
        add("screen.cyberneticenhancements.ripper_station.no_arm_cyberware", "No arm cyberware installed");
        add("screen.cyberneticenhancements.ripper_station.no_leg_cyberware", "No leg cyberware installed");
        add("screen.cyberneticenhancements.tech_station.service_bay", "Service Bay");
        add("screen.cyberneticenhancements.tech_station.upgrade_bay", "Upgrade Bay");
        add("screen.cyberneticenhancements.tech_station.operation", "Operation");
        add("screen.cyberneticenhancements.tech_station.input", "Input");
        add("screen.cyberneticenhancements.tech_station.materials", "Materials");
        add("screen.cyberneticenhancements.tech_station.upgrade_parts", "Parts");
        add("screen.cyberneticenhancements.tech_station.output", "Output");
        add("screen.cyberneticenhancements.tech_station.repair", "Repair");
        add("screen.cyberneticenhancements.tech_station.upgrade", "Upgrade");
        add("screen.cyberneticenhancements.tech_station.unavailable", "Unavailable");
        add("screen.cyberneticenhancements.tech_station.no_valid_operation", "Load the right materials to service this implant.");
        add("screen.cyberneticenhancements.tech_station.ready_repair", "Repair package ready.");
        add("screen.cyberneticenhancements.tech_station.ready_upgrade", "Upgrade package ready.");
        add("screen.cyberneticenhancements.tech_station.insert_repair_target", "Insert cyberware to inspect repair materials.");
        add("screen.cyberneticenhancements.tech_station.repair_unavailable", "This item cannot be repaired here.");
        add("screen.cyberneticenhancements.tech_station.insert_upgrade_target", "Insert cyberware to inspect upgrade materials.");
        add("screen.cyberneticenhancements.tech_station.upgrade_unavailable", "This item cannot be upgraded here.");
        add("screen.cyberneticenhancements.tech_station.upgrade_progress_preview", "Upgrades: %s/%s -> %s/%s");
        add("screen.cyberneticenhancements.tech_station.repair_slot_hint", "Insert repairable cyberware here.");
        add("screen.cyberneticenhancements.tech_station.upgrade_slot_hint", "Insert upgradeable cyberware here.");
        add("screen.cyberneticenhancements.recycler_station.recycler_bay", "Recycler Station");
        add("screen.cyberneticenhancements.recycler_station.scrap_input", "Scrap Input");
        add("screen.cyberneticenhancements.recycler_station.reclaimed_output", "Recovered Output");
        add("screen.cyberneticenhancements.recycler_station.recycle_target", "Recycle Target");
        add("screen.cyberneticenhancements.recycler_station.recovered_parts", "Recovered Parts");
        add("screen.cyberneticenhancements.recycler_station.insert_recycle_target", "Insert cyberware, chips, modules, or consumables to break them down.");
        add("screen.cyberneticenhancements.recycler_station.recovered_parts_hint", "Recovered materials appear here once a valid item is loaded.");
        add("screen.cyberneticenhancements.recycler_station.no_recycle_output", "Insert cyberware, chips, modules, or consumables to recycle them.");
        add("screen.cyberneticenhancements.recycler_station.recovery", "Recovery Yield");
        add("screen.cyberneticenhancements.recycler_station.input_slot_hint", "Insert recyclable cyberware here.");
        add("hud.cyberneticenhancements.chrome", "Chrome");
        add("hud.cyberneticenhancements.cyberstrain", "Cyberstrain");
        add("hud.cyberneticenhancements.statuses", "Statuses");
        add("hud.cyberneticenhancements.cooldowns", "Cooldowns");
        add("hud.cyberneticenhancements.ready", "Ready");
        add("hud.cyberneticenhancements.cooldown_short", "CD %ss");
        add("hud.cyberneticenhancements.ability_offline", "No active ability");
        add("hud.cyberneticenhancements.suppression", "Suppression %s");
        add("hud.cyberneticenhancements.ram_jolt", "RAM Jolt x%s");
        add("hud.cyberneticenhancements.ability.sandevistan", "Sandevistan");
        add("hud.cyberneticenhancements.ability.berserk", "Berserk");
        add("hud.cyberneticenhancements.ability.biodyn_berserk", "BioDyne");
        add("hud.cyberneticenhancements.ability.militech_berserk", "Militech");
        add("hud.cyberneticenhancements.ability.moore_tech_berserk", "Moore Tech");
        add("hud.cyberneticenhancements.ability.zetatech_berserk", "Zetatech");
        add("hud.cyberneticenhancements.ability.arasaka_shadow", "Shadow");
        add("hud.cyberneticenhancements.ability.biotech_sigma", "Sigma");
        add("hud.cyberneticenhancements.ability.militech_paraline", "Paraline");
        add("hud.cyberneticenhancements.ability.netwatch_netdriver", "Netdriver");
        add("hud.cyberneticenhancements.ability.raven_microcyber", "Raven");
        add("hud.cyberneticenhancements.ability.tetratronic_rippler", "Rippler");
        add("hud.cyberneticenhancements.ability.militech_canto", "Canto");
        add("hud.cyberneticenhancements.ability.dynalar_sandevistan", "Dynalar");
        add("hud.cyberneticenhancements.ability.zetatech_sandevistan", "Zetatech");
        add("hud.cyberneticenhancements.ability.militech_falcon", "Falcon");
        add("hud.cyberneticenhancements.ability.qiant_warp_dancer", "Warp Dancer");
        add("hud.cyberneticenhancements.ability.militech_apogee", "Apogee");
        add("hud.cyberneticenhancements.state.stable", "Stable");
        add("hud.cyberneticenhancements.state.strained", "Strained");
        add("hud.cyberneticenhancements.state.unstable", "Unstable");
        add("hud.cyberneticenhancements.state.critical", "Critical");
        add("hud.cyberneticenhancements.state.episode_risk", "Episode Risk");
        add("hud.cyberneticenhancements.state.aftershock", "Aftershock");
        add("hud.cyberneticenhancements.state.psychotic", "Psychotic");
    }

    private void addMaterialTranslations() {
        addItem(ModItems.COPPER_WIRING, "Copper Wiring");
        addItem(ModItems.CONDUCTIVE_PASTE, "Conductive Paste");
        addItem(ModItems.SERVO_SCREWS, "Servo Screws");
        addItem(ModItems.SENSOR_LENS, "Sensor Lens");
        addItem(ModItems.MICRO_BATTERY, "Micro Battery");
        addItem(ModItems.REPLACEMENT_JOINT, "Replacement Joint");
        addItem(ModItems.BASIC_CIRCUIT_PLATE, "Basic Circuit Plate");
        addItem(ModItems.COMMON_ITEM_COMPONENTS, "Common Item Components");
        addItem(ModItems.UNCOMMON_ITEM_COMPONENTS, "Uncommon Item Components");
        addItem(ModItems.RARE_ITEM_COMPONENTS, "Rare Item Components");
        addItem(ModItems.EPIC_ITEM_COMPONENTS, "Epic Item Components");
        addItem(ModItems.LEGENDARY_ITEM_COMPONENTS, "Legendary Item Components");
        addItem(ModItems.WHOS_READY_FOR_TOMORROW_MUSIC_DISC, "Music Disc");
        add("jukebox_song.cyberneticenhancements.whos_ready_for_tomorrow_instrumental", "RAT BOY - Who's Ready for Tomorrow (Instrumental)");
    }

    private void addConsumableTranslations() {
        add("tooltip.cyberneticenhancements.consumable_category.medical", "Medical");
        add("tooltip.cyberneticenhancements.consumable_category.booster", "Booster");
        add("tooltip.cyberneticenhancements.consumable_category.neural", "Neural");
        add("tooltip.cyberneticenhancements.consumable_category.suppressant", "Suppressant");
        add("tooltip.cyberneticenhancements.consumable_category.street", "Street");

        for (CyberConsumableDefinition definition : CyberConsumableCatalog.definitions()) {
            addItem(ModItems.consumable(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addChipwareTranslations() {
        for (ChipwareDefinition definition : ChipwareCatalog.definitions()) {
            addItem(ModItems.chipware(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addModuleTranslations() {
        add("tooltip.cyberneticenhancements.module_category.arms", "Arms");
        add("tooltip.cyberneticenhancements.module_category.legs", "Legs");

        for (CyberwareModuleDefinition definition : CyberwareModuleCatalog.definitions()) {
            addItem(ModItems.module(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addCyberwareTranslations() {
        for (CyberwareDefinition definition : CyberwareCatalog.definitions()) {
            addItem(ModItems.cyberware(definition.id()), definition.displayName());
            add(definition.descriptionKey(), definition.description());
        }
    }

    private void addBlockTranslations() {
        addBlock(ModBlocks.RIPPER_STATION, "Ripper Station");
        addBlock(ModBlocks.TECH_STATION, "Tech Station");
        addBlock(ModBlocks.RECYCLER_STATION, "Recycler Station");
    }

    private static String humanizeSlot(CyberwareSlotType type) {
        return switch (type) {
            case FRONTAL_CORTEX -> "Frontal Cortex";
            case OPERATING_SYSTEM -> "Operating System";
            case ARMS -> "Arms";
            case FACE -> "Face";
            case SKELETON -> "Skeleton";
            case HANDS -> "Hands";
            case NERVOUS_SYSTEM -> "Nervous System";
            case CIRCULATORY_SYSTEM -> "Circulatory System";
            case INTEGUMENTARY_SYSTEM -> "Integumentary System";
            case LEGS -> "Legs";
        };
    }
}
