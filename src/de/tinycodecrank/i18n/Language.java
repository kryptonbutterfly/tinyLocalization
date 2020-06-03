package de.tinycodecrank.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;

final class Language
{
	private final HashMap<String, String> mapping;
	String languageName;
	private Consumer<MissingLocalizationEvent> missingTranslationListener;
	
	private Language(HashMap<String, String> mapping, String languageName, Consumer<MissingLocalizationEvent> missingTranslationListener)
	{
		this.mapping = mapping;
		this.languageName = languageName;
		this.missingTranslationListener = missingTranslationListener;
	}
	
	void setTranslationListener(Consumer<MissingLocalizationEvent> listener)
	{
		this.missingTranslationListener = listener;
	}
	
	static Language loadFromStream(InputStream i18nStream, String delimiter, String languageName, Consumer<MissingLocalizationEvent> missingTranslationListener) throws IOException
	{
		try (Scanner sc = new Scanner(i18nStream))
		{
			return load(sc, delimiter, languageName, missingTranslationListener);
		}
	}
	
	private static Language load(Scanner sc, String delimiter, String languageName, Consumer<MissingLocalizationEvent> missingTranslationListener)
	{
		HashMap<String, String> mapping = new HashMap<>();
		while(sc.hasNext())
		{
			String line = sc.nextLine().trim();
			if(!line.isEmpty() && !line.startsWith("//"))
			{
				int delimiterIndex = line.indexOf(delimiter);
				if(delimiterIndex != -1)
				{
					String key = line.substring(0,delimiterIndex);
					String value = line.substring(delimiterIndex + delimiter.length());
					value = replaceParts(value);
					mapping.put(key.trim(), value.trim());
				}
			}
			
		}
		return new Language(mapping, languageName, missingTranslationListener);
	}
	
	String localize(String key)
	{
		String local = this.mapping.get(key);
		if(local != null)
		{
			return local;
		}
		else
		{
			MissingLocalizationEvent event = new MissingLocalizationEvent(key, languageName);
			this.missingTranslationListener.accept(event);
			if(event.exception != null)
			{
				throw event.exception;
			}
			else
			{
				return event.returnValue;
			}
		}
	}
	
	private static String replaceParts(String original)
	{
		final String backSlash = "\\\\";
		final String newLine = "\\n";
		
		int index = 0;
		int nextBackSlash = -1;
		int nextNewLine = -1;
		StringBuilder sb = new StringBuilder();
		do
		{
			nextBackSlash = original.indexOf(backSlash, index);
			nextNewLine = original.indexOf(newLine, index);
			if(nextBackSlash != -1)
			{
				if(nextNewLine != -1)
				{
					if(nextBackSlash > nextNewLine)
					{
						sb.append(original.substring(index, nextBackSlash));
						sb.append("\\");
						index = nextBackSlash + 2;
					}
					else
					{
						sb.append(original.substring(index, nextNewLine));
						sb.append("\n");
						index = nextNewLine + 2;
					}
				}
				else
				{
					sb.append(original.substring(index, nextNewLine));
					sb.append("\n");
					index = nextNewLine + 2;
				}
			}
			else
			{
				if(nextNewLine != -1)
				{
					sb.append(original.substring(index, nextNewLine));
					sb.append("\n");
					index = nextNewLine + 2;
				}
				else
				{
					sb.append(original.substring(index));
					index = original.length();
				}
			}
		}
		while(index < original.length());
		return sb.toString();
	}
}