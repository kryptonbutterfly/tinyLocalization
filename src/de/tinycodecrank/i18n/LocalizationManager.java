package de.tinycodecrank.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

public final class LocalizationManager
{
	private final Deque<Consumer<MissingLocalizationEvent>> missingTranslationListener = new LinkedList<>();
	
	private final void translationListener(MissingLocalizationEvent event)
	{
		for (Consumer<MissingLocalizationEvent> l : missingTranslationListener)
		{
			l.accept(event);
			if (event.consumed())
			{
				break;
			}
		}
	}
	
	public final I18nStreamConfig	config;
	HashMap<String, Language>		languages			= new HashMap<>();
	private Language				defaultLanguage;
	public boolean					throwWhenMissing	= false;
	
	public LocalizationManager(
		String configLocation,
		ResourceLoader loader,
		Consumer<MissingLocalizationEvent> listener)
		throws IOException
	{
		this(configLocation, loader);
		missingTranslationListener.addFirst(listener);
	}
	
	public LocalizationManager(String configLocation, ResourceLoader loader) throws IOException
	{
		missingTranslationListener.addFirst(this::defaultTranslationListener);
		this.config = loadConfig(configLocation, loader);
		load(loader);
	}
	
	public LocalizationManager(
		I18nStreamConfig config,
		ResourceLoader loader,
		Consumer<MissingLocalizationEvent> listener)
		throws IOException
	{
		this(config, loader);
		missingTranslationListener.addFirst(listener);
	}
	
	public LocalizationManager(I18nStreamConfig config, ResourceLoader loader) throws IOException
	{
		missingTranslationListener.addFirst(this::defaultTranslationListener);
		this.config = config;
		load(loader);
	}
	
	private void load(ResourceLoader loader) throws IOException
	{
		File[] langFiles = getLocalizationsFromFolder();
		if (langFiles != null)
		{
			for (File langFile : langFiles)
			{
				try (FileInputStream iStream = new FileInputStream(langFile))
				{
					if (iStream != null)
					{
						String	fileName	= langFile.getName();
						String	langName	= fileName.substring(0, fileName.length() - config.extension.length());
						this.languages.put(
							langName,
							Language.loadFromStream(iStream, config.delimiter, langName, this::translationListener));
					}
				}
			}
		}
		for (String lang : config.languageLocations)
		{
			try (InputStream iStream = loader.load(lang))
			{
				if (iStream != null)
				{
					String langName = lang
						.substring(lang.lastIndexOf("/") + 1, lang.length() - config.extension.length());
					if (!this.languages.containsKey(langName))
					{
						this.languages.put(
							langName,
							Language.loadFromStream(iStream, config.delimiter, langName, this::translationListener));
					}
				}
			}
		}
		this.defaultLanguage = this.languages.get(config.defaultLanguage);
	}
	
	public static I18nStreamConfig loadConfig(String configLocation, ResourceLoader loader)
		throws IOException
	{
		I18nStreamConfig i18nConfig = new I18nStreamConfig();
		loadConfig(i18nConfig, configLocation, loader);
		return i18nConfig;
	}
	
	public static void loadConfig(
		I18nStreamConfig i18nConfig,
		String configLocation,
		ResourceLoader loader)
		throws IOException
	{
		try (InputStream configStream = loader.load(configLocation))
		{
			if (configStream != null)
			{
				i18nConfig.load(configStream);
			}
			else
			{
				try (FileOutputStream oStream = new FileOutputStream(new File("./src" + configLocation)))
				{
					i18nConfig.save(oStream);
				}
			}
		}
	}
	
	public void addLocalizationListener(Consumer<MissingLocalizationEvent> listener)
	{
		this.missingTranslationListener.addFirst(listener);
	}
	
	private File[] getLocalizationsFromFolder()
	{
		if (config.localizationFolder != null)
		{
			File languageFolder = new File(config.localizationFolder);
			if (languageFolder.exists() && languageFolder.isDirectory())
			{
				return languageFolder.listFiles((File dir, String name) -> name.endsWith(config.extension));
			}
		}
		return null;
	}
	
	public void defaultTranslationListener(MissingLocalizationEvent event)
	{
		final String message = String
			.format("No translation found for: \"%s\" in %s", event.identifier(), event.language());
		if (throwWhenMissing)
		{
			event.exception = new MissingTranslationException(message);
		}
		else
		{
			System.err.println(message);
			event.returnValue = event.identifier();
		}
		event.consume();
	}
	
	public Localizer buildLocalizer()
	{
		return new Localizer(this, this.defaultLanguage);
	}
	
	public Localizer buildLocalizer(String language)
	{
		Language lang = this.languages.get(language);
		return new Localizer(this, lang);
	}
	
	public String[] languages()
	{
		return this.languages.keySet().toArray(new String[0]);
	}
}