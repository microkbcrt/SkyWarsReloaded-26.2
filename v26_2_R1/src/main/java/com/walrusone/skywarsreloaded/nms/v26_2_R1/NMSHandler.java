package com.walrusone.skywarsreloaded.nms.v26_2_R1;

import org.bukkit.GameRule;
import org.bukkit.World;

public class NMSHandler
        extends com.walrusone.skywarsreloaded.nms.v1_21_R1.NMSHandler {

    @Override
    public void setGameRule(World world, String ruleName, String value) {
        try {
            switch (ruleName) {
                case "doMobSpawning":
                case "minecraft:spawn_mobs":
                    setBooleanGameRule(
                            world,
                            GameRule.SPAWN_MOBS,
                            ruleName,
                            value
                    );
                    return;

                case "mobGriefing":
                case "minecraft:mob_griefing":
                    setBooleanGameRule(
                            world,
                            GameRule.MOB_GRIEFING,
                            ruleName,
                            value
                    );
                    return;

                case "showDeathMessages":
                case "minecraft:show_death_messages":
                    setBooleanGameRule(
                            world,
                            GameRule.SHOW_DEATH_MESSAGES,
                            ruleName,
                            value
                    );
                    return;

                case "announceAdvancements":
                case "minecraft:show_advancement_messages":
                    setBooleanGameRule(
                            world,
                            GameRule.SHOW_ADVANCEMENT_MESSAGES,
                            ruleName,
                            value
                    );
                    return;

                case "doDaylightCycle":
                case "minecraft:advance_time":
                    setBooleanGameRule(
                            world,
                            GameRule.ADVANCE_TIME,
                            ruleName,
                            value
                    );
                    return;

                case "doFireTick":
                case "minecraft:fire_spread_radius_around_player":
                    setFireSpreadGameRule(world, ruleName, value);
                    return;

                default:
                    /*
                     * 当前 Core 只调用上面的六条规则。
                     * 对其他规则保留旧 handler 的行为。
                     */
                    super.setGameRule(world, ruleName, value);
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    private void setBooleanGameRule(
            World world,
            GameRule<Boolean> gameRule,
            String ruleName,
            String value
    ) {
        if (!"true".equalsIgnoreCase(value)
                && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException(
                    "Invalid boolean GameRule value: "
                            + ruleName + " -> " + value
            );
        }

        world.setGameRule(gameRule, Boolean.parseBoolean(value));
    }

    private void setFireSpreadGameRule(
            World world,
            String ruleName,
            String value
    ) {
        final int radius;

        if ("false".equalsIgnoreCase(value)) {
            // 旧 doFireTick=false 等同于禁止火焰蔓延。
            radius = 0;
        } else if ("true".equalsIgnoreCase(value)) {
            /*
             * 不硬编码一个猜测的半径。
             * 使用当前 Spigot 版本为该规则提供的默认值。
             */
            radius = GameRule.FIRE_SPREAD_RADIUS_AROUND_PLAYER
                    .getDefaultValue();
        } else {
            try {
                radius = Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid integer GameRule value: "
                                + ruleName + " -> " + value,
                        exception
                );
            }
        }

        world.setGameRule(
                GameRule.FIRE_SPREAD_RADIUS_AROUND_PLAYER,
                radius
        );
    }

    @Override
    public int getVersion() {
        return 26;
    }
}
