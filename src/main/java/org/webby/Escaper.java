package org.webby;

public abstract class Escaper {

	public static String escapeHtml(Object input) {
		if (input == null) {
			return "";
		}
		String s = input.toString();
		s = s.replace("&", "&amp;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		s = s.replace("\"", "&quot;");
		return s;
	}

	public static String escapeJs(Object input) {
		if (input == null) {
			return "";
		}
		String s = input.toString();
		s = s.replace("\"", "\\\"");
		s = s.replace("\'", "\\\'");
		return s;
	}

}
