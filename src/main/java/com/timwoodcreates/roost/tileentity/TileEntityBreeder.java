package com.timwoodcreates.roost.tileentity;

import java.util.List;

import com.timwoodcreates.roost.Roost;
import com.timwoodcreates.roost.block.BlockBreeder;
import com.timwoodcreates.roost.data.DataChicken;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TileEntityBreeder extends TileEntityChickenContainer {

	private static final String CHICKEN_0_KEY = "Chicken0";
	private static final String CHICKEN_1_KEY = "Chicken1";
	private static final String COMPLETE_KEY = "Complete";
	private static final String HAS_SEEDS_KEY = "HasSeeds";

	@Override
	protected void isFullOfChickensChanged(boolean isFull) {
		BlockBreeder.setIsBreedingState(isFull, worldObj, pos);
	}

	@Override
	protected void spawnChickenDrop() {
		DataChicken left = getChickenData(0);
		DataChicken right = getChickenData(1);
		if (left != null && right != null) {
			putStackInOutput(left.createChildStack(right, worldObj));
			playSpawnSound();
			spawnParticles();
		}
	}

	private void playSpawnSound() {
		worldObj.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5F, 0.8F);
	}

	private void spawnParticles() {
		spawnParticle(-0.1d, 0.5d, 0, 0.2d);
		spawnParticle(0.5d, -0.1d, 0.2d, 0);
		spawnParticle(1.1d, 0.5d, 0, 0.2d);
		spawnParticle(0.5d, 1.1d, 0.2d, 0);
	}

	private void spawnParticle(double x, double z, double xOffset, double zOffset) {
		if (worldObj instanceof WorldServer) {
			WorldServer worldServer = (WorldServer) worldObj;
			worldServer.spawnParticle(EnumParticleTypes.HEART, pos.getX() + x, pos.getY() + 0.5d, pos.getZ() + z, 2,
					xOffset, 0.2d, zOffset, 0.02D);
		}
	}

	public void addInfoToTooltip(List<String> tooltip, NBTTagCompound tag) {
		if (tag.hasKey(CHICKEN_0_KEY)) tooltip.add(tag.getString(CHICKEN_0_KEY));
		if (tag.hasKey(CHICKEN_1_KEY)) tooltip.add(tag.getString(CHICKEN_1_KEY));
		if (tag.hasKey(COMPLETE_KEY)) {
			tooltip.add("Progress: " + tag.getString(COMPLETE_KEY));
			if (!tag.getBoolean(HAS_SEEDS_KEY)) {
				tooltip.add(TextFormatting.RED + I18n.format("container.roost.breeder.seedless"));
			}
		}
	}

	public void storeInfoForTooltip(NBTTagCompound tag) {
		DataChicken chicken0 = getChickenData(0);
		DataChicken chicken1 = getChickenData(1);
		if (chicken0 != null) tag.setString(CHICKEN_0_KEY, chicken0.getDisplaySummary());
		if (chicken1 != null) tag.setString(CHICKEN_1_KEY, chicken1.getDisplaySummary());
		if (chicken0 != null && chicken1 != null) {
			tag.setString(COMPLETE_KEY, getFormattedProgress());
			tag.setBoolean(HAS_SEEDS_KEY, hasEnoughSeeds());
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public String getName() {
		return "container." + Roost.MODID + ".breeder";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	public int getSizeChickenInventory() {
		return 2;
	}

	@Override
	protected int requiredSeedsForDrop() {
		return 2;
	}

}
