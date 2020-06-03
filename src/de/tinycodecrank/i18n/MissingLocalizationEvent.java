package de.tinycodecrank.i18n;

public final class MissingLocalizationEvent
{
	private final String identifier;
	private final String language;
	private boolean consumed = false;
	public MissingTranslationException exception = null;
	public String returnValue = null;
	
	MissingLocalizationEvent(String identifier, String language)
	{
		this.identifier = identifier;
		this.language = language;
	}
	
	public String identifier()
	{
		return identifier;
	}
	
	public String language()
	{
		return language;
	}
	
	public boolean consumed()
	{
		return consumed;
	}
	
	public void consume()
	{
		this.consumed = true;
	}
}