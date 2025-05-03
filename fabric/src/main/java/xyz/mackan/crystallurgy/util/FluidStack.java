package xyz.mackan.crystallurgy.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class FluidStack {
    public FluidVariant fluidVariant;
    public long amount;

    public FluidStack(FluidVariant variant, long amount) {
        this.fluidVariant = variant;
        this.amount = amount;
    }

    public FluidVariant getFluidVariant() {
        return fluidVariant;
    }

    public void setFluidVariant(FluidVariant fluidVariant) {
        this.fluidVariant = fluidVariant;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public static long convertDropletsToMb(long droplets) {
        return (droplets / 81);
    }

    public static long convertMbToDroplets(long mb) {
        return mb * 81;
    }

    public static FluidStack fromJson(JsonObject json) {
        Identifier fluidId = new Identifier(JsonHelper.getString(json, "fluid"));
        Fluid fluid = Registries.FLUID.getOrEmpty(fluidId)
                .orElseThrow(() -> new JsonSyntaxException("Unknown fluid: " + fluidId));

        NbtCompound nbt = null;
        if (json.has("nbt")) {
            try {
                nbt = StringNbtReader.parse(JsonHelper.getString(json, "nbt"));
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid fluid NBT", e);
            }
        }

        long amount = JsonHelper.getLong(json, "amount");
        return new FluidStack(FluidVariant.of(fluid, nbt), amount);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", Registries.FLUID.getId(fluidVariant.getFluid()).toString());
        if (fluidVariant.hasNbt()) {
            json.addProperty("nbt", fluidVariant.getNbt().toString());
        }
        json.addProperty("amount", amount);
        return json;
    }

    public void writePacket(PacketByteBuf buf) {
        fluidVariant.toPacket(buf);
        buf.writeLong(amount);
    }

    public static FluidStack fromPacket(PacketByteBuf buf) {
        FluidVariant variant = FluidVariant.fromPacket(buf);
        long amount = buf.readLong();
        return new FluidStack(variant, amount);
    }

    // This is only here because I can't be arsed to rip this whole thing out yet.
    public FluidUtils.DecodedFluid toDecoded() {
        return new FluidUtils.DecodedFluid(this.getFluidVariant().getFluid(), (int) this.amount);
    }
}