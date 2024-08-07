package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityAnimal;

import java.util.Iterator;
import java.util.List;

public class EntityAIFollowParent extends EntityAIBase
{
	EntityAnimal childAnimal;
	EntityAnimal parentAnimal;
	double field_75347_c;
	private int field_75345_d;
	private static final String __OBFID = "CL_00001586";

	public EntityAIFollowParent(EntityAnimal p_i1626_1_, double p_i1626_2_)
	{
		this.childAnimal = p_i1626_1_;
		this.field_75347_c = p_i1626_2_;
	}

	public boolean shouldExecute()
	{
		if (this.childAnimal.getGrowingAge() >= 0)
		{
			return false;
		}
		else
		{
			List list = this.childAnimal.worldObj.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
			EntityAnimal entityanimal = null;
			double d0 = Double.MAX_VALUE;
			Iterator iterator = list.iterator();

			while (iterator.hasNext())
			{
				EntityAnimal entityanimal1 = (EntityAnimal)iterator.next();

				if (entityanimal1.getGrowingAge() >= 0)
				{
					double d1 = this.childAnimal.getDistanceSqToEntity(entityanimal1);

					if (d1 <= d0)
					{
						d0 = d1;
						entityanimal = entityanimal1;
					}
				}
			}

			if (entityanimal == null)
			{
				return false;
			}
			else if (d0 < 9.0D)
			{
				return false;
			}
			else
			{
				this.parentAnimal = entityanimal;
				return true;
			}
		}
	}

	public boolean continueExecuting()
	{
		if (!this.parentAnimal.isEntityAlive())
		{
			return false;
		}
		else
		{
			double d0 = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
			return d0 >= 9.0D && d0 <= 256.0D;
		}
	}

	public void startExecuting()
	{
		this.field_75345_d = 0;
	}

	public void resetTask()
	{
		this.parentAnimal = null;
	}

	public void updateTask()
	{
		if (--this.field_75345_d <= 0)
		{
			this.field_75345_d = 10;
			this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.field_75347_c);
		}
	}
}