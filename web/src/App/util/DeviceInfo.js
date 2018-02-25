class DeviceInfo {
	constructor() {
		this.info = {
			server: "http://shell.srcf.net:8003/",
			name: "tom",
			uid: 1,
			devices: [
				{
					uid: 1,
					type: "IFTTTLight",
					config: {
						name: "Intel Light"
					}
				}, {
					uid: 2,
					type: "PhillipsHue",
					config: {
						"name": "Hue Bulb 1"
					}
				}, {
					uid: 3,
					type: "Chromecast",
					config: {
						name: "Chromecast 1"
					}
				}, {
					uid: 4,
					type: "DeviceGroup",
					config: {
						name: "All lights",
						devices: [1, 2]
					}
				}
			],
			rooms: [
				{
					name: "Intel Lab",
					uid: 2,
					devices: [/*1, */4]
				}, {
					name: "The Street",
					uid: 3,
					devices: [2, 4]
				}, {
					name: "Cafe",
					uid: 4,
					devices: [3]
				}
			]
		}
	}

	updateInfo() {
		// TODO
	}

	saveInfo() {
		// TODO
	}
}

export default DeviceInfo