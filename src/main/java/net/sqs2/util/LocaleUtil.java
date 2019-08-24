package net.sqs2.util;

import java.util.Locale;

public class LocaleUtil {
	
	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	public static final String DEFAULT_LOCALE_SUFFIX = "_en";
	
	public static interface LocaleFilter{
		public boolean filter(Locale locale, String localeSuffix);
	} 
	
	public static class SelectedLocale{
		Locale locale;
		String localeSuffix;
		SelectedLocale(Locale locale, String localeSuffix){
			this.locale = locale;
			this.localeSuffix = localeSuffix;
		}
		public Locale getLocale() {
			return locale;
		}
		public String getLocaleSuffix() {
			return localeSuffix;
		}
		
	}

	public static SelectedLocale selectLocale(Locale[] localeArrayByPriority, LocaleFilter filter){

		for(Locale loc : localeArrayByPriority){
			String language = loc.getLanguage();
			String country = loc.getCountry();
			String variant = loc.getVariant();
			String[] suffixList = new String[]{
					"_"+language+"_"+country+"_"+variant,
					"_"+language+"_"+country,
					"_"+language,
					""};
			for(String suffix: suffixList){
				if(filter.filter(loc, suffix)){
					return new SelectedLocale(loc, suffix);
				}
			}
		}
		return new SelectedLocale(DEFAULT_LOCALE, DEFAULT_LOCALE_SUFFIX);
	}
}
