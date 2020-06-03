package de.tinycodecrank.i18n;

import java.util.Locale;

import de.tinycodecrank.xmlConfig4J.StreamConfig;
import de.tinycodecrank.xmlConfig4J.annotations.Value;

public final class I18nStreamConfig extends StreamConfig
{
	@Value("The folder containing the localizations for all supported languages.")
	public String localizationFolder = "./localizations";
	
	@Value
	public String[] languageLocations = {};
	
	@Value("The default language")
	public String defaultLanguage = Locale.getDefault().toString();
	
	@Value("The fileExtension for localizationfiles.")
	public String extension = ".lang";
	
	@Value("The charset used to seperate the identifier from the translation.")
	public String delimiter = "=";
}