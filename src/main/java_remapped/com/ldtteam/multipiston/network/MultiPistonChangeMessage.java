package com.ldtteam.multipiston.network;

import com.ldtteam.multipiston.TileEntityMultiPiston;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Message class which handles updating the minecolonies multipiston.
 */
public class MultiPistonChangeMessage implements IMessage
{
    /**
     * The direction it should push or pull rom.
     */
    private Direction input;

    /**
     * The direction it should push or pull rom.
     */
    private Direction output;

    /**
     * The range it should pull to.
     */
    private int range;

    /**
     * The speed it should have.
     */
    private int speed;

    /**
     * The position of the tileEntity.
     */
    private BlockPos pos;

    /**
     * Empty public constructor.
     */
    public MultiPistonChangeMessage()
    {

    }

    /**
     * Constructor to create the 
     * @param pos the position of the block.
     * @param input the way it inputs from.
     * @param output the way it will output to.
     * @param range the range it should work.
     * @param speed the speed it should have.
     */
    public MultiPistonChangeMessage(final BlockPos pos, final Direction input, final Direction output, final int range, final int speed)
    {
        this.pos = pos;
        this.input = input;
        this.range = range;
        this.output = output;
        this.speed = speed;
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
        buf.writeInt(input.ordinal());
        buf.writeInt(output.ordinal());
        buf.writeInt(range);
        buf.writeInt(speed);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
        this.input = Direction.values()[buf.readInt()];
        this.output = Direction.values()[buf.readInt()];
        this.range = buf.readInt();
        this.speed = buf.readInt();
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final Level world = ctxIn.getSender().level();
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof TileEntityMultiPiston)
        {
            ((TileEntityMultiPiston) entity).setInput(input);
            ((TileEntityMultiPiston) entity).setOutput(output);
            ((TileEntityMultiPiston) entity).setRange(range);
            ((TileEntityMultiPiston) entity).setSpeed(speed);
            final BlockState state = world.getBlockState(pos);
            world.sendBlockUpdated(pos, state, state, 0x3);
        }
    }
}
