/*

 Resource.java

 Copyright 2004-2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Created on 2005/11/07

 */
package net.sqs2.util;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;


public class Resource {

	private static Map<ResourceBundleKey, ResourceBundle> map = new HashMap<ResourceBundleKey, ResourceBundle>();

	private static class ResourceBundleKey {
		String name;
		Locale locale;

		public ResourceBundleKey(String name, Locale locale) {
			this.name = name;
			this.locale = locale;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ResourceBundleKey) {
				ResourceBundleKey o = (ResourceBundleKey) obj;
				if (o.name.equals(name) && o.locale.equals(locale)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode() ^ locale.hashCode();
		}
	}

	private static String format(ResourceBundle resourceBundle, String key){
		return resourceBundle.getString(key);
	}

	private static Appendable format(Appendable ap, ResourceBundle resourceBundle, String key)throws IOException{
		String src = resourceBundle.getString(key);
		return ap.append(src);
	}

	private static String format(ResourceBundle resourceBundle, String key, Object... args){
		String src = resourceBundle.getString(key);
		Locale l = resourceBundle.getLocale();
		Formatter f = new Formatter(l).format(src, args);
		return f.toString();
	}

	private static Appendable format(Appendable ap, ResourceBundle resourceBundle, String key, Object... args)throws IOException{
		String src = resourceBundle.getString(key);
		Locale l = resourceBundle.getLocale();
		Formatter f = new Formatter(l).format(src, args);
		return ap.append(f.format(src, args).toString());
	}
	
	private static ResourceBundle getResourceBundleValue(String name, Locale locale) {
		ResourceBundleKey resourceBundleKey = new ResourceBundleKey(name, locale);
		ResourceBundle resourceBundle = map.get(resourceBundleKey);
		if(resourceBundle == null){
			resourceBundle = ResourceBundle.getBundle(name, locale);
			map.put(resourceBundleKey, resourceBundle);
		}
		return resourceBundle;
	}

	public static String __(String name, Locale locale, String key, Object... args){
		try{
			ResourceBundle resourceBundle = getResourceBundleValue(name, locale);
			return format(resourceBundle, key, args);
		} catch (Exception ex) {
			logError(name, locale, key);
			throw new RuntimeException(ex);
		}
	}

	private static void logError(String name, Locale locale, String key) {
		Logger.getLogger("ERROR ").severe("ResourceName=" + name + ", Locale=" + locale.toString() + ", Key="+key);
	}

	public static String __(String name, Locale locale, String key){
		try{
			ResourceBundle resourceBundle = getResourceBundleValue(name, locale);
			return format(resourceBundle, key);
		} catch (Exception ex) {
			logError(name, locale, key);
			throw new RuntimeException(ex);
		}
	}

	public static Appendable __(Appendable appendable, String name, Locale locale, String key, Object... args){
		try{
			ResourceBundle resourceBundle = getResourceBundleValue(name, locale);
			return format(appendable, resourceBundle, key, args);
		} catch (Exception ex) {
			logError(name, locale, key);
			throw new RuntimeException(ex);
		}
	}
	
	public static Appendable __(Appendable appendable, String name, Locale locale, String key){
		try{
			ResourceBundle resourceBundle = getResourceBundleValue(name, locale);
			return format(appendable, resourceBundle, key);
		} catch (Exception ex) {
			logError(name, locale, key);
			throw new RuntimeException(ex);
		}
	}
	
	public static String __(String name, String key, Object... args){
		return __(name, Locale.getDefault(), key, args);
	}
	
	public static String __(String name, String key){
		return __(name, Locale.getDefault(), key);
	}
	
	public static Appendable __(Appendable appendable, String name, String key, Object... args){
		return __(appendable, name, Locale.getDefault(), key, args);
	}
	
	public static Appendable __(Appendable appendable, String name, String key){
		return __(appendable, name, Locale.getDefault(), key);
	}
	
	public static int _i(String name, String key) {
		return Integer.parseInt(__(name, key));
	}

	public static double _d(String name, String key) {
		return Double.parseDouble(__(name, key));
	}

	public float _f(String name, String key) {
		return Float.parseFloat(__(name, key));
	}

}
