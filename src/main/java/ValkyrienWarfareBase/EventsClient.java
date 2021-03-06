package ValkyrienWarfareBase;

import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Fixes.SoundFixWrapper;
import ValkyrienWarfareBase.Interaction.EntityDraggable;
import ValkyrienWarfareBase.Interaction.IDraggable;
import ValkyrienWarfareBase.Network.PlayerShipRefrenceMessage;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.PhysicsManagement.WorldPhysObjectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class EventsClient {

    private final static Minecraft mc = Minecraft.getMinecraft();

    private static double oldXOff;
    private static double oldYOff;
    private static double oldZOff;

    protected static final Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public static void updatePlayerMouseOver(Entity entity) {
    	if(entity != Minecraft.getMinecraft().player) {
    		return;
    	}
        Minecraft.getMinecraft().entityRenderer.getMouseOver(Minecraft.getMinecraft().getRenderPartialTicks());
    }

    @SubscribeEvent
    public void onPlaySoundEvent(PlaySoundEvent event) {
        ISound sound = event.getSound();
        BlockPos pos = new BlockPos(sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
        PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(Minecraft.getMinecraft().world, pos);

        if (wrapper != null) {
            Vector newSoundLocation = new Vector(sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
            RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWTransform, newSoundLocation);

            SoundFixWrapper soundFix = new SoundFixWrapper(sound, wrapper, newSoundLocation);

            event.setResultSound(soundFix);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTickEvent(ClientTickEvent event) {
        if (mc.world != null) {
            if (!mc.isGamePaused()) {
                WorldPhysObjectManager manager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(mc.world);
                if (event.phase == Phase.END) {
                    for (PhysicsWrapperEntity wrapper : manager.physicsEntities) {
                        wrapper.wrapping.onPostTickClient();
                    }
                    EntityDraggable.tickAddedVelocityForWorld(mc.world);
                }
            }
            if (event.phase == Phase.END) {
                Object o = Minecraft.getMinecraft().player;
                IDraggable draggable = (IDraggable) o;

                if (draggable.getWorldBelowFeet() != null) {
                    PlayerShipRefrenceMessage playerPosMessage = new PlayerShipRefrenceMessage(Minecraft.getMinecraft().player, draggable.getWorldBelowFeet());

                    ValkyrienWarfareMod.physWrapperNetwork.sendToServer(playerPosMessage);
                }
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCameraSetup(CameraSetup event) {

    }

    @SubscribeEvent
    public void onChunkLoadClient(ChunkEvent.Load event) {

    }

    @SubscribeEvent
    public void onChunkUnloadClient(ChunkEvent.Unload event) {

    }

    @SubscribeEvent
    public void onRenderTickEvent(RenderTickEvent event) {
        if (mc.player != null && mc.playerController != null) {
            // if(!(mc.playerController instanceof CustomPlayerControllerMP)){
            // PlayerControllerMP oldController = mc.playerController;
            // mc.playerController = new CustomPlayerControllerMP(mc, mc.getConnection());
            // mc.playerController.setGameType(oldController.getCurrentGameType());
            // }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onDrawBlockHighlightEventFirst(DrawBlockHighlightEvent event) {
        BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
        if (pos != null) {
            PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(Minecraft.getMinecraft().world, pos);
            if (wrapper != null && wrapper.wrapping != null && wrapper.wrapping.renderer != null && wrapper.wrapping.centerCoord != null) {
            	RayTraceResult objectOver = Minecraft.getMinecraft().objectMouseOver;
            	if(objectOver != null && objectOver.hitVec != null) {
            		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
            		oldXOff = buffer.xOffset;
            		oldYOff = buffer.yOffset;
            		oldZOff = buffer.zOffset;
            		wrapper.wrapping.renderer.setupTranslation(event.getPartialTicks());
//            		objectOver.hitVec = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.wToLTransform, objectOver.hitVec);
            	}
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onDrawBlockHighlightEventLast(DrawBlockHighlightEvent event) {
        BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
        if (pos != null) {
            PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(Minecraft.getMinecraft().world, pos);
            if (wrapper != null && wrapper.wrapping != null && wrapper.wrapping.renderer != null && wrapper.wrapping.centerCoord != null) {
            	RayTraceResult objectOver = Minecraft.getMinecraft().objectMouseOver;
            	if(objectOver != null && objectOver.hitVec != null) {
            		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
            		buffer.xOffset = oldXOff;
            		buffer.yOffset = oldYOff;
            		buffer.zOffset = oldZOff;
            		wrapper.wrapping.renderer.inverseTransform(event.getPartialTicks());
//            		objectOver.hitVec = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWTransform, objectOver.hitVec);
            	}
            }
        }
    }
}
