package ValkyrienWarfareBase.PhysicsManagement;

import javax.annotation.Nullable;

import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This entity's only purpose is to use the functionality of sending itself
 * to nearby players, all other operations are handled by the PhysicsObject
 * class
 * @author thebest108
 *
 */
public class PhysicsWrapperEntity extends Entity implements IEntityAdditionalSpawnData{

	public PhysicsObject wrapping;
	public double pitch;
	public double yaw;
	public double roll;
	
	public double prevPitch;
	public double prevYaw;
	public double prevRoll;
	
	private static final AxisAlignedBB zeroBB = new AxisAlignedBB(0,0,0,0,0,0);

	public PhysicsWrapperEntity(World worldIn) {
		super(worldIn);
		wrapping = new PhysicsObject(this);
	}
	
	public PhysicsWrapperEntity(World worldIn,double x,double y,double z,@Nullable EntityPlayer maker, int detectorID) {
		this(worldIn);
		posX = x;
		posY = y;
		posZ = z;
		wrapping.creator = maker;
		wrapping.detectorID = detectorID;
		wrapping.processChunkClaims();
	}

	@Override
	public void onUpdate(){
		if(isDead){
			return;
		}
//		super.onUpdate();
		wrapping.onTick();
	}
	
	@Override
	public void updatePassenger(Entity passenger){
		Vector passengerPosition = new Vector(passenger.posX,passenger.posY,passenger.posZ);
		RotationMatrices.applyTransform(wrapping.coordTransform.prevwToLTransform, passengerPosition);
		RotationMatrices.applyTransform(wrapping.coordTransform.lToWTransform, passengerPosition);
//		passenger.posX = passengerPosition.X;
//		passenger.posY = passengerPosition.Y;
//		passenger.posZ = passengerPosition.Z;
		passenger.dismountRidingEntity();
	}
	
	@Override
	protected void entityInit() {
		
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox(){
		return wrapping.collisionBB;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
		return wrapping.collisionBB;
    }
	
	@Override
	public void setPosition(double x, double y, double z){}
	
	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch){}
	
	@Override
	@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport){}
	
	@Override
	public void setPositionAndUpdate(double x, double y, double z){}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		wrapping.readFromNBTTag(tagCompund);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {
		wrapping.writeToNBTTag(tagCompound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		wrapping.preloadNewPlayers();
		wrapping.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		wrapping.readSpawnData(additionalData);
	}
}
