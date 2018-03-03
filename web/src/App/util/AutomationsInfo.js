class AutomationsInfo {
	constructor() {
		this.info = [
			{
				"Me": "00000000-0000-0000-0000-000000000100",
				"Locations": {
					"00000000-0000-0000-0000-000000000100": "intel lab"
				},
				"Actions": [
					{
						"device": "00000000-0000-0000-0000-000000000003",
						"method": "setPlaying",
						"parameters": ["Boolean"],
						"arguments": ["true"]
					}
				],
				"LeaveActions": [
					{
						"device": "00000000-0000-0000-0000-000000000003",
						"method": "setPlaying",
						"parameters": ["Boolean"],
						"arguments": ["false"]
					}
				]
			},
			{
				"Me": "00000000-0000-0000-0000-000000000100",
				"Locations": {
					"00000000-0000-0000-0000-000000000100": "intel lab"
				},
				"Actions": [
					{
						"device": "00000000-0000-0000-0000-000000000003",
						"method": "setPlaying",
						"parameters": ["Boolean"],
						"arguments": ["true"]
					}
				],
				"LeaveActions": [
					{
						"device": "00000000-0000-0000-0000-000000000003",
						"method": "setPlaying",
						"parameters": ["Boolean"],
						"arguments": ["false"]
					}
				]
			}
		];
		this.loaded = true;
		this.configLocation = "http://example.com:port";
	}

	updateInfo(callback) {

	}
}

export default AutomationsInfo