package ralf2oo2.portalgun.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import ralf2oo2.portalgun.portal.PortalInfo;

import java.util.HashMap;
import java.util.Map;

public class PortalState extends PersistentState {

    public static final String DATA_ID = "PortalGunClassicSaveData";

    public HashMap<Integer, HashMap<String, PortalInfo>> portalInfo = new HashMap<>();

    public PortalState(String id) {
        super(id);
    }

    public void set(World world, boolean orange, BlockPos pos)
    {
        HashMap<String, PortalInfo> map = portalInfo.computeIfAbsent(world.dimension.id, k -> new HashMap<>());
        map.put(orange ? "orange" : "blue", new PortalInfo(orange, pos));
        this.markDirty();
        //PortalGunClassic.channel.sendToDimension(new PacketPortalStatus(map.containsKey("blue"), map.containsKey("orange")), world.provider.getDimension());
    }

    public void kill(World world, boolean orange)
    {
        HashMap<String, PortalInfo> map = portalInfo.get(world.dimension.id);
        if(map != null)
        {
            PortalInfo info = map.get(orange ? "orange" : "blue");
            if(info != null)
            {
                info.kill(world);
                map.remove(orange ? "orange" : "blue");
                if(map.isEmpty())
                {
                    portalInfo.remove(world.dimension.id);
                }
                markDirty();
            }
            //PortalGunClassic.channel.sendToDimension(new PacketPortalStatus(map.containsKey("blue"), map.containsKey("orange")), world.provider.getDimension());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        int count = nbt.getInt("dimCount");
        for(int i = 0; i < count; i++)
        {
            NbtCompound dimTag = nbt.getCompound("dim" + i);
            HashMap<String, PortalInfo> map = new HashMap<>();
            if(dimTag.contains("blue"))
            {
                map.put("blue", PortalInfo.createFromNBT(dimTag.getCompound("blue")));
            }
            if(dimTag.contains("orange"))
            {
                map.put("orange", PortalInfo.createFromNBT(dimTag.getCompound("orange")));
            }
            if(!map.isEmpty())
            {
                portalInfo.put(dimTag.getInt("dim"), map);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("dimCount", portalInfo.size());
        int i = 0;
        for(Map.Entry<Integer, HashMap<String, PortalInfo>> e : portalInfo.entrySet())
        {
            NbtCompound dimTag = new NbtCompound();
            dimTag.putInt("dim", e.getKey());
            if(e.getValue().containsKey("blue"))
            {
                dimTag.put("blue", e.getValue().get("blue").toNBT());
            }
            if(e.getValue().containsKey("orange"))
            {
                dimTag.put("orange", e.getValue().get("orange").toNBT());
            }

            nbt.put("dim" + i, dimTag);
            i++;
        }
    }
}
