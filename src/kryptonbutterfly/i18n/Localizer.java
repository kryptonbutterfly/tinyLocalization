package kryptonbutterfly.i18n;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Localizer implements AutoCloseable
{
	Language language;
	public final LocalizationManager manager;
	
	private final HashSet<Localization> localizations = new HashSet<>();
	
	private final HashSet<BiConsumer<String, String>> languageChangeListener = new HashSet<>();
	
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
			Language old = this.language;
			this.language = lang;
			localizations.forEach(Localization::notifyListener);
			languageChangeListener.forEach(listener -> listener.accept(old.languageName, language));
		}
		return lang != null;
	}
	
	public void addLanguageChangeListener(BiConsumer<String, String> listener)
	{
		this.languageChangeListener.add(listener);
	}
	
	public void removeLanguageChangeListener(BiConsumer<String, String> listener)
	{
		this.languageChangeListener.remove(listener);
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