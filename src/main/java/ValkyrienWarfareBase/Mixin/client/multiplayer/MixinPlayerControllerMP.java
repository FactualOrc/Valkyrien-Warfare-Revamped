package ValkyrienWarfareBase.Mixin.client.multiplayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraftforge.common.ForgeHooks;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

	@Overwrite
	public EnumActionResult processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
        this.syncCurrentPlayItem();
        ItemStack itemstack = player.getHeldItem(hand);
        float f = (float)(vec.xCoord - (double)pos.getX());
        float f1 = (float)(vec.yCoord - (double)pos.getY());
        float f2 = (float)(vec.zCoord - (double)pos.getZ());
        boolean flag = false;

        if (!this.mc.world.getWorldBorder().contains(pos))
        {
            return EnumActionResult.FAIL;
        }
        else
        {

//        	PhysicsWrapperEntity originalHitVecWrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldIn, pos);
//        	if(originalHitVecWrapper != null) {
//        		//Fix for Chisels and Bits
//        		vec = RotationMatrices.applyTransform(originalHitVecWrapper.wrapping.coordTransform.wToLTransform, vec);
//
//        		f = (float)(vec.xCoord - (double)pos.getX());
//                f1 = (float)(vec.yCoord - (double)pos.getY());
//                f2 = (float)(vec.zCoord - (double)pos.getZ());
//        	}


        	Vec3d preTransform = null;
//        	Vec3d preTransform = net.minecraftforge.common.ForgeHooks.rayTraceEyeHitVec(player, getBlockReachDistance() + 1);

        	RayTraceResult rayTrace = ForgeHooks.rayTraceEyes(player, getBlockReachDistance() + 1);

        	if(rayTrace != null) {
        		preTransform = rayTrace.hitVec;
        		BlockPos posRayHit = rayTrace.getBlockPos();
        		if(pos != null && !pos.equals(BlockPos.ORIGIN)) {
        			PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldIn, posRayHit);
        			if(wrapper != null) {
        				//Fix for Chisels and Bits
//        				preTransform = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.wToLTransform, preTransform);
        			}
        		}
        	}

            net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks
                    .onRightClickBlock(player, hand, pos, direction, preTransform);
            if (event.isCanceled())
            {
                // Give the server a chance to fire event as well. That way server event is not dependant on client event.
                this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
                return event.getCancellationResult();
            }
            EnumActionResult result = EnumActionResult.PASS;

            if (this.currentGameType != GameType.SPECTATOR)
            {
                EnumActionResult ret = itemstack.onItemUseFirst(player, worldIn, pos, hand, direction, f, f1, f2);
                if (ret != EnumActionResult.PASS) return ret;

                IBlockState iblockstate = worldIn.getBlockState(pos);
                boolean bypass = itemstack.isEmpty() || itemstack.getItem().doesSneakBypassUse(itemstack, worldIn, pos, player);

                if ((!player.isSneaking() || bypass || event.getUseBlock() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW))
                {
                    if (event.getUseBlock() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                    flag = iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, hand, direction, f, f1, f2);
                    if (flag) result = EnumActionResult.SUCCESS;
                }

                if (!flag && itemstack.getItem() instanceof ItemBlock)
                {
                    ItemBlock itemblock = (ItemBlock)itemstack.getItem();

                    if (!itemblock.canPlaceBlockOnSide(worldIn, pos, direction, player, itemstack))
                    {
                        return EnumActionResult.FAIL;
                    }
                }
            }

            this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));

            if (!flag && this.currentGameType != GameType.SPECTATOR || event.getUseItem() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW)
            {
                if (itemstack.isEmpty())
                {
                    return EnumActionResult.PASS;
                }
                else if (player.getCooldownTracker().hasCooldown(itemstack.getItem()))
                {
                    return EnumActionResult.PASS;
                }
                else
                {
                    if (itemstack.getItem() instanceof ItemBlock && !player.canUseCommandBlock())
                    {
                        Block block = ((ItemBlock)itemstack.getItem()).getBlock();

                        if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                        {
                            return EnumActionResult.FAIL;
                        }
                    }

                    if (this.currentGameType.isCreative())
                    {
                        int i = itemstack.getMetadata();
                        int j = itemstack.getCount();
                        if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                        EnumActionResult enumactionresult = itemstack.onItemUse(player, worldIn, pos, hand, direction, f, f1, f2);
                        itemstack.setItemDamage(i);
                        itemstack.setCount(j);
                        return enumactionresult;
                        } else return result;
                    }
                    else
                    {
                        ItemStack copyForUse = itemstack.copy();
                        if (event.getUseItem() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                        result = itemstack.onItemUse(player, worldIn, pos, hand, direction, f, f1, f2);
                        if (itemstack.isEmpty()) net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyForUse, hand);
                        return result;
                    }
                }
            }
            else
            {
                return EnumActionResult.SUCCESS;
            }
        }
    }

	@Shadow
    public abstract void syncCurrentPlayItem();
	@Shadow
    public abstract float getBlockReachDistance();
	@Shadow @Final
    public Minecraft mc;
	@Shadow @Final
    public NetHandlerPlayClient connection;
	@Shadow
    public GameType currentGameType;

}
