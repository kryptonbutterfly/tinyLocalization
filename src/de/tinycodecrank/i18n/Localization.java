package de.tinycodecrank.i18n;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Localization implements Supplier<String>, AutoCloseable
{
	private String key;
	private Localizer localizer;
	
	private HashSet<Consumer<String>> changeListener = new HashSet<>();
	
	Localization(String key, Localizer localizer, Consumer<String>[] listener)
	{
		this.key = key;
		this.localizer = localizer;
		Arrays.stream(listener)
			.forEach(this::addApply);
	}
	
	public String get()
	{
		return localizer.language.localize(key);
	}
	
	public void notifyListener()
	{
		changeListener.forEach(l -> l.accept(get()));
	}
	
	public Localization add(Consumer<String> listener)
	{
		this.changeListener.add(listener);
		return this;
	}
	
	public Localization addApply(Consumer<String> listener)
	{
		this.changeListener.add(listener);
		listener.accept(get());
		return this;
	}

	@Override
	public void close()
	{
		this.localizer.remove(this);
		this.changeListener.clear();
	}
}