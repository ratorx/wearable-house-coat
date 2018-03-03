class DeviceInfo {
	constructor() {
		this.info = null;
		this.loaded = false;
		this.configLocation = "http://shell.srcf.net:8003/config";
	}

	updateInfo(callback) {
		fetch(this.configLocation).then(response => {
			if(response.status === 200){
				response.json().then(data => {
					this.info = data;
					this.loaded = true;
					console.log("Succesfully received config");
					if(callback != null){
						callback();
					}
				}).catch(err => {
					console.log("Error reading config JSON: ", err);
				});
			}  
		}).catch(err => {
			console.log("Config Fetch Error: ", err);
		})
	}

	saveInfo(callback) {
		fetch(this.configLocation, {
			method: "put",
			body: JSON.stringify(this.info),
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			}
		}).then(response => {
			response.text().then(data => {
				console.log(data);
				if(callback != null){
					callback();
				}
			});
		}).catch(err => {
			console.log("Put Config Failed: ", err);
		});
	}

	createUid() {
		let s4 = () => Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1)
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
	}
}

export default DeviceInfo
