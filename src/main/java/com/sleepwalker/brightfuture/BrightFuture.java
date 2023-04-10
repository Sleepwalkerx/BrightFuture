package com.sleepwalker.brightfuture;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.v2.common.ForgeMod;

import javax.annotation.Nonnull;

@Mod(BrightFuture.MODID)
public class BrightFuture {

    @Nonnull
    public static final String MODID = "brightfuture";

    public BrightFuture(){

        //init ForgeMod
        new ForgeMod();
    }
}
