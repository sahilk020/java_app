package com.pay10.pg.core.util;

public enum LocaleLanguageType {

	ENGLISH		("English", "en"),
	SPANISH		("EspaÃ±ol", "es"),
	FRENCH	    ("FranÃ§aise", "fr"),
	GERMAN		("Deutsch", "ge"),
	HINDI		("à¤¹à¤¿à¤¨à¥�à¤¦à¥€", "lo-hn"),
	PUNJABI		("à¨ªà©°à¨œà¨¾à¨¬à©€", "lo-pn"),
	MARATHI		("à¤®à¤°à¤¾à¤ à¥€", "lo-ma"),
	GUJRATI	    ("àª—à«�àªœàª°àª¾àª¤à«€", "lo-gj"),
	BENGALI		("à¦¬à¦¾à¦‚à¦²à¦¾", "lo-bn"),
	TAMIL		("à®¤à®®à®¿à®´à¯�", "lo-ta"),
	TELGU	    ("à°¤à±†à°²à±�à°—à±�", "lo-te"),
	KANNADA		("à²•à²¨à³�à²¨à²¡", "lo-ka");
	
	
	private final String name;
	private final String code;
	
	private LocaleLanguageType(String name, String code){
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}
}
