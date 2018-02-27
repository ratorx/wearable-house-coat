class DeviceInfo {
	constructor() {
		this.info = {}
    this.loaded = false;
    this.configLocation = "http://shell.srcf.net:8003/config";
	}

	updateInfo() {
    fetch(this.configLocation).then(response => {
      if(response.status === 200){
        response.json().then(data => {
          this.info = data.data;
          this.loaded = true;
        });
      }  
    }).catch(err => {
      console.log("Config Fetch Error: ", err);
    })
	}

	saveInfo() {
    let dataObj = {
      data: this.info
    };
    fetch(this.configLocation, {
      method: "put",
      body: JSON.stringify(dataObj)
    }).then(response => {
      response.text().then(data => {
        console.log(data);
      });
    }).catch(err => {
      console.log("Put Config Failed: ", err);
    });
	}
}

export default DeviceInfo
