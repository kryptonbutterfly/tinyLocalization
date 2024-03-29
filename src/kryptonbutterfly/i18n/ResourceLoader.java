package kryptonbutterfly.i18n;

import java.io.InputStream;
import java.util.function.Supplier;

import kryptonbutterfly.functions.applicable.ApplicableLeft;
import kryptonbutterfly.functions.applicable.ApplicableRight;

@FunctionalInterface
public interface ResourceLoader extends ApplicableLeft<String, Supplier<InputStream>>, ApplicableRight<String, Supplier<InputStream>>
{
	public InputStream load(String resource);
	
	@Override
	default Supplier<InputStream> aptFirst(String resource)
	{
		return () -> load(resource);
	}
	
	@Override
	default Supplier<InputStream> aptLast(String resource)
	{
		return aptFirst(resource);
	}
}