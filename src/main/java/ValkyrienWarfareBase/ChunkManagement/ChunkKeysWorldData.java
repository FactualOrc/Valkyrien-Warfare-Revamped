package ValkyrienWarfareBase.ChunkManagement;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.ArrayList;

public class ChunkKeysWorldData extends WorldSavedData {

    private static final String key = "ChunkKeys";
    public ArrayList<Integer> avalibleChunkKeys = new ArrayList<Integer>();
    public int chunkKey;

    public ChunkKeysWorldData() {
        super(key);
    }

    public ChunkKeysWorldData(String name) {
        super(name);
    }

    public static ChunkKeysWorldData get(World world) {
        MapStorage storage = world.getPerWorldStorage();
        ChunkKeysWorldData data = (ChunkKeysWorldData) storage.getOrLoadData(ChunkKeysWorldData.class, key);
        if (data == null) {
            data = new ChunkKeysWorldData();
            world.setData(key, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        chunkKey = nbt.getInteger("chunkKey");
        int[] array = nbt.getIntArray("avalibleChunkKeys");
        for (int i : array) {
            avalibleChunkKeys.add(i);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("chunkKey", chunkKey);
        int[] array = new int[avalibleChunkKeys.size()];
        for (int i = 0; i < avalibleChunkKeys.size(); i++) {
            array[i] = avalibleChunkKeys.get(i);
        }
        nbt.setIntArray("avalibleChunkKeys", array);
        return nbt;
    }

}
