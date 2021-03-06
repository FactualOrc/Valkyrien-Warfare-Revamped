package ValkyrienWarfareBase.PhysicsManagement;

import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Interaction.EntityDraggable;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PhysicsTickHandler {

    public static void onWorldTickStart(World world) {
        WorldPhysObjectManager manager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(world);

        ArrayList<PhysicsWrapperEntity> toUnload = (ArrayList<PhysicsWrapperEntity>) manager.physicsEntitiesToUnload.clone();
        for (PhysicsWrapperEntity wrapper : toUnload) {
            manager.onUnload(wrapper);
        }

        ArrayList<PhysicsWrapperEntity> physicsEntities = manager.getTickablePhysicsEntities();

        if (!ValkyrienWarfareMod.doSplitting) {
            for (PhysicsWrapperEntity wrapper : physicsEntities) {
                wrapper.wrapping.coordTransform.setPrevMatrices();
                wrapper.wrapping.updateChunkCache();
                // Collections.shuffle(wrapper.wrapping.physicsProcessor.activeForcePositions);
            }
        } else {
//			boolean didSplitOccur = false; for(PhysicsWrapperEntity wrapper:physicsEntities){ if(wrapper.wrapping.processPotentialSplitting()){ didSplitOccur = true; } } if(didSplitOccur){ while(didSplitOccur){ didSplitOccur = false; ArrayList oldPhysicsEntities = physicsEntities; ArrayList<PhysicsWrapperEntity> newPhysicsEntities = (ArrayList<PhysicsWrapperEntity>) manager.physicsEntities.clone(); newPhysicsEntities.removeAll(oldPhysicsEntities); if(newPhysicsEntities.size()!=0){ for(PhysicsWrapperEntity wrapper:newPhysicsEntities){ if(wrapper.wrapping.processPotentialSplitting()){ didSplitOccur = true; } } } } physicsEntities = (ArrayList<PhysicsWrapperEntity>) manager.physicsEntities.clone(); } for(PhysicsWrapperEntity wrapper:physicsEntities){ wrapper.wrapping.coordTransform.setPrevMatrices(); wrapper.wrapping.updateChunkCache(); // Collections.shuffle(wrapper.wrapping.physicsProcessor.activeForcePositions); }
        }

        int iters = ValkyrienWarfareMod.physIter;
        double newPhysSpeed = ValkyrienWarfareMod.physSpeed;
        Vector newGravity = ValkyrienWarfareMod.gravity;
        for (int pass = 0; pass < iters; pass++) {
            // Run PRE-Col
            for (PhysicsWrapperEntity wrapper : physicsEntities) {
                wrapper.wrapping.physicsProcessor.gravity = newGravity;
                wrapper.wrapping.physicsProcessor.rawPhysTickPreCol(newPhysSpeed, iters);
            }

            if (ValkyrienWarfareMod.doShipCollision) {
                for (int i = 0; i < physicsEntities.size(); i++) {
                    PhysicsWrapperEntity first = physicsEntities.get(i);
                    for (int j = i + 1; j < physicsEntities.size(); j++) {
                        PhysicsWrapperEntity second = physicsEntities.get(j);
                        if (first.wrapping.collisionBB.intersectsWith(second.wrapping.collisionBB)) {
                            first.wrapping.physicsProcessor.shipCollision.doShipCollision(second.wrapping);
                        }
                    }
                }

            }

            if (ValkyrienWarfareMod.multiThreadedPhysics) {
                try {
                    ValkyrienWarfareMod.MultiThreadExecutor.invokeAll(manager.physCollisonCallables);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                for (PhysicsWrapperEntity wrapper : physicsEntities) {
                    wrapper.wrapping.physicsProcessor.processWorldCollision();
                }
                for (PhysicsWrapperEntity wrapper : physicsEntities) {
                    wrapper.wrapping.physicsProcessor.rawPhysTickPostCol();
                }
            }
        }

        for (PhysicsWrapperEntity wrapper : physicsEntities) {
            wrapper.wrapping.coordTransform.sendPositionToPlayers();
        }
        EntityDraggable.tickAddedVelocityForWorld(world);
    }

    public static void onWorldTickEnd(World world) {
        WorldPhysObjectManager manager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(world);
        ArrayList<PhysicsWrapperEntity> physicsEntities = manager.getTickablePhysicsEntities();
        for (PhysicsWrapperEntity wrapperEnt : physicsEntities) {
            wrapperEnt.wrapping.onPostTick();
        }
    }

}
