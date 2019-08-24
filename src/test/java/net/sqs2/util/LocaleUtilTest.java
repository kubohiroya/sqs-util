package net.sqs2.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class LocaleUtilTest {
	
	private Set<String> createStringSet(String[] itemArray){
		final Set<String> set = new HashSet<String>();
		for(String item: itemArray){
			set.add(item);
		}
		return set;
	}

	static class LocaleFilter implements LocaleUtil.LocaleFilter{
		Set<String> set; 
		LocaleFilter(Set<String> set){
			this.set = set;
		}
		
		@Override
		public boolean filter(Locale locale, String localeSuffix) {
			String name = "hoge"+localeSuffix+".properties";
			try{
				return set.contains(name);
			}catch(Exception ignore){
			}
			return false;
		}
		
	}

	@Test
	public void selectLocaleTest(){
		
		final Set<String> src1 = createStringSet(new String[]{
				"hoge_ja_JP.properties",
				"hoge_ja.properties",
				"hoge_fr_CA.properties",
				"hoge_en.properties",
				"hoge_it.properties",
				"hoge.properties"
		});
		
		LocaleFilter filter = new LocaleFilter(src1); 

		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.JAPANESE}, filter).getLocaleSuffix(), "_ja");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.CANADA_FRENCH}, filter).getLocaleSuffix(), "_fr_CA");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.ENGLISH}, filter).getLocaleSuffix(), "_en");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.ITALIAN}, filter).getLocaleSuffix(), "_it");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.FRENCH}, filter).getLocaleSuffix(), ""); // not found, "" will be used.
	}
	
	@Test
	public void selectDefaultLocaleTest(){
		final Set<String> src2 = createStringSet(new String[]{
				"hoge_ja_JP.properties",
				"hoge_ja.properties",
				"hoge_fr_CA.properties",
				"hoge_en.properties",
				"hoge_it.properties",
		});
		LocaleFilter filter = new LocaleFilter(src2); 
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.FRENCH}, filter).getLocaleSuffix(), "_en"); // not found, "_en" will be used.
	}

	@Test
	public void selectLocleWithPriorityTest(){
		final Set<String> src2 = createStringSet(new String[]{
				"hoge_ja_JP.properties",
				"hoge_ja.properties",
				"hoge_fr_CA.properties",
				"hoge_en.properties",
				"hoge_it.properties",
		});
		LocaleFilter filter = new LocaleFilter(src2); 
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.JAPANESE, Locale.ITALIAN, Locale.ENGLISH}, filter).getLocaleSuffix(), "_ja");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.ITALIAN, Locale.JAPANESE, Locale.ENGLISH}, filter).getLocaleSuffix(), "_it");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.ITALIAN, Locale.JAPANESE, Locale.ENGLISH, Locale.FRENCH}, filter).getLocaleSuffix(), "_it");
		
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.CHINESE, Locale.ITALIAN, Locale.JAPANESE, Locale.GERMANY}, filter).getLocaleSuffix(), "_it");
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.CHINESE, Locale.FRENCH, Locale.ITALIAN}, filter).getLocaleSuffix(), "_it"); 
		
		assertEquals(LocaleUtil.selectLocale(new Locale[]{Locale.CHINESE, Locale.FRENCH, Locale.GERMAN}, filter).getLocaleSuffix(), "_en");
	}

}
