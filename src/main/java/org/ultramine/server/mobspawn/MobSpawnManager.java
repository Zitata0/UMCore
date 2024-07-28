package org.ultramine.server.mobspawn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldServer;
import org.ultramine.server.WorldsConfig.WorldConfig;

@SideOnly(Side.SERVER)
public class MobSpawnManager
{
	private final WorldServer world;
	private final MobSpawner spawnerMonsters;
	private MobSpawner spawnerAnimals;
	private MobSpawner spawnerWater;
	private MobSpawner spawnerAmbient;

	public MobSpawnManager(WorldServer world)
	{
		this.world = world;

		spawnerMonsters = new MobSpawnerMonsters(world);
		if(!(world.provider instanceof WorldProviderEnd) && !(world.provider instanceof WorldProviderHell))
		{
			spawnerWater = new MobSpawnerWater(world);
			spawnerAmbient = new MobSpawnerAmbient(world);
		}
		if(!(world.provider instanceof WorldProviderEnd)){
			spawnerAnimals = new MobSpawnerAnimals(world);
		}
	}

	public void configure(WorldConfig config)
	{
		spawnerMonsters.configure(config);
		if(spawnerAnimals != null) {
			spawnerAnimals.configure(config);
		}
		if(spawnerWater != null){
			spawnerWater.configure(config);
			spawnerAmbient.configure(config);
		}
	}

	public void performSpawn(boolean spawnMonsters, boolean spawnAnimals, long currentTick)
	{
		spawnerMonsters.performSpawn(currentTick);
		if(spawnerAnimals != null) {
			spawnerAnimals.performSpawn(currentTick);
		}
		if(spawnerWater != null){
			spawnerWater.performSpawn(currentTick);
			spawnerAmbient.performSpawn(currentTick);
		}
	}
}
