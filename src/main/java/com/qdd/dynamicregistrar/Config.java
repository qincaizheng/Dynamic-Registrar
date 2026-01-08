package com.qdd.dynamicregistrar;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    // 是否启用重载
    public static final ModConfigSpec.BooleanValue ENABLE_RELOAD = BUILDER.comment("是否启用重载").define("enableReload", false);
    static final ModConfigSpec SPEC = BUILDER.build();

}
