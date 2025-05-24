package xyz.mackan.crystallurgy.forge.fluid;

import net.minecraft.util.Identifier;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class BaseFluid extends FluidType {
    protected int color;

    public BaseFluid(int color) {
        super(Properties.create()
                .density(1000)
                .viscosity(1000)
                .temperature(300)
                .lightLevel(0)
        );

        this.color = color;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor() {
                return color;
            }

            @Override
            public Identifier getStillTexture() {
                return Identifier.of("minecraft", "block/water_still");
            }

            @Override
            public Identifier getFlowingTexture() {
                return Identifier.of("minecraft", "block/water_flow");
            }
        });
    }
}
