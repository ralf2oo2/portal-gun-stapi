package ralf2oo2.portalgun.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.PortalBlock;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;
import ralf2oo2.portalgun.events.init.BlockRegistry;

public class PortalProjectileEntity extends Entity {
    public static final int ORANGE = 30;
    public int age = 0;


    public PortalProjectileEntity(World world) {
        super(world);
        setBoundingBoxSpacing(0.3F, 0.3F);
    }

    public PortalProjectileEntity(World world, Entity entity, boolean isOrange){
        this(world);
        this.dataTracker.set(ORANGE, isOrange ? 1 : 0);
        shoot(entity, 4.999F);
        setPositionAndAngles(entity.x, entity.y + entity.getEyeHeight() - (width / 2F), entity.z, entity.yaw, entity.pitch);
    }

    public void setOrange(boolean flag){
        this.dataTracker.set(ORANGE, flag ? 1 : 0);
    }

    public boolean isOrange(){
        return this.dataTracker.getInt(ORANGE) != 0;
    }

    public void shoot(Entity entity, float velocity)
    {
        float f = -MathHelper.sin(entity.yaw * 0.017453292F) * MathHelper.cos(entity.pitch * 0.017453292F);
        float f1 = -MathHelper.sin((entity.pitch) * 0.017453292F);
        float f2 = MathHelper.cos(entity.yaw * 0.017453292F) * MathHelper.cos(entity.pitch * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity);
        this.velocityX += entity.velocityX;
        this.velocityZ += entity.velocityZ;

        if (!entity.onGround)
        {
            this.velocityY += entity.velocityY;
        }
    }

    public void shoot(double x, double y, double z, float velocity)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.yaw = (float)(Math.atan2(x, z) * (180D / Math.PI));
        this.pitch = (float)(Math.atan2(y, f1) * (180D / Math.PI));
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ORANGE, 0);
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        setOrange(nbt.getBoolean("orange"));
    }

    @Override
    public void tick() {
        if(y > world.getHeight() * 2 || y < -world.getBottomY() || age > 1200) //a minute
        {
            markDead();
            return;
        }

        age++;

        this.lastTickX = this.x;
        this.lastTickY = this.y;
        this.lastTickZ = this.z;

        super.tick();

        Vec3d vec31 = Vec3d.create(this.x, this.y, this.z);
        Vec3d vec32 = Vec3d.create(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos;

                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z) || (l == i && i1 == j && j1 == k)) {
                        break;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    Direction direction;

                    if (d3 < d4 && d3 < d5) {
                        direction = i > l ? Direction.WEST : Direction.EAST;
                        vec31 = Vec3d.create(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        direction = j > i1 ? Direction.DOWN : Direction.UP;
                        vec31 = Vec3d.create(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        direction = k > j1 ? Direction.NORTH : Direction.SOUTH;
                        vec31 = Vec3d.create(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (direction == Direction.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (direction == Direction.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (direction == Direction.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    BlockState iblockstate1 = world.getBlockState(blockpos);
                    int meta = world.getBlockMeta(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                    Block block1 = iblockstate1.getBlock();

                    if (iblockstate1.getBlock().getCollisionShape(world, blockpos.getX(), blockpos.getY(), blockpos.getZ()) != PortalBlock.EMPTY_BOX) {
                        if (block1.hasCollision(meta, true)) {
                            HitResult raytraceresult1 = iblockstate1.getBlock().raycast(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), vec31, vec32);
                            if (raytraceresult1 != null) {
                                if (false) // block1 == Blocks.IRON_BARS
                                {
                                    vec31 = Vec3d.create(raytraceresult1.pos.x + (velocityX / 5000D), raytraceresult1.pos.y + (velocityY / 5000D), raytraceresult1.pos.z + (velocityZ / 5000D));
                                } else {
                                    createPortal(raytraceresult1);
                                    markDead();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("orange", isOrange());
    }

    public void createPortal(HitResult hitResult){
        if(!world.isRemote){
            BlockPos pos = new BlockPos(hitResult.blockX, hitResult.blockY, hitResult.blockZ).offset(Direction.byId(hitResult.side));
            Direction sideHit = Direction.byId(hitResult.side);
            if(PortalBlock.canPlace(world, pos, Direction.byId(hitResult.side), isOrange()))
            {
                PortalGun.getState(world).kill(world, isOrange());

                world.setBlockState(pos, BlockRegistry.portalBlock.getDefaultState());
                BlockEntity te = world.getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
                if(te instanceof PortalBlockEntity portalBlockEntity)
                {
                    portalBlockEntity.setup(sideHit.getAxis() != Direction.Axis.Y, isOrange(), sideHit);
                }
                if(sideHit.getAxis() != Direction.Axis.Y)
                {
                    world.setBlockState(pos.down(), BlockRegistry.portalBlock.getDefaultState());
                    te = world.getBlockEntity(pos.down().getX(), pos.down().getY(), pos.down().getZ());
                    if(te instanceof PortalBlockEntity portalBlockEntity)
                    {
                        portalBlockEntity.setup(false, isOrange(), sideHit);
                    }
                }
                PortalGun.getState(world).set(world, isOrange(), sideHit.getAxis() != Direction.Axis.Y ? pos.down() : pos);

                world.playSound(this.x, this.y + (this.height / 2F), this.z, isOrange() ? "openred" : "openblue", 0.3F, 1.0F);
            }
            else
            {
                world.playSound(this.x, this.y + (this.height / 2F), this.z, "invalid", 0.5F, 1.0F);
            }
        }
    }
}
