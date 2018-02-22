package clquebec.com.environment;

/**
 * Created by reeto on 06/02/18.
 */

public final class Keys {
    public static final String IFTTT = "d5xeD19a2M8W439R_YD4Bf";

		public static final String ConfigJSON = "{"+
			"'server': 'http://shell.srcf.net:8003/',"+
			"'name': 'tom',"+
			"'uid': 'aaaa-bbbb-cccc',"+
			"'rooms': [{"+
				"'name':'Intel Lab',"+
				"'uid': 'xxxxx-yyyy-zzzzz',"+
				"'devices': [{"+
					"'type': 'IFTTTLight',"+
					"'config': {"+
						"'name': 'Intel Light'"+
					"}"+
			"}, {"+
				"'name':'The Street',"+
				"'uid':'aaaa-bbbb-cccc-d',"+
				"'devices':[{"+
					"'type': 'PhillipsHue',"+
					"'config': {"+
						"'name': 'Hue Bulb 1'"+
					"}"+
			"}, {"+
				"'name':'Cafe',"+
				"'uid':'aaaaaaaaaa',"+
				"'devices':[{"+
					"'type': 'Chromecast',"+
					"'config': {"+
						"'name': 'Chromecast 1'"+
					"}"+
			"}"+
		"}";
}
