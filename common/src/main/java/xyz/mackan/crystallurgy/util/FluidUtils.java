package xyz.mackan.crystallurgy.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import xyz.mackan.crystallurgy.CrystallurgyCommon;

import java.util.Objects;


public class FluidUtils {
    public static DecodedFluid fromJson(JsonObject json) {
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

        int amount = JsonHelper.getInt(json, "amount");
        return new DecodedFluid(fluid, amount);
    }

    // Stolen from fabric.
    public static Fluid of(Fluid fluid, @Nullable NbtCompound nbt) {
        Objects.requireNonNull(fluid, "Fluid may not be null.");
        if (!fluid.isStill(fluid.getDefaultState()) && fluid != Fluids.EMPTY) {
            if (!(fluid instanceof FlowableFluid)) {
                Identifier id = Registries.FLUID.getId(fluid);
                throw new IllegalArgumentException("Cannot convert flowing fluid %s (%s) into a still fluid.".formatted(id, fluid));
            }

            FlowableFluid flowable = (FlowableFluid)fluid;
            fluid = flowable.getStill();
        }

        return fluid;
    }

    // Parts Stolen from fabric.
    public static DecodedFluid fromPacket(PacketByteBuf buf) {
        Fluid fluid = Fluids.EMPTY;

        if (!buf.readBoolean()) {
            return new DecodedFluid(fluid, 0);
        } else {
            fluid = (Fluid)Registries.FLUID.get(buf.readVarInt());
            NbtCompound nbt = buf.readNbt();

            fluid = of(fluid, nbt);
        }

        int amount = buf.readInt();

        return new DecodedFluid(fluid, amount);
    }

    public record DecodedFluid(Fluid fluid, int amount) {
        public boolean isBlank() {
            return this.fluid == Fluids.EMPTY;
        }

        // Taken and adapted from fabric
        public void writePacket(PacketByteBuf buf) {
            if (this.isBlank()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeVarInt(Registries.FLUID.getRawId(this.fluid));
                buf.writeInt(this.amount);
            }
        }

        @Override
        public String toString() {
            return String.format("Fluid: %s, amount: %s", this.fluid, this.amount);
        }
    }
}
