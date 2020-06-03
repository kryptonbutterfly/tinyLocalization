package de.tinycodecrank.i18n;

public final class MissingTranslationException extends RuntimeException
{
	private static final long serialVersionUID = 68907859885569891L;

	public MissingTranslationException()
	{
		super();
	}

	public MissingTranslationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingTranslationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MissingTranslationException(String message)
	{
		super(message);
	}

	public MissingTranslationException(Throwable cause)
	{
		super(cause);
	}
}