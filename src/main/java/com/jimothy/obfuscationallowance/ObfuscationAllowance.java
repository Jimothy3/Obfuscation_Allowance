package com.jimothy.obfuscationallowance;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ObfuscationAllowance.MODID)
@Mod.EventBusSubscriber(modid = ObfuscationAllowance.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ObfuscationAllowance {

    // mod id ref place
    public static final String MODID = "obfuscationallowance";

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        String input = event.getName();

        if (input == null || input.isBlank()) return;

        Component name = parseFormattingCodes(input);

        ItemStack output = event.getLeft().copy();
        output.set(DataComponents.CUSTOM_NAME, name);

        event.setOutput(output);
        event.setCost(1);
    }

    public static Component parseFormattingCodes(String input) {
        MutableComponent result = Component.empty();

        boolean obfuscated = false;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '&' && i + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(i + 1));

                // flush buffered plain text using current style
                if (!buffer.isEmpty()) {
                    MutableComponent part = Component.literal(buffer.toString());
                    if (obfuscated) {
                        part = part.withStyle(ChatFormatting.OBFUSCATED);
                    }
                    result.append(part);
                    buffer.setLength(0);
                }

                // handle formatting codes
                if (code == 'k') {
                    obfuscated = true;
                    i++; // skip the code char
                    continue;
                } else if (code == 'r') {
                    obfuscated = false;
                    i++; // skip the code char
                    continue;
                }
            }

            buffer.append(c);
        }

        // flush remaining text
        if (!buffer.isEmpty()) {
            MutableComponent part = Component.literal(buffer.toString());
            if (obfuscated) {
                part = part.withStyle(ChatFormatting.OBFUSCATED);
            }
            result.append(part);
        }

        return result;
    }
}
