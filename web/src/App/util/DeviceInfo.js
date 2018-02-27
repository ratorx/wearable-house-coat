class DeviceInfo {
	constructor() {
		// this.info = {
		// 	server: "http://shell.srcf.net:8003/",
		// 	name: "tom",
		// 	uid: 1,
		// 	devices: [
		// 		{
		// 			uid: 1,
		// 			type: "IFTTTLight",
		// 			config: {
		// 				name: "Intel Light"
		// 			}
		// 		}, {
		// 			uid: 2,
		// 			type: "PhillipsHue",
		// 			config: {
		// 				"name": "Hue Bulb 1"
		// 			}
		// 		}, {
		// 			uid: 3,
		// 			type: "Chromecast",
		// 			config: {
		// 				name: "Chromecast 1"
		// 			}
		// 		}, {
		// 			uid: 4,
		// 			type: "DeviceGroup",
		// 			config: {
		// 				name: "All lights",
		// 				devices: [1, 2]
		// 			}
		// 		}
		// 	],
		// 	rooms: [
		// 		{
		// 			name: "Intel Lab",
		// 			uid: 2,
		// 			devices: [1, 4]
		// 		}, {
		// 			name: "The Street",
		// 			uid: 3,
		// 			devices: [2, 4]
		// 		}, {
		// 			name: "Cafe",
		// 			uid: 4,
		// 			devices: [3]
		// 		}
		// 	]
		// }
		this.info = {
			"data": {
				"server": "http://shell.srcf.net:8003/",
				"me": "00000000-0000-0000-0000-000000000100",
				"devices": [
					{
						"uid": "00000000-0000-0000-0000-000000000001",
						"type": "IFTTTLight",
						"name": "Intel Light",
						"config": {}
					}, {
						"uid": "00000000-0000-0000-0000-000000000002",
						"type": "PhillipsHue",
						"name": "Hue Bulb 1",
						"config": {}
					}, {
						"uid": "00000000-0000-0000-0000-000000000003",
						"type": "Chromecast",
						"name": "Chromecast 1",
						"config": {}
					}
				],
				"rooms": [
					{
						"uid": "00000000-0000-0000-0000-000000000010",
						"name": "Intel Lab",
						"devices": ["00000000-0000-0000-0000-000000000001"]
					}, {
						"uid": "00000000-0000-0000-0000-000000000011",
						"name": "The Street",
						"devices": ["00000000-0000-0000-0000-000000000002"]
					}, {
						"uid": "00000000-0000-0000-0000-000000000012",
						"name": "Cafe",
						"devices": ["00000000-0000-0000-0000-000000000003"]
					}
				],
				// "groups": [
				// 	{
				// 		"uid":"",
				// 		"name":"All Lights",
				// 		"devices": ["00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002"]
				// 	}
				// ],
				"people": [
					{
						"name": "TestPerson",
						"uid": "00000000-0000-0000-0000-000000000100"
					}, {
						"name": "TestPerson2",
						"uid": "00000000-0000-0000-0000-000000000101"
					}
				]
			}
		}
	}

	updateInfo() {
		// TODO
	}

	saveInfo() {
		// TODO
	}

	createUid() {
		return "dkfjahgsdkjfhaslkdjfhalskjdhflaksjdhflajsdhflkajh";
	}
}

export default DeviceInfo