package ralf2oo2.portalgun.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ralf2oo2.portalgun.events.init.BlockRegistry;
import ralf2oo2.portalgun.mixin.EntityAccessor;

import java.util.ArrayList;
import java.util.List;

public class PortalBlockEntity extends BlockEntity {
    public int set = 1;
    public int type = 1;
    public int colour = 1;
    public int sideOn = 1;
    public int cHex = 361215;
    public int spSet = 1;
    public int spType = 1;
    public int spColour = 1;
    public int spSideOn = 1;
    public int gelSide = -1;
    public int gelMeta = -1;
    public boolean top = false;
    public boolean setup = false;
    public boolean firstUpdate = true;
    public boolean portalling = false;
    public boolean isSpawner = false;
    public boolean spTop = false;
    public boolean powered = false;
    public boolean inSpace = false;
    public boolean linkInSpace = false;
    public boolean spawnerPowered = true;
    public String owner = "default";
    public String spOwner = "default";
    public NbtCompound nbt = null;
    public List portalledEntities = new ArrayList();
    public List suckedEntities = new ArrayList();
    public Portal_TileEntityPortalRenderer.TextureRender txRender = null;
    public PortalBlockEntity tepPair = null;
    public PortalBlockEntity tepLink = null;
    public PortalBlockEntity spPair = null;

    public String getInvName(){
        return "Portal Block";
    }

    public void setPosition(Entity entity, double d, double d1, double d2) {
        if(entity != null) {
            entity.lastTickX = entity.prevX = entity.x = d;
            entity.lastTickY = entity.prevY = entity.y = d1;
            entity.lastTickZ = entity.prevZ = entity.z = d2;
            entity.setPosition(d, d1, d2);
        }
    }

    //TODO add turret support back
    protected void setRotation(Entity entity, float f, float f1) {
        if(entity != null) {
//            if(entity instanceof Portal_EntityTurret) {
//                float f2 = ((Portal_EntityTurret)entity).renderYawOffset - entity.yaw;
//                ((Portal_EntityTurret)entity).updateRenderYaw(f);
//                ((Portal_EntityTurret)entity).prevRenderYawOffset = f;
//                entity.yaw = ((Portal_EntityTurret)entity).renderYawOffset - f2;
//                entity.prevYaw = ((Portal_EntityTurret)entity).renderYawOffset - f2;
//            }

            entity.prevYaw = f % 360.0F;
            entity.prevPitch = f1 % 360.0F;
            entity.yaw = f % 360.0F;
            entity.pitch = f1 % 360.0F;
        }
    }

    public void setupType(int pset, int side, int yawDir, int clr, boolean isTop, PortalBlockEntity portalBlockEntity, String Owner) {
        this.setupType(pset, side, yawDir, clr, isTop, portalBlockEntity, false, Owner);
    }

    public void setupType(int pset, int side, int yawDir, int clr, boolean isTop, PortalBlockEntity portalBlockEntity, boolean bySpawner, String Owner) {
        this.set = pset;
        this.colour = clr;
        this.top = isTop;
        this.sideOn = side;
        this.owner = Owner;
        this.cHex = this.getCHex(this.owner, this.colour);
        this.tepPair = portalBlockEntity;
        if(bySpawner) {
            this.type = yawDir;
        } else if(side <= 1) {
            if(yawDir == 0) {
                this.type = 4;
            } else if(yawDir == 1) {
                this.type = 3;
            } else if(yawDir == 2) {
                this.type = 2;
            } else if(yawDir == 3) {
                this.type = 1;
            }
        } else if(side == 2) {
            this.type = 3;
        } else if(side == 3) {
            this.type = 1;
        } else if(side == 4) {
            this.type = 2;
        } else if(side == 5) {
            this.type = 4;
        }

        if(!this.top) {
            mod_PortalGun.addPortalToList(this);
            if(mod_PortalGun.getSettings("seeThroughPortals") >= 1 && !this.inSpace) {
                this.txRender = mod_PortalGun.getTxRender(this.owner, this.colour);
                if(this.txRender == null) {
                    this.txRender = mod_PortalGun.createTxRender();
                }
            }
        }

        this.setup = true;
    }

    public void setupSpawner(int pset, int side, int yawDir, boolean isTop, PortalBlockEntity portalBlockEntity) {
        this.spSet = pset;
        this.spTop = isTop;
        this.spSideOn = side;
        if(side <= 1) {
            if(yawDir == 0) {
                this.spType = 2;
            } else if(yawDir == 1) {
                this.spType = 3;
            } else if(yawDir == 2) {
                this.spType = 4;
            } else if(yawDir == 3) {
                this.spType = 1;
            }
        } else if(side == 2) {
            this.spType = 3;
        } else if(side == 3) {
            this.spType = 1;
        } else if(side == 4) {
            this.spType = 2;
        } else if(side == 5) {
            this.spType = 4;
        }

        this.spPair = portalBlockEntity;
        this.isSpawner = true;
    }

    public void findLink() {
        if(this.top) {
            if(this.tepPair != null && !this.tepPair.top && this.tepPair.tepLink == null) {
                this.tepPair.findLink();
            }

        } else {
            for(int i = mod_PortalGun.portalsList.size() - 1; i >= 0; --i) {
                PortalBlockEntity portalBlockEntity = (PortalBlockEntity) mod_PortalGun.portalsList.get(i);
                if(portalBlockEntity.owner.equalsIgnoreCase(this.owner) && (portalBlockEntity.world != null && this.world != null && (portalBlockEntity.world == this.world || portalBlockEntity.world.dimension.id == this.world.dimension.id) || this.world != null && portalBlockEntity.inSpace && this.world.dimension.id == 0)) {
                    if(portalBlockEntity.colour == this.colour) {
                        if(portalBlockEntity != this && portalBlockEntity != this.tepPair) {
                            portalBlockEntity.remove();
                        }
                    } else {
                        if(portalBlockEntity.tepLink != null && portalBlockEntity.tepLink != this) {
                            portalBlockEntity.tepLink.remove();
                        }

                        if(portalBlockEntity.setup) {
                            this.tepLink = portalBlockEntity;
                            portalBlockEntity.tepLink = this;
                            BlockEntity blockEntity;
                            PortalBlockEntity portalBlockEntity1;
                            if(this.inSpace) {
                                portalBlockEntity.linkInSpace = true;
                                if(portalBlockEntity.tepPair != null && portalBlockEntity.world.getBlockId(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z) == BlockRegistry.portalBlock.id) {
                                    blockEntity = portalBlockEntity.world.getBlockEntity(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z);
                                    if(blockEntity != null && blockEntity instanceof PortalBlockEntity) {
                                        portalBlockEntity1 = (PortalBlockEntity) blockEntity;
                                        if(portalBlockEntity1 == portalBlockEntity.tepPair) {
                                            portalBlockEntity1.linkInSpace = true;
                                        }
                                    }
                                }
                            } else if(portalBlockEntity.linkInSpace) {
                                portalBlockEntity.linkInSpace = false;
                                if(portalBlockEntity.tepPair != null && portalBlockEntity.world.getBlockId(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z) == BlockRegistry.portalBlock.id) {
                                    blockEntity = portalBlockEntity.world.getBlockEntity(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z);
                                    if(blockEntity != null && blockEntity instanceof PortalBlockEntity) {
                                        portalBlockEntity1 = (PortalBlockEntity) blockEntity;
                                        if(portalBlockEntity1 == portalBlockEntity.tepPair) {
                                            portalBlockEntity1.linkInSpace = false;
                                        }
                                    }
                                }
                            }

                            if(portalBlockEntity.inSpace) {
                                this.linkInSpace = true;
                                if(this.tepPair != null && this.world.getBlockId(this.tepPair.x, this.tepPair.y, this.tepPair.z) == BlockRegistry.portalBlock.id) {
                                    blockEntity = this.world.getBlockEntity(this.tepPair.x, this.tepPair.y, this.tepPair.z);
                                    if(blockEntity != null && blockEntity instanceof PortalBlockEntity) {
                                        portalBlockEntity1 = (PortalBlockEntity) blockEntity;
                                        if(portalBlockEntity1 == this.tepPair) {
                                            portalBlockEntity1.linkInSpace = true;
                                        }
                                    }
                                }
                            } else if(mod_PortalGun.getSettings("seeThroughPortals") == 3) {
                                Portal_TileEntityPortalRenderer.updateTexture(1.0F, portalBlockEntity.getTxRender(), portalBlockEntity, this, true);
                            }
                        }
                    }
                }
            }

        }
    }

    public void remove() {
        this.removal();
        this.setup = false;
        BlockEntity blockEntity;
        if(!this.isSpawner && !this.inSpace) {
            if(this.gelMeta != -1) {
                try {
                    Class.forName("mod_TheFGels");
                    Class.forName("FLabs_TileEntityGel");
                    this.world.setBlock(this.x, this.y, this.z, mod_TheFGels.gel.blockID, this.gelMeta);
                    blockEntity = this.world.getBlockTileEntity(this.x, this.y, this.z);
                    if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                        ((FLabs_TileEntityGel)blockEntity).setGelSide(this.gelSide);
                    } else {
                        this.world.setBlockWithNotify(this.x, this.y, this.z, 0);
                    }
                } catch (Throwable throwable5) {
                }
            } else {
                this.world.setBlock(this.x, this.y, this.z, 0);
            }
        }

        if(this.tepPair != null && this.world.getBlockId(this.tepPair.x, this.tepPair.y, this.tepPair.z) == BlockRegistry.portalBlock.id) {
            blockEntity = this.world.getBlockEntity(this.tepPair.x, this.tepPair.y, this.tepPair.z);
            if(blockEntity != null && blockEntity instanceof PortalBlockEntity) {
                PortalBlockEntity portalBlockEntity = (PortalBlockEntity) blockEntity;
                if(portalBlockEntity.tepPair == this) {
                    portalBlockEntity.setup = false;
                    if(portalBlockEntity.isSpawner) {
                        if(!portalBlockEntity.top && mod_PortalGun.portalsList.contains(portalBlockEntity)) {
                            mod_PortalGun.removePortalFromList(portalBlockEntity);
                        }
                    } else if(portalBlockEntity.gelMeta != -1) {
                        try {
                            Class.forName("mod_TheFGels");
                            Class.forName("FLabs_TileEntityGel");
                            if(!portalBlockEntity.top && mod_PortalGun.portalsList.contains(portalBlockEntity)) {
                                mod_PortalGun.removePortalFromList(portalBlockEntity);
                            }

                            this.world.setBlock(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z, mod_TheFGels.gel.blockID, portalBlockEntity.gelMeta);
                            TileEntity t = this.world.getBlockEntity(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z);
                            if(t != null && t instanceof FLabs_TileEntityGel) {
                                ((FLabs_TileEntityGel)t).setGelSide(portalBlockEntity.gelSide);
                            } else {
                                this.world.setBlock(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z, 0);
                            }
                        } catch (Throwable throwable4) {
                        }
                    } else {
                        this.world.setBlock(this.tepPair.x, this.tepPair.y, this.tepPair.z, 0);
                    }
                }
            }
        }

    }

    public void removeSpawner() {
        BlockEntity blockEntity;
        if(this.setup) {
            this.isSpawner = false;
        } else if(this.gelMeta != -1) {
            try {
                Class.forName("mod_TheFGels");
                Class.forName("FLabs_TileEntityGel");
                this.world.setBlock(this.x, this.y, this.z, mod_TheFGels.gel.blockID, this.gelMeta);
                blockEntity = this.world.getBlockTileEntity(this.x, this.y, this.z);
                if(blockEntity != null && blockEntity instanceof FLabs_TileEntityGel) {
                    ((FLabs_TileEntityGel)blockEntity).setGelSide(this.gelSide);
                } else {
                    this.world.setBlock(this.x, this.y, this.z, 0);
                }
            } catch (Throwable throwable5) {
            }
        } else {
            this.world.setBlock(this.x, this.y, this.z, 0);
        }

        if(this.spPair != null && this.world.getBlockId(this.spPair.x, this.spPair.y, this.spPair.z) == BlockRegistry.portalBlock.id) {
            blockEntity = this.world.getBlockEntity(this.spPair.x, this.spPair.y, this.spPair.z);
            if(blockEntity != null && blockEntity instanceof Portal_TileEntityPortal) {
                PortalBlockEntity portalBlockEntity = (PortalBlockEntity) blockEntity;
                if(portalBlockEntity.spPair == this) {
                    if(portalBlockEntity.setup) {
                        portalBlockEntity.isSpawner = false;
                    } else if(portalBlockEntity.gelMeta != -1) {
                        try {
                            Class.forName("mod_TheFGels");
                            Class.forName("FLabs_TileEntityGel");
                            this.world.setBlock(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z, mod_TheFGels.gel.blockID, portalBlockEntity.gelMeta);
                            BlockEntity blockEntity1 = this.world.getBlockEntity(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z);
                            if(blockEntity1 != null && blockEntity1 instanceof FLabs_TileEntityGel) {
                                ((FLabs_TileEntityGel)blockEntity1).setGelSide(portalBlockEntity.gelSide);
                            } else {
                                this.world.setBlock(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z, 0);
                            }
                        } catch (Throwable throwable4) {
                        }
                    } else {
                        this.world.setBlock(this.tepPair.x, this.tepPair.y, this.tepPair.z, 0);
                    }
                }
            }
        }
    }

    public void removal() {
        PortalBlockEntity portalBlockEntity = null;
        if(this.top && this.tepPair != null && this.tepPair.tepLink != null) {
            portalBlockEntity = this.tepPair.tepLink;
        } else {
            portalBlockEntity = this.tepLink;
        }

        if(portalBlockEntity != null && (!this.top && portalBlockEntity.tepLink == this || this.top && this.tepPair != null && portalBlockEntity.tepLink == this.tepPair)) {
            portalBlockEntity.tepLink = null;
            if(this.inSpace) {
                portalBlockEntity.linkInSpace = false;
                if(portalBlockEntity.tepPair != null && this.world.getBlockId(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z) == BlockRegistry.portalBlock.id) {
                    BlockEntity i = this.world.getBlockEntity(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z);
                    if(i != null && i instanceof PortalBlockEntity) {
                        PortalBlockEntity ent = (PortalBlockEntity) i;
                        if(ent == portalBlockEntity.tepPair) {
                            ent.linkInSpace = false;
                        }
                    }
                }
            }

            if(portalBlockEntity != null && portalBlockEntity.world != null) {
                portalBlockEntity.world.blockUpdateEvent(portalBlockEntity.x, portalBlockEntity.y, portalBlockEntity.z);
                if(portalBlockEntity.tepPair != null) {
                    portalBlockEntity.world.blockUpdateEvent(portalBlockEntity.tepPair.x, portalBlockEntity.tepPair.y, portalBlockEntity.tepPair.z);
                }
            }

            if(this.top && this.tepPair != null) {
                this.tepPair.tepLink = null;
            } else {
                this.tepLink = null;
            }
        }

        if(!this.inSpace) {
            this.world.playSound((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D, "portalgun.portal_fizzle_", 0.15F, 1.0F);
        }

        if(!this.top) {
            mod_PortalGun.removePortalFromList(this);
        }

        for(int i5 = this.suckedEntities.size() - 1; i5 >= 0; --i5) {
            Entity entity6 = (Entity)this.suckedEntities.get(i5);
            if(entity6 != null && !entity6.dead) {
                if(entity6 instanceof Portal_EntityBlock) {
                    Portal_EntityBlock block = (Portal_EntityBlock)entity6;
                    block.dummy = false;
                }

                this.suckedEntities.remove(entity6);
            } else {
                this.suckedEntities.remove(i5);
            }
        }

    }

    public int getBB(int i, boolean start) {
        if(start) {
            if(this.set == 0) {
                if(i == 0) {
                    return this.x;
                }

                if(i == 1) {
                    return this.y - 4;
                }

                if(i == 2) {
                    return this.z;
                }
            } else if(this.set == 1) {
                if(i == 0) {
                    return this.x;
                }

                if(i == 1) {
                    return this.y;
                }

                if(i == 2) {
                    return this.z;
                }
            } else if(this.set == 2) {
                if(i == 1) {
                    return this.y;
                }

                if(this.type == 1) {
                    if(i == 0) {
                        return this.x;
                    }

                    if(i == 2) {
                        return this.z;
                    }
                } else if(this.type == 2) {
                    if(i == 0) {
                        return this.x - 4;
                    }

                    if(i == 2) {
                        return this.z;
                    }
                } else if(this.type == 3) {
                    if(i == 0) {
                        return this.x;
                    }

                    if(i == 2) {
                        return this.z - 4;
                    }
                } else if(this.type == 4) {
                    if(i == 0) {
                        return this.x;
                    }

                    if(i == 2) {
                        return this.z;
                    }
                }
            }
        } else if(this.set == 0) {
            if(i == 0) {
                return this.x + 1;
            }

            if(i == 1) {
                return this.y + 1;
            }

            if(i == 2) {
                return this.z + 1;
            }
        } else if(this.set == 1) {
            if(i == 0) {
                return this.x + 1;
            }

            if(i == 1) {
                return this.y + 5;
            }

            if(i == 2) {
                return this.z + 1;
            }
        } else if(this.set == 2) {
            if(i == 1) {
                return this.y + 1;
            }

            if(this.type == 1) {
                if(i == 0) {
                    return this.x + 1;
                }

                if(i == 2) {
                    return this.z + 5;
                }
            } else if(this.type == 2) {
                if(i == 0) {
                    return this.x + 1;
                }

                if(i == 2) {
                    return this.z + 1;
                }
            } else if(this.type == 3) {
                if(i == 0) {
                    return this.x + 1;
                }

                if(i == 2) {
                    return this.z + 1;
                }
            } else if(this.type == 4) {
                if(i == 0) {
                    return this.x + 5;
                }

                if(i == 2) {
                    return this.z + 1;
                }
            }
        }

        return this.x;
    }

    @Override
    public void tick() {
        if(!this.setup && !this.isSpawner && !this.world.isRemote) {
            this.remove();
        }

        int living;
        if(this.firstUpdate) {
            this.firstUpdate = false;
            if(this.nbt != null) {
                BlockEntity blockEntity = this.world.getBlockEntity(this.nbt.getInt("tepPairX"), this.nbt.getInt("tepPairY"), this.nbt.getInt("tepPairZ"));
                if(blockEntity != null && blockEntity instanceof PortalBlockEntity) {
                    this.tepPair = (PortalBlockEntity) blockEntity;
                    this.tepPair.tepPair = this;
                } else {
                    this.remove();
                }

                BlockEntity blockEntity1 = this.world.getBlockEntity(this.nbt.getInt("tepLinkX"), this.nbt.getInt("tepLinkY"), this.nbt.getInt("tepLinkZ"));
                if(blockEntity1 != null && blockEntity1 instanceof PortalBlockEntity) {
                    PortalBlockEntity block = (PortalBlockEntity)blockEntity1;
                    if(block.owner.equalsIgnoreCase(this.owner) && block.setup) {
                        this.tepLink = block;
                        this.tepLink.tepLink = this;
                    } else {
                        this.findLink();
                    }
                }

                if(this.isSpawner) {
                    BlockEntity blockEntity2 = this.world.getBlockEntity(this.nbt.getInt("spPairX"), this.nbt.getInt("spPairY"), this.nbt.getInt("spPairZ"));
                    if(blockEntity2 != null && blockEntity2 instanceof Portal_TileEntityPortal) {
                        this.spPair = (PortalBlockEntity) blockEntity2;
                        this.spPair.spPair = this;
                    }
                }

                if(!this.world.isRemote && (this.linkInSpace && this.tepLink == null || this.linkInSpace && this.tepLink != null && !this.tepLink.inSpace)) {
                    boolean z22 = false;

                    for(living = mod_PortalGun.portalsList.size() - 1; living >= 0; --living) {
                        PortalBlockEntity k = (PortalBlockEntity)mod_PortalGun.portalsList.get(living);
                        if(k.inSpace && this.colour != k.colour && this.owner.equalsIgnoreCase(k.owner)) {
                            this.tepLink = k;
                            k.tepLink = this;
                            z22 = true;
                            break;
                        }
                    }

                    if(!z22) {
                        mod_PortalGun.spawnMoonPortal(this.world, this.colour == 1 ? 2 : 1, true, this.owner);
                    }
                }
            }

            if(!this.top && this.setup) {
                mod_PortalGun.addPortalToList(this);
                if(mod_PortalGun.getSettings("seeThroughPortals") >= 1 && !this.inSpace) {
                    this.txRender = mod_PortalGun.getTxRender(this.owner, this.colour);
                    if(this.txRender == null) {
                        this.txRender = mod_PortalGun.createTxRender();
                    }

                    if(mod_PortalGun.getSettings("seeThroughPortals") == 3) {
                        Portal_TileEntityPortalRenderer.updateTexture(1.0F, this.getTxRender(), this, this.tepLink, true);
                    }
                }
            }
        }

        if(this.setup) {
            List list18 = this.world.getEntities((Entity)null, Box.createCached((double)this.getBB(0, true), (double)this.getBB(1, true), (double)this.getBB(2, true), (double)this.getBB(0, false), (double)this.getBB(1, false), (double)this.getBB(2, false)));
            int i20;
            if(list18.size() > 0) {
                for(i20 = 0; i20 < list18.size(); ++i20) {
                    this.portalEntity((Entity)list18.get(i20));
                }
            }

            if(this.linkInSpace) {
                for(i20 = 0; i20 < 15; ++i20) {
                    int i24 = this.world.random.nextInt(9);
                    living = this.world.random.nextInt(9);
                    int i25 = this.world.random.nextInt(9);
                    i24 = (this.world.random.nextInt(2) != 0 || (this.set != 2 || this.type == 2) && this.set > 1) && (this.set != 2 || this.type != 4) ? -i24 : i24;
                    living = (this.world.random.nextInt(2) != 0 || this.set == 0) && this.set != 1 ? -living : living;
                    i25 = (this.world.random.nextInt(2) != 0 || (this.set != 2 || this.type == 3) && this.set > 1) && (this.set != 2 || this.type != 1) ? -i25 : i25;
                    i24 += this.x;
                    living += this.y;
                    i25 += this.z;
                    if((i24 != this.x || living != this.y || i25 != this.z) && this.tepPair != null && (i24 != this.tepPair.x || living != this.tepPair.y || i25 != this.tepPair.z) && (double)MathHelper.sqrt_double(this.getDistanceFrom((double)i24 + 0.5D, (double)living + 0.5D, (double)i25 + 0.5D)) <= 7.5D) {
                        int bId = this.world.getBlockId(i24, living, i25);
                        if(bId != 0 && bId != Block.BEDROCK.id) {
                            if(!this.world.isRemote) {
                                Vec3d vec3D30 = Vec3d.create((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D);
                                Vec3d vec3d1 = Vec3d.create((double)i24 + 0.5D, (double)living + 0.5D, (double)i25 + 0.5D);
                                HitResult hitResult = this.world.raycast(vec3D30, vec3d1, false, true);
                                vec3D30 = Vec3d.create((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D);
                                vec3d1 = Vec3d.create((double)i24 + 0.5D, (double)living + 0.5D, (double)i25 + 0.5D);
                                if(hitResult != null) {
                                    vec3d1 = Vec3d.create(hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
                                }

                                if(hitResult != null && hitResult.entity == null) {
                                    int xTile = hitResult.blockX;
                                    int yTile = hitResult.blockY;
                                    int zTile = hitResult.blockZ;
                                    int inTile = this.world.getBlockId(xTile, yTile, zTile);
                                    if(inTile == 0 || inTile == Block.BEDROCK.id) {
                                        break;
                                    }

                                    boolean grabable = true;
                                    if(mod_PortalGun.getSettings("portalsUseGrabList") == 1) {
                                        int entBlock;
                                        int[] modMeta;
                                        int j1;
                                        if(mod_PortalGun.getSettings("grabBlockWhitelist") == 0) {
                                            if(mod_PortalGun.isBlockIdInList(mod_PortalGun.grabBlockIds, inTile)) {
                                                entBlock = this.world.getBlockMeta(i24, living, i25);
                                                modMeta = mod_PortalGun.getBlockMetaInList(mod_PortalGun.grabBlockIds, inTile);

                                                for(j1 = 0; j1 < modMeta.length; ++j1) {
                                                    if(entBlock == modMeta[j1]) {
                                                        grabable = false;
                                                        break;
                                                    }
                                                }

                                                if(modMeta.length != 0 && modMeta[0] == -1) {
                                                    grabable = false;
                                                }
                                            }
                                        } else {
                                            grabable = false;
                                            if(mod_PortalGun.isBlockIdInList(mod_PortalGun.grabBlockIds, inTile)) {
                                                entBlock = this.world.getBlockMeta(i24, living, i25);
                                                modMeta = mod_PortalGun.getBlockMetaInList(mod_PortalGun.grabBlockIds, inTile);

                                                for(j1 = 0; j1 < modMeta.length; ++j1) {
                                                    if(entBlock == modMeta[j1]) {
                                                        grabable = true;
                                                        break;
                                                    }
                                                }

                                                if(modMeta.length != 0 && modMeta[0] == -1) {
                                                    grabable = true;
                                                }
                                            }
                                        }

                                        if(mod_PortalGun.isBlockIdInList(mod_PortalGun.modBlockIds, inTile)) {
                                            entBlock = this.world.getBlockMeta(i24, living, i25);
                                            modMeta = mod_PortalGun.getBlockMetaInList(mod_PortalGun.modBlockIds, inTile);

                                            for(j1 = 0; j1 < modMeta.length; ++j1) {
                                                if(entBlock == modMeta[j1]) {
                                                    grabable = false;
                                                    break;
                                                }
                                            }

                                            if(modMeta.length != 0 && modMeta[0] == -1) {
                                                grabable = false;
                                            }
                                        }
                                    }

                                    if(grabable) {
                                        Portal_EntityBlock portal_EntityBlock31 = new Portal_EntityBlock(this.world, (double)((float)hitResult.blockX + 0.5F), (double)((float)hitResult.blockY + 0.5F), (double)((float)hitResult.blockZ + 0.5F), inTile, true);
                                        this.world.spawnEntityInWorld(portal_EntityBlock31);
                                        this.suckedEntities.add(portal_EntityBlock31);
                                    }
                                    break;
                                }
                            }
                        } else if(this.world.random.nextInt(30) == 0) {
                            float f = 0.1F;
                            if(this.world.random.nextInt(8) == 0) {
                                this.world.addParticle("largesmoke", (double)i24 + this.world.random.nextDouble(), (double)living + this.world.random.nextDouble(), (double)i25 + this.world.random.nextDouble(), (double)((float)(this.x - i24) * f), (double)((float)(this.y - living) * f), (double)((float)(this.z - i25) * f));
                            } else {
                                this.world.addParticle("smoke", (double)i24 + this.world.random.nextDouble(), (double)living + this.world.random.nextDouble(), (double)i25 + this.world.random.nextDouble(), (double)((float)(this.x - i24) * f), (double)((float)(this.y - living) * f), (double)((float)(this.z - i25) * f));
                            }
                        }
                    }
                }

                if(this.world.random.nextInt(15) == 0 && !this.world.isRemote) {
                    for(i20 = 0; i20 < this.world.entities.size(); ++i20) {
                        Entity entity26 = (Entity)this.world.entities.get(i20);
                        if(!(entity26 instanceof Portal_EntityPortalBall) && (!(entity26 instanceof EntityPlayer) || mod_PortalGun.getSettings("canPlayerBeSuckedToTheMoon") != 0) && entity26.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 7.5D && (this.set == 0 && entity26.posY <= (double)this.y + 1.01D || this.set == 1 && entity26.posY >= (double)this.y - 0.01D || this.set == 2 && (this.type == 1 && entity26.z >= (double)this.z - 0.01D || this.type == 3 && entity26.z <= (double)this.z + 1.01D || this.type == 2 && entity26.x <= (double)this.x + 1.01D || this.type == 4 && entity26.x >= (double)this.x - 0.01D)) && this.canEntityBeSeen(entity26) && !this.suckedEntities.contains(entity26) && this.tepPair != null && this.tepPair.linkInSpace && !this.tepPair.suckedEntities.contains(entity26)) {
                            if(entity26 instanceof EntityLiving && !(entity26 instanceof EntityPlayer) && this.world.random.nextInt(25) == 0) {
                                EntityLiving entityLiving27 = (EntityLiving)entity26;
                                this.world.playSoundAtEntity(entityLiving27, entityLiving27.getLivingSound(), entityLiving27.getSoundVolume(), 1.0F);
                            }

                            this.suckedEntities.add(entity26);
                        }
                    }
                }
            }
        } else if(mod_PortalGun.portalsList.contains(this)) {
            mod_PortalGun.removePortalFromList(this);
        }

        if(this.isSpawner && !this.world.isRemote) {
            if(!this.powered) {
                if(this.isPowered() || this.spPair != null && this.spPair.isPowered()) {
                    this.powered = true;
                    this.spPair.powered = true;
                    if(this.setup && (this.top != this.spTop || this.colour != this.spColour || !this.owner.equalsIgnoreCase(this.spOwner))) {
                        this.remove();
                    }

                    if(this.spPair.setup && (this.spPair.top != this.spPair.spTop || this.spPair.colour != this.spPair.spColour || !this.spPair.owner.equalsIgnoreCase(this.spPair.spOwner))) {
                        this.spPair.remove();
                    }

                    if(!this.setup) {
                        this.setupType(this.spSet, this.spSideOn, this.spType, this.spColour, this.spTop, this.spPair, true, this.spOwner);
                        this.spPair.setupType(this.spPair.spSet, this.spPair.spSideOn, this.spPair.spType, this.spPair.spColour, this.spPair.spTop, this.spPair.spPair, true, this.spOwner);
                        if(this.spTop) {
                            this.spPair.findLink();
                        } else {
                            this.findLink();
                            if(mod_PortalGun.getSettings("seeThroughPortals") == 3) {
                                Portal_TileEntityPortalRenderer.updateTexture(1.0F, this.getTxRender(), this, this.tepLink, true);
                            }
                        }

                        if(this.colour == 1) {
                            this.world.playSound((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D, "portalgun.portal_open_blue_", 0.3F, 1.0F);
                        } else if(this.colour == 2) {
                            this.world.playSound((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D, "portalgun.portal_open_red_", 0.3F, 1.0F);
                        }
                    }
                }
            } else if(!this.isPowered() && this.spPair != null && !this.spPair.isPowered()) {
                this.powered = false;
                this.spPair.powered = false;
                if(this.spawnerPowered && this.spPair != null && this.setup && this.spPair.setup && this.spColour == this.colour && this.spPair == this.tepPair && this.spOwner.equalsIgnoreCase(this.owner)) {
                    this.remove();
                }
            }
        }

        int i19;
        Entity entity23;
        for(i19 = this.portalledEntities.size() - 1; i19 >= 0; --i19) {
            entity23 = (Entity)this.portalledEntities.get(i19);
            if(entity23 == null || entity23.velocityY <= 0.0D || entity23.dead) {
                ((EntityAccessor)entity23).setFallDistance(0.0F);
                this.portalledEntities.remove(i19);
            }
        }

        for(i19 = this.suckedEntities.size() - 1; i19 >= 0; --i19) {
            entity23 = (Entity)this.suckedEntities.get(i19);
            if(entity23 != null && !entity23.dead && (this.tepPair == null || !this.tepPair.suckedEntities.contains(entity23)) && entity23.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 7.5D) {
                if(this.linkInSpace) {
                    double d28 = entity23.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) == 0.0D ? 0.01D : entity23.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) / 7.5D;
                    d28 /= 1.0D;
                    entity23.setVelocity(((double)this.x + 0.5D - entity23.x - entity23.velocityX) / 10.0D, ((double)this.y + 0.5D - (entity23.boundingBox.maxY + entity23.boundingBox.minY) / 2.0D - entity23.velocityY) / 10.0D, ((double)this.z + 0.5D - entity23.z - entity23.velocityZ) / 10.0D);
                    entity23.setVelocity(entity23.velocityX / d28, entity23.velocityY / d28, entity23.velocityZ / d28);
                } else {
                    if(entity23 instanceof Portal_EntityBlock) {
                        Portal_EntityBlock portal_EntityBlock29 = (Portal_EntityBlock)entity23;
                        portal_EntityBlock29.dummy = false;
                    }

                    this.suckedEntities.remove(entity23);
                }
            } else {
                this.suckedEntities.remove(i19);
            }
        }
    }

    public float getRotationYawIn() {
        return this.type == 1 ? 180.0F : (this.type == 2 ? 270.0F : (this.type == 3 ? 0.0F : (this.type == 4 ? 90.0F : 0.0F)));
    }

    public float getRotationYawOut() {
        float f = this.getRotationYawIn();
        f += 180.0F;
        if(f > 360.0F) {
            f -= 360.0F;
        }

        return f;
    }

    public void portalEntity(Entity entity) {
        if((!this.world.isRemote || !(entity instanceof PlayerEntity)) && !(entity instanceof Portal_EntityPortalBall) && !(entity instanceof Portal_EntityAPG) && !(entity instanceof EntityPainting) && !mod_PortalGun.isPlayerGrabbing(entity)) {
            int i = this.x;
            int j = this.y;
            int k = this.z;
            if((this.top || this.tepLink != null) && (!this.top || this.tepPair == null || this.tepPair.tepLink != null) && (!this.top || this.tepPair != null)) {
                double moX = entity.velocityX;
                double moY = entity.velocityY;
                double moZ = entity.velocityZ;
                if(this.isSuitable(entity) || this.linkInSpace && entity.getDistance((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 1.0D) {
                    float mountYawDiff;
                    float grabbed;
                    if(this.linkInSpace && !this.world.isRemote) {
                        if(!(entity instanceof PlayerEntity)) {
                            entity.markDead();
                        } else {
                            PlayerEntity playerEntity = (PlayerEntity)entity;
                            playerEntity.health = 1;
                            playerEntity.attackEntityFrom(DamageSource.generic, 20);
                            playerEntity.setPosition(playerEntity.x, playerEntity.y + 400.0D, playerEntity.z);
                            playerEntity.velocityY = 1.0D;
                            ((EntityAccessor)playerEntity).setFallDistance(-250.0F);
                            if(playerEntity == ModLoader.getMinecraftInstance().renderViewEntity) {
                                float typeDiff1 = this.world.getCelestialAngle(1.0F);
                                mountYawDiff = playerEntity.yaw > 0.0F ? 270.0F : -90.0F;
                                mountYawDiff = typeDiff1 > 0.5F ? (playerEntity.yaw > 0.0F ? 90.0F : -270.0F) : mountYawDiff;
                                grabbed = this.world.getCelestialAngle(1.0F);
                                grabbed = grabbed > 0.5F ? 1.0F - grabbed : grabbed;
                                grabbed -= 0.26F;
                                grabbed = grabbed / 0.26F * -94.0F - 4.0F;
                                playerEntity.setRotation(mountYawDiff, grabbed);
                                mod_PortalGun.zoomValue = 9.0D;
                                mod_PortalGun.unzoom = true;
                                mod_PortalGun.zoomClock = 21;
                            }

                            if(playerEntity == ModLoader.getMinecraftInstance().thePlayer && playerEntity.health <= 0) {
                                ChunkCoordinates typeDiff2 = playerEntity.getSpawnChunk();
                                if(typeDiff2 == null) {
                                    typeDiff2 = this.world.getSpawnPoint();
                                }

                                if(typeDiff2 != null) {
                                    IChunkProvider mountYawDiff1 = this.world.getChunkProvider();
                                    mountYawDiff1.loadChunk(typeDiff2.x - 3 >> 4, typeDiff2.posZ - 3 >> 4);
                                    mountYawDiff1.loadChunk(typeDiff2.x + 3 >> 4, typeDiff2.posZ - 3 >> 4);
                                    mountYawDiff1.loadChunk(typeDiff2.x - 3 >> 4, typeDiff2.posZ + 3 >> 4);
                                    mountYawDiff1.loadChunk(typeDiff2.x + 3 >> 4, typeDiff2.posZ + 3 >> 4);
                                    if((double)MathHelper.sqrt_double(this.getDistanceFrom((double)typeDiff2.x, (double)typeDiff2.y, (double)typeDiff2.posZ)) <= 8.0D && this.tepLink != null && this.tepLink.inSpace && this.tepLink.setup) {
                                        this.tepLink.remove();
                                    }
                                }
                            }
                        }

                        if(this.suckedEntities.contains(entity)) {
                            this.suckedEntities.remove(entity);
                        }

                        if(this.tepPair != null && this.tepPair.suckedEntities.contains(entity)) {
                            this.tepPair.suckedEntities.remove(entity);
                        }

                        return;
                    }

                    PortalBlockEntity tep = null;
                    if(this.top) {
                        if(this.tepPair == null || this.tepPair.tepLink == null || this.tepPair.tepLink.tepPair == null) {
                            return;
                        }

                        tep = this.tepPair.tepLink.tepPair;
                    } else {
                        if(this.tepLink == null) {
                            return;
                        }

                        tep = this.tepLink;
                    }

                    if(this.tepPair != null && this.tepPair.portalling) {
                        return;
                    }

                    if(!tep.setup && !tep.inSpace || !tep.owner.equalsIgnoreCase(this.owner)) {
                        this.findLink();
                        if(this.getLink() != null && this.getLink() != tep && this.getLink().setup && this.getLink().owner.equalsIgnoreCase(this.owner) && (this.top && this.getLink() != this.tepPair || this.getLink() != this)) {
                            this.portalEntity(entity);
                        }

                        return;
                    }

                    this.portalling = true;
                    if(!(entity instanceof Particle) && !(entity instanceof Portal_EntityBullet) && !(entity instanceof Portal_EntityHEP)) {
                        this.world.playSound(entity, "portalgun.portal_enter_", 0.1F, 1.0F);
                    }

                    int typeDiff = tep.type - this.type;
                    mountYawDiff = entity.riddenByEntity != null ? entity.riddenByEntity.yaw - entity.yaw : 0.0F;
                    if(this.set <= 1) {
                        if(tep.set <= 1) {
                            grabbed = 0.0F;
                            grabbed += (float)typeDiff * 90.0F;
                            this.setRotation(entity, entity.yaw + grabbed, entity.pitch);
                        }

                        if(this.set != 0) {
                            if(this.set == 1) {
                                if(tep.set == 0) {
                                    this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : 1.0D - (entity.boundingBox.maxY - entity.y) - 0.1D), (double)tep.z + 0.5D);
                                    if(typeDiff != -3 && typeDiff != 1) {
                                        if(typeDiff != -2 && typeDiff != 2) {
                                            if(typeDiff == -1 || typeDiff == 3) {
                                                entity.velocityX = moZ;
                                                entity.velocityZ = -moX;
                                            }
                                        } else {
                                            entity.velocityX *= -1.0D;
                                            entity.velocityZ *= -1.0D;
                                        }
                                    } else {
                                        entity.velocityX = -moZ;
                                        entity.velocityZ = moX;
                                    }
                                } else if(tep.set == 1) {
                                    this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y + 0.1D), (double)tep.z + 0.5D);
                                    if(typeDiff != -3 && typeDiff != 1) {
                                        if(typeDiff != -2 && typeDiff != 2) {
                                            if(typeDiff == -1 || typeDiff == 3) {
                                                entity.velocityX = moZ;
                                                entity.velocityZ = -moX;
                                            }
                                        } else {
                                            entity.velocityX *= -1.0D;
                                            entity.velocityZ *= -1.0D;
                                        }
                                    } else {
                                        entity.velocityX = -moZ;
                                        entity.velocityZ = moX;
                                    }

                                    entity.velocityY *= -1.0D;
                                    entity.velocityY += 0.0D;
                                    if(entity instanceof LivingEntity) {
                                        LivingEntity grabbed1 = (LivingEntity)entity;
                                        if(entity.velocityY < 0.7D) {
                                            entity.velocityY -= entity.velocityY / 20.0D;
                                        } else {
                                            entity.velocityY += entity.velocityY / 12.0D;
                                        }
                                    } else if(!(entity instanceof Portal_EntityHEP)) {
                                        entity.velocityY += entity.velocityY / 3.0D;
                                    }

                                    if(entity.velocityY <= 0.1D && !(entity instanceof Portal_EntityHEP) || entity.onGround && !(entity instanceof Portal_EntityHEP)) {
                                        if(entity instanceof LivingEntity) {
                                            ((EntityLiving)entity).jump();
                                        } else {
                                            entity.velocityY += 0.2D;
                                        }
                                    }
                                } else {
                                    if(this.top) {
                                        if(this.tepPair == null || this.tepPair.tepLink == null) {
                                            return;
                                        }

                                        tep = this.tepPair.tepLink;
                                    }

                                    this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? (this.top ? 1.5D : 0.5D) : (double)entity.yOffset + 0.05D), (double)tep.z + 0.5D);
                                    entity.velocityX = 0.0D;
                                    entity.velocityZ = 0.0D;
                                    if(entity.velocityY > -0.17D) {
                                        entity.velocityY = -0.17D;
                                    }

                                    if(!(entity instanceof Portal_EntityHEP)) {
                                        entity.velocityY *= 1.2D;
                                    }

                                    if(tep.type == 1) {
                                        entity.velocityZ = -entity.velocityY;
                                        entity.yaw = 0.0F;
                                    } else if(tep.type == 2) {
                                        entity.velocityX = entity.velocityY;
                                        entity.yaw = 90.0F;
                                    } else if(tep.type == 3) {
                                        entity.velocityZ = entity.velocityY;
                                        entity.yaw = 180.0F;
                                    } else if(tep.type == 4) {
                                        entity.velocityX = -entity.velocityY;
                                        entity.yaw = 270.0F;
                                    }

                                    this.setRotation(entity, entity.yaw, entity.pitch);
                                    ((EntityAccessor)entity).setFallDistance(0.0F);
                                    entity.velocityY = 0.0D;
                                }
                            }
                        } else if(tep.set == 0) {
                            entity.velocityY *= -1.0D;
                            this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y - 0.2D), (double)tep.z + 0.5D);
                            if(typeDiff != -3 && typeDiff != 1) {
                                if(typeDiff != -2 && typeDiff != 2) {
                                    if(typeDiff == -1 || typeDiff == 3) {
                                        entity.velocityX = moZ;
                                        entity.velocityZ = -moX;
                                    }
                                } else {
                                    entity.velocityX *= -1.0D;
                                    entity.velocityZ *= -1.0D;
                                }
                            } else {
                                entity.velocityX = -moZ;
                                entity.velocityZ = moX;
                            }
                        } else if(tep.set == 1) {
                            this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y + 0.1D), (double)tep.z + 0.5D);
                            if(typeDiff != -3 && typeDiff != 1) {
                                if(typeDiff != -2 && typeDiff != 2) {
                                    if(typeDiff == -1 || typeDiff == 3) {
                                        entity.velocityX = moZ;
                                        entity.velocityZ = -moX;
                                    }
                                } else {
                                    entity.velocityX *= -1.0D;
                                    entity.velocityZ *= -1.0D;
                                }
                            } else {
                                entity.velocityX = -moZ;
                                entity.velocityZ = moX;
                            }

                            if(entity.velocityY <= 0.1D && !(entity instanceof Portal_EntityHEP)) {
                                if(entity instanceof LivingEntity) {
                                    ((LivingEntity)entity).jump();
                                } else {
                                    entity.velocityY += 0.11D;
                                }
                            }
                        } else {
                            if(this.top) {
                                if(this.tepPair == null || this.tepPair.tepLink == null) {
                                    return;
                                }

                                tep = this.tepPair.tepLink;
                            }

                            this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? (this.top ? 1.5D : 0.5D) : 0.1D + (double)entity.yOffset), (double)tep.z + 0.5D);
                            entity.velocityX = 0.0D;
                            entity.velocityZ = 0.0D;
                            if(entity.velocityY < 0.17D && !(entity instanceof Portal_EntityHEP)) {
                                entity.velocityY = 0.17D;
                            }

                            if(tep.type == 1) {
                                entity.velocityZ = entity.velocityY;
                                entity.yaw = 0.0F;
                            } else if(tep.type == 2) {
                                entity.velocityX = -entity.velocityY;
                                entity.yaw = 90.0F;
                            } else if(tep.type == 3) {
                                entity.velocityZ = -entity.velocityY;
                                entity.yaw = 180.0F;
                            } else if(tep.type == 4) {
                                entity.velocityX = entity.velocityY;
                                entity.yaw = 270.0F;
                            }

                            this.setRotation(entity, entity.yaw, entity.pitch);
                            ((EntityAccessor)entity).setFallDistance(0.0F);
                            entity.velocityY = 0.0D;
                        }
                    } else if(tep.set <= 1) {
                        if(entity instanceof LivingEntity) {
                            if(!entity.onGround) {
                                moX *= 0.587167162D;
                                moZ *= 0.587167162D;
                            }

                            moX *= 4.0D;
                            moZ *= 4.0D;
                        }

                        grabbed = 0.0F;
                        grabbed += (float)typeDiff * 90.0F;
                        this.setRotation(entity, entity.yaw + grabbed, entity.pitch);
                        if(tep.set == 0) {
                            if(this.top) {
                                this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y), (double)tep.z + 0.5D);
                            } else {
                                this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y - (double)entity.yOffset), (double)tep.z + 0.5D);
                            }

                            if(this.type == 1) {
                                entity.velocityY = moZ;
                            } else if(this.type == 2) {
                                entity.velocityY = -moX;
                            } else if(this.type == 3) {
                                entity.velocityY = -moZ;
                            } else if(this.type == 4) {
                                entity.velocityY = moX;
                            }

                            entity.velocityX = 0.0D;
                            entity.velocityZ = 0.0D;
                        } else if(tep.set == 1) {
                            this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : 0.1D + (double)entity.yOffset), (double)tep.z + 0.5D);
                            if(this.type == 1) {
                                entity.velocityY = -moZ;
                            } else if(this.type == 2) {
                                entity.velocityY = moX;
                            } else if(this.type == 3) {
                                entity.velocityY = moZ;
                            } else if(this.type == 4) {
                                entity.velocityY = -moX;
                            }

                            if(entity.velocityY <= 0.2D && !(entity instanceof Portal_EntityHEP)) {
                                if(entity instanceof EntityLiving) {
                                    ((LivingEntity)entity).jump();
                                } else {
                                    entity.velocityY += 0.1D;
                                }
                            }

                            entity.velocityX = 0.0D;
                            entity.velocityZ = 0.0D;
                        }
                    } else if(tep.set == 2) {
                        grabbed = -180.0F;
                        grabbed += (float)typeDiff * 90.0F;
                        this.setRotation(entity, entity.yaw + grabbed, entity.pitch);
                        this.setPosition(entity, (double)tep.x + 0.5D, (double)tep.y + (entity instanceof Portal_EntityHEP ? 0.5D : entity.y - (double)this.y + 0.01D), (double)tep.z + 0.5D);
                        if(typeDiff != -3 && typeDiff != 1) {
                            if(typeDiff != -1 && typeDiff != 3) {
                                if(typeDiff == 0) {
                                    entity.velocityX = -moX;
                                    entity.velocityZ = -moZ;
                                }
                            } else {
                                entity.velocityX = -moZ;
                                entity.velocityZ = moX;
                            }
                        } else {
                            entity.velocityX = moZ;
                            entity.velocityZ = -moX;
                        }
                    }

                    if(entity.riddenByEntity != null) {
                        entity.riddenByEntity.yaw = entity.yaw + mountYawDiff;
                    }

                    if(entity instanceof Portal_EntityHEP) {
                        Portal_EntityHEP grabbed2 = (Portal_EntityHEP)entity;
                        grabbed2.setBounced(0);
                    } else if(entity instanceof EntityArrow) {
                        ArrowEntity grabbed3 = (ArrowEntity)entity;

                        try {
                            ModLoader.setPrivateValue(EntityArrow.class, grabbed3, "inGround", false);
                        } catch (Exception exception18) {
                            try {
                                ModLoader.setPrivateValue(EntityArrow.class, grabbed3, "aq", false);
                            } catch (Exception exception17) {
                                mod_PortalGun.console("Forgot to update obfuscation!");
                            }
                        }

                        grabbed3.arrowShake = 0;
                    } else if(entity.velocityY >= 0.0D) {
                        tep.portalledEntities.add(entity);
                    }

                    if(entity == ModLoader.getMinecraftInstance().thePlayer && mod_PortalGun.getGrabbedEnt(ModLoader.getMinecraftInstance().thePlayer) != null) {
                        Entity grabbed4 = mod_PortalGun.getGrabbedEnt(ModLoader.getMinecraftInstance().thePlayer);
                        this.setPosition(grabbed4, entity.x, entity.y, entity.posZ);
                    }

                    this.portalling = false;
                    if(!(entity instanceof Particle) && !(entity instanceof Portal_EntityBullet) && !(entity instanceof Portal_EntityHEP)) {
                        this.world.playSound(entity, "portalgun.portal_exit_", 0.1F, 1.0F);
                    }
                }

            }
        }
    }
}
