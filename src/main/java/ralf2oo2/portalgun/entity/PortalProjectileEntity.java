package ralf2oo2.portalgun.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;
import ralf2oo2.portalgun.PortalGun;
import ralf2oo2.portalgun.block.PortalBlock;
import ralf2oo2.portalgun.block.entity.PortalBlockEntity;
import ralf2oo2.portalgun.events.init.BlockRegistry;
import ralf2oo2.portalgun.util.Util;

public class PortalProjectileEntity extends Entity {
    private int blockX;
    private int blockY;
    private int blockZ;
    private int inBlock;



    public PortalProjectileEntity(World world) {
        super(world);
        setBoundingBoxSpacing(0.5F, 0.5F);
        this.renderDistanceMultiplier = 10.0D;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(16, 361215);
        this.dataTracker.startTracking(17, 1);
        this.dataTracker.startTracking(18, "default");
    }

    public int getRenderColor(){
        return this.dataTracker.getInt(16);
    }

    public void setRenderColor(int color){
        this.dataTracker.set(16, color);
    }

    public int getType(){
        return this.dataTracker.getInt(17);
    }

    public void setType(int type){
        this.dataTracker.set(17, type);
    }

    public String getOwner(){
        return this.dataTracker.getString(18);
    }

    public void setOwner(String owner){
        this.dataTracker.set(18, owner);
    }

    public void setHeading(double d, double d1, double d2, float f) {
        float f2 = MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d *= (double)f;
        d1 *= (double)f;
        d2 *= (double)f;
        this.velocityX = d;
        this.velocityY = d1;
        this.velocityZ = d2;
        float f3 = MathHelper.sqrt(d * d + d2 * d2);
        this.prevYaw = this.yaw = (float)(Math.atan2(d, d2) * 180.0D / (double)(float)Math.PI);
        this.prevPitch = this.pitch = (float)(Math.atan2(d1, (double)f3) * 180.0D / (double)(float)Math.PI);
    }

    public void setVelocity(double d, double d1, double d2) {
        this.velocityX = d;
        this.velocityY = d1;
        this.velocityZ = d2;
        if(this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float f = MathHelper.sqrt(d * d + d2 * d2);
            this.prevYaw = this.yaw = (float)(Math.atan2(d, d2) * 180.0D / (double)(float)Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(d1, (double)f) * 180.0D / (double)(float)Math.PI);
            this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        }

    }

    public boolean spawnBlock(int x, int y, int z, int blockID, int side) {
        if(y >= 0 && (y != 0 || side != 0)) {
            int ii = x;
            int kk = z;
            if(true) { // blockID != Block.VINE.blockID && blockID != Block.tallGrass.blockID probably for checking which blocks the portal projectile can pass though
                this.markDead();
                byte ptype = 2;
                if(side == 0) {
                    --y;
                    ptype = 0;
                } else if(side == 1) {
                    ++y;
                    ptype = 1;
                } else if(side == 2) {
                    --z;
                } else if(side == 3) {
                    ++z;
                } else if(side == 4) {
                    --x;
                } else if(side == 5) {
                    ++x;
                }

                this.world.getBlockId(x, y, z);
                byte convGelSide1 = -1;
                byte convGelSide2 = -1;
                int convGelMeta1 = -1;
                int convGelMeta2 = -1;
                BlockEntity blockEntity;
                PortalBlockEntity portalBlockEntity;
                if(side <= 1) {
                    int te = MathHelper.floor((double)((this.yaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                    if(te == 0) {
                        --ii;
                    } else if(te == 1) {
                        ++kk;
                    } else if(te == 2) {
                        ++ii;
                    } else if(te == 3) {
                        --kk;
                    }

                    this.checkForRemovablePortal(x, y, z);

                    try {
                        Class.forName("mod_TheFGels");
                        Class.forName("FLabs_TileEntityGel");
                        if(this.world.getBlockId(x, y, z) == mod_TheFGels.gel.blockID) {
                            blockEntity = this.world.getBlockEntity(x, y, z);
                            if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                convGelMeta1 = this.world.getBlockMetadata(x, y, z);
                                convGelSide1 = ((FLabs_TileEntityGel)blockEntity).Gelside;
                                if(!this.world.isRemote) {
                                    this.world.setBlock(x, y, z, 0);
                                }

                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta1 == 2;
                            }
                        }
                    } catch (Throwable throwable25) {
                    }

                    if(this.world.canBlockBePlacedAt(BlockRegistry.portalBlock.id, x, y, z, true, side)) {
                        ((PortalBlock)BlockRegistry.portalBlock).isOnConversionGel = false;
                        this.checkForRemovablePortal(ii, y, kk);

                        try {
                            Class.forName("mod_TheFGels");
                            Class.forName("FLabs_TileEntityGel");
                            if(this.world.getBlockId(ii, y, kk) == mod_TheFGels.gel.blockID) {
                                blockEntity = this.world.getBlockTileEntity(ii, y, kk);
                                if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                    convGelMeta2 = this.world.getBlockMetadata(ii, y, kk);
                                    convGelSide2 = ((FLabs_TileEntityGel)blockEntity).Gelside;
                                    if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(ii, y, kk, 0);
                                    }

                                    ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta2 == 2;
                                }
                            }
                        } catch (Throwable throwable24) {
                        }

                        BlockEntity blockEntity1;
                        PortalBlockEntity portalBlockEntity1;
                        if(this.world.canPlace(BlockRegistry.portalBlock.id, ii, y, kk, true, side)) {
                            ((PortalBlock)BlockRegistry.portalBlock).isOnConversionGel = false;
                            if(this.world.isRemote) {
                                return true;
                            }

                            blockEntity = null;
                            blockEntity1 = null;
                            if(this.world.getBlockId(x, y, z) == BlockRegistry.portalBlock.id) {
                                blockEntity = this.world.getBlockEntity(x, y, z);
                                if(blockEntity == null || !(blockEntity instanceof PortalBlockEntity)) {
                                    blockEntity = null;
                                }
                            } else {
                                this.world.setBlock(x, y, z, BlockRegistry.portalBlock.id);
                                blockEntity = this.world.getBlockEntity(x, y, z);
                            }

                            if(this.world.getBlockId(ii, y, kk) == BlockRegistry.portalBlock.id) {
                                blockEntity1 = this.world.getBlockEntity(ii, y, kk);
                                if(blockEntity1 == null || !(blockEntity instanceof Portal_TileEntityPortal)) {
                                    blockEntity1 = null;
                                }
                            } else {
                                this.world.setBlock(ii, y, kk, BlockRegistry.portalBlock.id);
                                blockEntity1 = this.world.getBlockEntity(ii, y, kk);
                            }

                            if(blockEntity != null && blockEntity1 != null && blockEntity instanceof PortalBlockEntity && blockEntity1 instanceof PortalBlockEntity) {
                                portalBlockEntity = (PortalBlockEntity)blockEntity;
                                portalBlockEntity1 = (PortalBlockEntity)blockEntity1;
                                portalBlockEntity.setupType(ptype, side, te, this.getType(), false, portalBlockEntity1, this.getOwner());
                                portalBlockEntity1.setupType(ptype, side, te, this.getType(), true, portalBlockEntity, this.getOwner());
                                portalBlockEntity.gelSide = convGelSide1;
                                portalBlockEntity1.gelSide = convGelSide2;
                                portalBlockEntity.gelMeta = convGelMeta1;
                                portalBlockEntity1.gelMeta = convGelMeta2;
                                portalBlockEntity.findLink();
                                return true;
                            }
                        } else {
                            ((PortalBlock)mod_PortalGun.blockPortal).isOnConversionGel = false;
                            if(convGelSide2 != -1) {
                                if(!this.world.isRemote) {
                                    this.world.setBlockAndMetadata(ii, y, kk, mod_TheFGels.gel.blockID, convGelMeta2);
                                }

                                blockEntity = this.world.getBlockTileEntity(ii, y, kk);
                                if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                    ((FLabs_TileEntityGel)blockEntity).setGelSide(convGelSide2);
                                } else if(!this.world.isRemote) {
                                    this.world.setBlockWithNotify(ii, y, kk, 0);
                                }
                            }

                            if(te == 0) {
                                ii += 2;
                            } else if(te == 1) {
                                kk -= 2;
                            } else if(te == 2) {
                                ii -= 2;
                            } else if(te == 3) {
                                kk += 2;
                            }

                            this.checkForRemovablePortal(ii, y, kk);

                            try {
                                Class.forName("mod_TheFGels");
                                Class.forName("FLabs_TileEntityGel");
                                if(this.world.getBlockId(ii, y, kk) == mod_TheFGels.gel.blockID) {
                                    blockEntity = this.world.getBlockTileEntity(ii, y, kk);
                                    if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                        convGelMeta2 = this.world.getBlockMetadata(ii, y, kk);
                                        convGelSide2 = ((FLabs_TileEntityGel)blockEntity).Gelside;
                                        if(!this.world.isRemote) {
                                            this.world.setBlockWithNotify(ii, y, kk, 0);
                                        }

                                        ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta2 == 2;
                                    }
                                }
                            } catch (Throwable throwable23) {
                            }

                            if(this.world.canBlockBePlacedAt(mod_PortalGun.blockPortal.blockID, ii, y, kk, true, side)) {
                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                                if(this.world.isRemote) {
                                    return true;
                                }

                                blockEntity = null;
                                blockEntity1 = null;
                                if(this.world.getBlockId(x, y, z) == mod_PortalGun.blockPortal.blockID) {
                                    blockEntity = this.world.getBlockTileEntity(x, y, z);
                                    if(blockEntity == null || !(blockEntity instanceof Portal_TileEntityPortal)) {
                                        blockEntity = null;
                                    }
                                } else {
                                    this.world.setBlockWithNotify(x, y, z, mod_PortalGun.blockPortal.blockID);
                                    blockEntity = this.world.getBlockTileEntity(x, y, z);
                                }

                                if(this.world.getBlockId(ii, y, kk) == mod_PortalGun.blockPortal.blockID) {
                                    blockEntity1 = this.world.getBlockTileEntity(ii, y, kk);
                                    if(blockEntity1 == null || !(blockEntity instanceof Portal_TileEntityPortal)) {
                                        blockEntity1 = null;
                                    }
                                } else {
                                    this.world.setBlockWithNotify(ii, y, kk, mod_PortalGun.blockPortal.blockID);
                                    blockEntity1 = this.world.getBlockTileEntity(ii, y, kk);
                                }

                                if(blockEntity != null && blockEntity1 != null && blockEntity instanceof Portal_TileEntityPortal && blockEntity1 instanceof Portal_TileEntityPortal) {
                                    portalBlockEntity = (Portal_TileEntityPortal)blockEntity;
                                    portalBlockEntity1 = (Portal_TileEntityPortal)blockEntity1;
                                    portalBlockEntity.setupType(ptype, side, te, this.getType(), true, portalBlockEntity1, this.getOwner());
                                    portalBlockEntity1.setupType(ptype, side, te, this.getType(), false, portalBlockEntity, this.getOwner());
                                    portalBlockEntity.gelSide = convGelSide1;
                                    portalBlockEntity1.gelSide = convGelSide2;
                                    portalBlockEntity.gelMeta = convGelMeta1;
                                    portalBlockEntity1.gelMeta = convGelMeta2;
                                    portalBlockEntity1.findLink();
                                    return true;
                                }
                            } else {
                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                                if(convGelSide1 != -1) {
                                    if(!this.world.isRemote) {
                                        this.world.setBlockAndMetadata(x, y, z, mod_TheFGels.gel.blockID, convGelMeta1);
                                    }

                                    blockEntity = this.world.getBlockTileEntity(x, y, z);
                                    if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                        ((FLabs_TileEntityGel)blockEntity).setGelSide(convGelSide1);
                                    } else if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(x, y, z, 0);
                                    }
                                }

                                if(convGelSide2 != -1) {
                                    if(!this.world.isRemote) {
                                        this.world.setBlockAndMetadata(x, y, z, mod_TheFGels.gel.blockID, convGelMeta2);
                                    }

                                    blockEntity = this.world.getBlockTileEntity(ii, y, kk);
                                    if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                                        ((FLabs_TileEntityGel)blockEntity).setGelSide(convGelSide2);
                                    } else if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(ii, y, kk, 0);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    this.checkForRemovablePortal(x, y, z);

                    TileEntity tileEntity26;
                    try {
                        Class.forName("mod_TheFGels");
                        Class.forName("FLabs_TileEntityGel");
                        if(this.world.getBlockId(x, y, z) == mod_TheFGels.gel.blockID) {
                            tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                            if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                convGelMeta1 = this.world.getBlockMetadata(x, y, z);
                                convGelSide1 = ((FLabs_TileEntityGel)tileEntity26).Gelside;
                                if(!this.world.isRemote) {
                                    this.world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
                                }

                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta1 == 2;
                            }
                        }
                    } catch (Throwable throwable22) {
                    }

                    if(this.world.canBlockBePlacedAt(mod_PortalGun.blockPortal.blockID, x, y, z, true, side)) {
                        ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                        this.checkForRemovablePortal(x, y - 1, z);

                        try {
                            Class.forName("mod_TheFGels");
                            Class.forName("FLabs_TileEntityGel");
                            if(this.world.getBlockId(x, y - 1, z) == mod_TheFGels.gel.blockID) {
                                tileEntity26 = this.world.getBlockTileEntity(x, y - 1, z);
                                if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                    convGelMeta2 = this.world.getBlockMetadata(x, y - 1, z);
                                    convGelSide2 = ((FLabs_TileEntityGel)tileEntity26).Gelside;
                                    if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(x, y - 1, z, 0);
                                    }

                                    ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta2 == 2;
                                }
                            }
                        } catch (Throwable throwable21) {
                        }

                        Portal_TileEntityPortal portal_TileEntityPortal27;
                        if(this.world.canBlockBePlacedAt(mod_PortalGun.blockPortal.blockID, x, y - 1, z, true, side)) {
                            ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                            if(this.world.isRemote) {
                                return true;
                            }

                            tileEntity26 = null;
                            blockEntity = null;
                            if(this.world.getBlockId(x, y, z) == mod_PortalGun.blockPortal.blockID) {
                                tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                                if(tileEntity26 == null || !(tileEntity26 instanceof Portal_TileEntityPortal)) {
                                    tileEntity26 = null;
                                }
                            } else {
                                this.world.setBlockWithNotify(x, y, z, mod_PortalGun.blockPortal.blockID);
                                tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                            }

                            if(this.world.getBlockId(x, y - 1, z) == mod_PortalGun.blockPortal.blockID) {
                                blockEntity = this.world.getBlockTileEntity(x, y - 1, z);
                                if(blockEntity == null || !(tileEntity26 instanceof Portal_TileEntityPortal)) {
                                    blockEntity = null;
                                }
                            } else {
                                this.world.setBlockWithNotify(x, y - 1, z, mod_PortalGun.blockPortal.blockID);
                                blockEntity = this.world.getBlockTileEntity(x, y - 1, z);
                            }

                            if(tileEntity26 != null && blockEntity != null && tileEntity26 instanceof Portal_TileEntityPortal && blockEntity instanceof Portal_TileEntityPortal) {
                                portal_TileEntityPortal27 = (Portal_TileEntityPortal)tileEntity26;
                                portalBlockEntity = (Portal_TileEntityPortal)blockEntity;
                                portal_TileEntityPortal27.setupType(ptype, side, 0, this.getType(), true, portalBlockEntity, this.getOwner());
                                portalBlockEntity.setupType(ptype, side, 0, this.getType(), false, portal_TileEntityPortal27, this.getOwner());
                                portal_TileEntityPortal27.gelSide = convGelSide1;
                                portalBlockEntity.gelSide = convGelSide2;
                                portal_TileEntityPortal27.gelMeta = convGelMeta1;
                                portalBlockEntity.gelMeta = convGelMeta2;
                                portalBlockEntity.findLink();
                                return true;
                            }
                        } else {
                            ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                            if(convGelSide2 != -1) {
                                if(!this.world.isRemote) {
                                    this.world.setBlockAndMetadata(ii, y, kk, mod_TheFGels.gel.blockID, convGelMeta2);
                                }

                                tileEntity26 = this.world.getBlockTileEntity(ii, y, kk);
                                if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                    ((FLabs_TileEntityGel)tileEntity26).setGelSide(convGelSide2);
                                } else if(!this.world.isRemote) {
                                    this.world.setBlockWithNotify(ii, y, kk, 0);
                                }
                            }

                            this.checkForRemovablePortal(x, y + 1, z);

                            try {
                                Class.forName("mod_TheFGels");
                                Class.forName("FLabs_TileEntityGel");
                                if(this.world.getBlockId(x, y + 1, z) == mod_TheFGels.gel.blockID) {
                                    tileEntity26 = this.world.getBlockTileEntity(x, y + 1, z);
                                    if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                        convGelMeta2 = this.world.getBlockMetadata(x, y + 1, z);
                                        convGelSide2 = ((FLabs_TileEntityGel)tileEntity26).Gelside;
                                        if(!this.world.isRemote) {
                                            this.world.setBlockWithNotify(x, y + 1, z, 0);
                                        }

                                        ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = convGelMeta2 == 2;
                                    }
                                }
                            } catch (Throwable throwable20) {
                            }

                            if(this.world.canBlockBePlacedAt(mod_PortalGun.blockPortal.blockID, x, y + 1, z, true, side)) {
                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                                if(this.world.isRemote) {
                                    return true;
                                }

                                tileEntity26 = null;
                                blockEntity = null;
                                if(this.world.getBlockId(x, y, z) == mod_PortalGun.blockPortal.blockID) {
                                    tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                                    if(tileEntity26 == null || !(tileEntity26 instanceof Portal_TileEntityPortal)) {
                                        tileEntity26 = null;
                                    }
                                } else {
                                    this.world.setBlockWithNotify(x, y, z, mod_PortalGun.blockPortal.blockID);
                                    tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                                }

                                if(this.world.getBlockId(x, y + 1, z) == mod_PortalGun.blockPortal.blockID) {
                                    blockEntity = this.world.getBlockTileEntity(x, y + 1, z);
                                    if(blockEntity == null || !(tileEntity26 instanceof Portal_TileEntityPortal)) {
                                        blockEntity = null;
                                    }
                                } else {
                                    this.world.setBlockWithNotify(x, y + 1, z, mod_PortalGun.blockPortal.blockID);
                                    blockEntity = this.world.getBlockTileEntity(x, y + 1, z);
                                }

                                if(tileEntity26 != null && blockEntity != null && tileEntity26 instanceof Portal_TileEntityPortal && blockEntity instanceof Portal_TileEntityPortal) {
                                    portal_TileEntityPortal27 = (Portal_TileEntityPortal)tileEntity26;
                                    portalBlockEntity = (Portal_TileEntityPortal)blockEntity;
                                    portal_TileEntityPortal27.setupType(ptype, side, 0, this.getType(), false, portalBlockEntity, this.getOwner());
                                    portalBlockEntity.setupType(ptype, side, 0, this.getType(), true, portal_TileEntityPortal27, this.getOwner());
                                    portal_TileEntityPortal27.gelSide = convGelSide1;
                                    portalBlockEntity.gelSide = convGelSide2;
                                    portal_TileEntityPortal27.gelMeta = convGelMeta1;
                                    portalBlockEntity.gelMeta = convGelMeta2;
                                    portal_TileEntityPortal27.findLink();
                                    return true;
                                }
                            } else {
                                ((Portal_BlockPortal)mod_PortalGun.blockPortal).isOnConversionGel = false;
                                if(convGelSide1 != -1) {
                                    if(!this.world.isRemote) {
                                        this.world.setBlockAndMetadata(x, y, z, mod_TheFGels.gel.blockID, convGelMeta1);
                                    }

                                    tileEntity26 = this.world.getBlockTileEntity(x, y, z);
                                    if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                        ((FLabs_TileEntityGel)tileEntity26).setGelSide(convGelSide1);
                                    } else if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(x, y, z, 0);
                                    }
                                }

                                if(convGelSide2 != -1) {
                                    if(!this.world.isRemote) {
                                        this.world.setBlockAndMetadata(x, y + 1, kk, mod_TheFGels.gel.blockID, convGelMeta2);
                                    }

                                    tileEntity26 = this.world.getBlockTileEntity(x, y + 1, z);
                                    if(tileEntity26 != null && tileEntity26 instanceof FLabs_TileEntityGel) {
                                        ((FLabs_TileEntityGel)tileEntity26).setGelSide(convGelSide2);
                                    } else if(!this.world.isRemote) {
                                        this.world.setBlockWithNotify(x, y + 1, z, 0);
                                    }
                                }
                            }
                        }
                    }
                }

                return false;
            } else {
                return false;
            }
        } else {
            this.markDead();
            return false;
        }
    }

    public void checkForRemovablePortal(int i, int j, int k) {
        if(this.world.getBlockId(i, j, k) == mod_PortalGun.blockPortal.blockID) {
            BlockEntity blockEntity = this.world.getBlockEntity(i, j, k);
            if(blockEntity != null && blockEntity instanceof PortalBlockEntity portalBlockEntity) {
                if(portalBlockEntity.setup && (portalBlockEntity.colour == this.getType() || !portalBlockEntity.owner.equalsIgnoreCase(this.getOwner()))) {
                    portalBlockEntity.remove();
                }
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if(this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float mX = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.prevYaw = this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0D / (double)(float)Math.PI);
            this.prevPitch = this.pitch = (float)(Math.atan2(this.velocityY, (double)mX) * 180.0D / (double)(float)Math.PI);
        }

        double d13 = this.velocityX / 5.0D;
        double mY = this.velocityY / 5.0D;
        double mZ = this.velocityZ / 5.0D;

        for(int f3 = 0; f3 < 5; ++f3) {
            if(this.world.getBlockId((int)this.x, (int)this.y, (int)this.z) == mod_PortalGun.blockFizzler.blockID && ((Portal_BlockFizzler)Block.blocksList[mod_PortalGun.blockFizzler.blockID]).isOn(this.world, (int)this.x, (int)this.y, (int)this.z)) {
                this.markDead();

                for(int i15 = 0; i15 < 35; ++i15) {
                    ModLoader.getMinecraftInstance().effectRenderer.addEffect(new Portal_EntityPortalFX(this.world, this.x, this.y, this.z, this));
                }

                this.world.playSound(this, "portalgun.portal_invalid_surface_", 0.4F, 1.0F);
                return;
            }

            Vec3d vec3d = Vec3d.create(this.x, this.y, this.z);
            Vec3d vec3d1 = Vec3d.create(this.x + d13, this.y + mY, this.z + mZ);
            HitResult hitResult = this.world.raycast(vec3d, vec3d1, false, true);
            vec3d = Vec3d.create(this.x, this.y, this.z);
            vec3d1 = Vec3d.create(this.x + d13, this.y + mY, this.z + mZ);
            if(hitResult != null) {
                vec3d1 = Vec3d.create(hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
            }

            int f;
            if(hitResult != null && hitResult.entity == null) {
                this.blockX = hitResult.blockX;
                this.blockY = hitResult.blockY;
                this.blockZ = hitResult.blockZ;
                this.inBlock = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
                if((this.inBlock != Block.GLASS.id && this.inBlock != Block.thinGlass.blockID || mod_PortalGun.getSettings("canShootPortalsThroughGlass") != 1) && this.inBlock != Block.fenceIron.blockID) {
                    if(!this.spawnBlock(this.blockX, this.blockY, this.blockZ, this.inBlock, hitResult.side)) {
                        for(f = 0; f < 35; ++f) {
                            ModLoader.getMinecraftInstance().effectRenderer.addEffect(new Portal_EntityPortalFX(this.world, hitResult.pos.x, hitResult.pos.y, hitResult.pos.z, this));
                        }

                        this.world.playSound(this, "portalgun.portal_invalid_surface_", 0.4F, 1.0F);
                    } else if(this.getType() == 1) {
                        this.world.playSound(this, "portalgun.portal_open_blue_", 0.3F, 1.0F);
                    } else if(this.getType() == 2) {
                        this.world.playSound(this, "portalgun.portal_open_red_", 0.3F, 1.0F);
                    }
                } else {
                    this.setPosition(hitResult.pos.x + d13 / 4.0D, hitResult.pos.y + mY / 4.0D, hitResult.pos.z + mZ / 4.0D);
                }
                break;
            }

            this.x += d13;
            this.y += mY;
            this.z += mZ;
            this.setPosition(this.x, this.y, this.z);
            if(this.checkWaterCollisions()) {
                for(f = 0; f < 4; ++f) {
                    float f7 = 0.25F;
                    this.world.addParticle("bubble", this.x - this.velocityX * (double)f7, this.y - this.velocityY * (double)f7, this.z - this.velocityZ * (double)f7, this.velocityX, this.velocityY, this.velocityZ);
                }

                float f16 = MathHelper.sqrt(this.velocityX * this.velocityX * (double)0.2F + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ * (double)0.2F) * 0.2F;
                this.world.playSound(this, "random.splash", f16, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.markDead();
                break;
            }

//            if(this.handleLavaMovement()) {
//                this.setDead();
//                break;
//            }
        }

        float f14 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
        this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0D / (double)(float)Math.PI);

        for(this.pitch = (float)(Math.atan2(this.velocityY, (double)f14) * 180.0D / (double)(float)Math.PI); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
        }

        while(this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
        }

        while(this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
        }

        while(this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
        }

        this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
        this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
    }

    @Override
    protected void readNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

    }
}
