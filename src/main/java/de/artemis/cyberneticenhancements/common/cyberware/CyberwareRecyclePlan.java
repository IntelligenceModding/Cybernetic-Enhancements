package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record CyberwareRecyclePlan(List<ItemStack> outputs) {
    public CyberwareRecyclePlan {
        List<ItemStack> cleaned = new ArrayList<>(outputs.size());
        for (ItemStack output : outputs) {
            if (!output.isEmpty()) {
                cleaned.add(output.copy());
            }
        }
        outputs = Collections.unmodifiableList(cleaned);
    }

    public static CyberwareRecyclePlan empty() {
        return new CyberwareRecyclePlan(List.of());
    }

    public boolean isAvailable() {
        return !outputs.isEmpty();
    }
}
