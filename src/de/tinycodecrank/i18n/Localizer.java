package de.tinycodecrank.i18n;

import java.util.HashSet;
import java.util.function.Consumer;

public final class Localizer implements AutoCloseable
{
	Language language;
	public final LocalizationManager manager;
	
	private final HashSet<Localization> localizations = new HashSet<>();
	
	Localizer(LocalizationManager manager, Language lang)
	{
		this.manager = manager;
		this.language = lang;
	}
	
	@SafeVarargs
	public final Localization get(String key, Consumer<String> ... listener)
	{
		Localization localization = new Localization(key, this, listener);
		localizations.add(localization);
		return localization;
	}
	
	void remove(Localization localization)
	{
		this.localizations.remove(localization);
	}
	
	public String localize(String key)
	{
		return language.localize(key);
	}
	
	public boolean setCurrentLanguage(String language)
	{
		Language lang = manager.languages.get(language);
		if(lang != null)
		{
			this.language = lang;
			localizations.forEach(Localization::notifyListener);
		}
		return lang != null;
	}
	
	public String currentLanguage()
	{
		return language.languageName;
	}

	@Override
	public void close()
	{
		localizations.clear();
	}
}