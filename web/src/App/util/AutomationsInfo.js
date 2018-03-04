class AutomationsInfo {
	constructor() {
		this.info = null;
		this.loaded = true;
		this.autoLocation = "http://shell.srcf.net:8003/automations";
	}

	updateInfo(userid, callback) {
		fetch(this.autoLocation + "?user=" + userid).then(response => {
			if(response.status === 200) {
				response.json().then(data => {
					this.info = data ? data : [];
					this.loaded = true;
					console.log("Succesfully received automations");
					if(callback != null){
						callback();
					}
				}).catch(err => {
					console.log("Error loading automations JSON: ", err);
				});
			}
		}).catch(err => {
			console.log("Automations fetch error: ", err);
		})
	}

	saveInfo(userid, callback) {
		fetch(this.autoLocation + "?user=" + userid, {
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
			console.log("Put Automations Failed: ", err);
		});
	}
}

export default AutomationsInfo