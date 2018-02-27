class DeviceInfo {
	constructor(app) {
		this.info = {data:{
      devices:[],
      people:[],
      rooms:[],
    }};
    this.updateInfo(function(){
      console.log("Callback");
      this.setState();
    }, app);
    this.loaded = false;
    this.configLocation = "http://shell.srcf.net:8004/config";
	}

	updateInfo(callback, app) {
    fetch(this.configLocation).then(response => {
      if(response.status === 200){
        response.json().then(data => {
          this.info = data;
          this.loaded = true;
          console.log("Succesfully received config");
          if(callback != null){
            if(app != null){
              callback.call(app);
            }else{
              callback();
            }
          }
        }).catch(err => {
          console.log("Error reading config JSON: ", err);
        });
      }  
    }).catch(err => {
      console.log("Config Fetch Error: ", err);
    })
	}

	saveInfo(callback, app) {
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
          if(app != null){
            callback.call(app);
          }else{
            callback();
          }
        }
      });
    }).catch(err => {
      console.log("Put Config Failed: ", err);
    });
	}

	createUid() {
		return "dkfjahgsdkjfhaslkdjfhalskjdhflaksjdhflajsdhflkajh";
	}
}

export default DeviceInfo
