import React from 'react';
import './App.css';
import Navigation from './components/Navigation.js';
import Overview from './components/Overview.js';
import SetRooms from './components/SetRooms.js';
import SetDevices from './components/SetDevices.js';
import Help from './components/Help.js';
import DeviceInfo from "./util/DeviceInfo.js";

class App extends React.Component {
	pages = [
		{name: "Overview"},
		{name: "Setup",
			dropdown: [
				{name: "Rooms"},
				{name: "Devices"}
			]
		},
		{name: "Help"}
	]

	constructor(){
		super();

  let devInfo = new DeviceInfo(this);
  console.log(devInfo.info);
  devInfo.updateInfo(function(){
      this.setState({
        deviceInfo: devInfo
      });

      //Test putting config
      console.log(devInfo.info);
      console.log(JSON.stringify(devInfo.info));
      devInfo.saveInfo();
    }, this);

		this.state = {
			currentPage: this.pages[1].dropdown[0],
			deviceInfo: devInfo,
		};
	}

	setCurrentPage(page) {
		this.setState((prevState) => {
			return {
				currentPage: page
			}
		})
	}

	deleteDevice(device) {
		this.setState((prevState) => {
			for(let room of prevState.deviceInfo.info.data.rooms) {
				let roomDeviceIndex = room.devices.indexOf(device.uid);
				if(roomDeviceIndex !== -1) {
					room.devices.splice(roomDeviceIndex, 1);
				}
			}
			let deviceIndex = prevState.deviceInfo.info.data.devices.indexOf(device);
			prevState.deviceInfo.info.data.devices.splice(deviceIndex, 1);
			return {};
		})
	}

	saveDeviceEdits({device, name, room}) {
		if(device !== null) {
			this.setState((prevState) => {
				device.name = name;

				let oldRoom = prevState.deviceInfo.info.data.rooms.find(room => room.devices.includes(device.uid));
				if(room !== oldRoom) {
					if(oldRoom) {
						let oldRoomDeviceIndex = oldRoom.devices.indexOf(device.uid);
						oldRoom.devices.splice(oldRoomDeviceIndex, 1);
					}
					room.devices.push(device.uid);
				}
				return {}
			});
		}
	}

	deleteRoom(room) {
		this.setState((prevState) => {
			let roomIndex = prevState.deviceInfo.info.data.rooms.indexOf(room);
			if(roomIndex !== -1)
				prevState.deviceInfo.info.data.rooms.splice(roomIndex, 1);
			return {};
		})
	}

	addRoom() {
		let newRoom = {
			uid: this.state.deviceInfo.createUid(),
			name: "",
			devices: []
		};
		this.setState((prevState) => {
			prevState.deviceInfo.info.data.rooms.push(newRoom);
			return {}
		});
		return newRoom
	}

	saveRoomEdits({room, name, devices}) {
		if(room){
			this.setState((prevState) => {
				room.name = name
				room.devices = devices
				return {}
			});
		}
	}

	render() {
		let contentComponent;
		switch(this.state.currentPage.name) {
			case "Overview":
				contentComponent = <Overview/>
				break;
			case "Rooms":
				contentComponent = <SetRooms
					devices={this.state.deviceInfo.info.data.devices}
					rooms={this.state.deviceInfo.info.data.rooms}
					onAddRoom={this.addRoom.bind(this)}
					onDeleteRoom={this.deleteRoom.bind(this)}
					onSaveEdits={this.saveRoomEdits.bind(this)}
				/>
				break;
			case "Devices":
				contentComponent = <SetDevices
					devices={this.state.deviceInfo.info.data.devices}
					rooms={this.state.deviceInfo.info.data.rooms}
					onDeleteDevice={this.deleteDevice.bind(this)}
					onSaveEdits={this.saveDeviceEdits.bind(this)}
				/>
				break;
			case "Help":
				contentComponent = <Help/>
				break;
			default:
				contentComponent = null
		}

		return <div>
			<Navigation
				onBrandClick={() => {this.setCurrentPage.call(this, this.pages[0])}}
				pages={this.pages}
				activePage={this.state.currentPage}
				onSelectPage={this.setCurrentPage.bind(this)}
			/>
			<div className="content">
				{contentComponent}
			</div>
		</div>
	}
}

export default App;
