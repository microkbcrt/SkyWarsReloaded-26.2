package com.walrusone.skywarsreloaded.nms.v26_2_R1;

import java.lang.reflect.Method;

import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
                            "spawn_mobs",
                            ruleName,
                            value
                    );
                    return;

                case "mobGriefing":
                case "minecraft:mob_griefing":
                    setBooleanGameRule(
                            world,
                            "mob_griefing",
                            ruleName,
                            value
                    );
                    return;

                case "showDeathMessages":
                case "minecraft:show_death_messages":
                    setBooleanGameRule(
                            world,
                            "show_death_messages",
                            ruleName,
                            value
                    );
                    return;

                case "announceAdvancements":
                case "minecraft:show_advancement_messages":
                    setBooleanGameRule(
                            world,
                            "show_advancement_messages",
                            ruleName,
                            value
                    );
                    return;

                case "doDaylightCycle":
                case "minecraft:advance_time":
                    setBooleanGameRule(
                            world,
                            "advance_time",
                            ruleName,
                            value
                    );
                    return;

                case "doFireTick":
                case "minecraft:fire_spread_radius_around_player":
                    setFireSpreadGameRule(world, ruleName, value);
                    return;

                default:
                    super.setGameRule(world, ruleName, value);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void setBooleanGameRule(
            World world,
            String registryKey,
            String originalRuleName,
            String value
    ) {
        if (!"true".equalsIgnoreCase(value)
                && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException(
                    "Invalid boolean GameRule value: "
                            + originalRuleName + " -> " + value
            );
        }

        GameRule<?> gameRule = getGameRule(registryKey);

        setGameRuleUnchecked(
                world,
                gameRule,
                Boolean.parseBoolean(value)
        );
    }

    private void setFireSpreadGameRule(
            World world,
            String originalRuleName,
            String value
    ) {
        GameRule<?> gameRule =
                getGameRule("fire_spread_radius_around_player");

        final int radius;

        if ("false".equalsIgnoreCase(value)) {
            /*
             * 旧 doFireTick=false：
             * 禁止火焰蔓延。
             */
            radius = 0;
        } else if ("true".equalsIgnoreCase(value)) {
            /*
             * 通过反射读取默认值。
             *
             * 原因：
             * Spigot 26.2 的 GameRule 是接口；
             * Paper 26.2 的 GameRule 是抽象类。
             * 直接调用接口方法可能导致进一步的二进制兼容错误。
             */
            radius = getIntegerDefaultValue(gameRule);
        } else {
            try {
                radius = Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid integer GameRule value: "
                                + originalRuleName + " -> " + value,
                        exception
                );
            }
        }

        setGameRuleUnchecked(world, gameRule, radius);
    }

    private GameRule<?> getGameRule(String key) {
        GameRule<?> gameRule = Registry.GAME_RULE.get(
                NamespacedKey.minecraft(key)
        );

        if (gameRule == null) {
            throw new IllegalArgumentException(
                    "Invalid GameRule registry key: minecraft:" + key
            );
        }

        return gameRule;
    }

    private int getIntegerDefaultValue(GameRule<?> gameRule) {
        try {
            /*
             * 不直接调用 gameRule.getDefaultValue()。
             * 反射可以同时适配 Paper 的抽象类实现和
             * Spigot 的接口实现。
             */
            Method method =
                    GameRule.class.getMethod("getDefaultValue");

            Object defaultValue = method.invoke(gameRule);

            if (!(defaultValue instanceof Integer)) {
                throw new IllegalStateException(
                        "Expected integer GameRule default value, got: "
                                + defaultValue
                );
            }

            return (Integer) defaultValue;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Could not read GameRule default value",
                    exception
            );
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setGameRuleUnchecked(
            World world,
            GameRule<?> gameRule,
            Object value
    ) {
        /*
         * GameRule 的具体值类型由注册表键决定。
         * 这里的映射都是固定且已知的：
         *
         * Boolean:
         * spawn_mobs
         * mob_griefing
         * show_death_messages
         * show_advancement_messages
         * advance_time
         *
         * Integer:
         * fire_spread_radius_around_player
         */
        world.setGameRule((GameRule) gameRule, value);
    }

    @Override
    public int getVersion() {
        return 26;
    }
}
