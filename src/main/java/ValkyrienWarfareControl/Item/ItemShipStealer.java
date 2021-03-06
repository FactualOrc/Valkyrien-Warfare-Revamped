package ValkyrienWarfareControl.Item;

import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemShipStealer extends Item {

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List itemInformation, boolean par4) {
        itemInformation.add(TextFormatting.ITALIC + "" + TextFormatting.RED + TextFormatting.ITALIC + "Unfinished until v_0.91_alpha");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos looking = playerIn.rayTrace(playerIn.isCreative() ? 5.0 : 4.5, 1).getBlockPos();
        PhysicsWrapperEntity entity = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(playerIn.getEntityWorld(), looking);

        if (entity != null) {
            String oldOwner = entity.wrapping.creator;
            if (oldOwner == playerIn.entityUniqueID.toString()) {
                playerIn.sendMessage(new TextComponentString("You can't steal your own airship!"));
                return EnumActionResult.SUCCESS;
            }
            switch (entity.wrapping.changeOwner(playerIn)) {
                case ERROR_NEWOWNER_NOT_ENOUGH:
                    playerIn.sendMessage(new TextComponentString("You already own the maximum amount of airships!"));
                    break;
                case ERROR_IMPOSSIBLE_STATUS:
                    playerIn.sendMessage(new TextComponentString("Error! Please report to mod devs."));
                    break;
                case SUCCESS:
                    playerIn.sendMessage(new TextComponentString("You've stolen an airship!"));
                    break;
                case ALREADY_CLAIMED:
                    playerIn.sendMessage(new TextComponentString("You already own that airship!"));
                    break;
            }
            return EnumActionResult.SUCCESS;
        }

        playerIn.sendMessage(new TextComponentString("The block needs to be part of an airship!"));
        return EnumActionResult.SUCCESS;
    }
}
