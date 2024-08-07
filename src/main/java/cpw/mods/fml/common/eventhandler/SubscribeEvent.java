package cpw.mods.fml.common.eventhandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeEvent
{
	public EventPriority priority() default EventPriority.NORMAL;
	public boolean receiveCanceled() default false;
}