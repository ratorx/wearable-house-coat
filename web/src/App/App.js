import React from 'react';
import './App.css';
import { PageHeader } from 'react-bootstrap';
import { GoogleAPI, GoogleLogin, GoogleLogout } from 'react-google-oauth';
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
<<<<<<< HEAD
		devInfo.updateInfo((() =>
			this.setState({
				deviceInfo: devInfo
			})).bind(this)
=======
		devInfo.updateInfo((() => 
			this.setState({ 
				deviceInfo: devInfo 
			}))
>>>>>>> 6563744dc7f87601a5fcd73d6df73d01c8677637
		);

		this.state = {
			currentPage: this.pages[0],
			deviceInfo: devInfo,
			googleUser: null
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
			prevState.deviceInfo.saveInfo();
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
				prevState.deviceInfo.saveInfo();
				return {}
			});
		}
	}

	deleteRoom(room) {
		this.setState((prevState) => {
			let roomIndex = prevState.deviceInfo.info.data.rooms.indexOf(room);
			if(roomIndex !== -1){
				prevState.deviceInfo.info.data.rooms.splice(roomIndex, 1);
				prevState.deviceInfo.saveInfo();
			}
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
			prevState.deviceInfo.saveInfo();
			return {}
		});
		return newRoom
	}

	saveRoomEdits({room, name, devices}) {
		if(room){
			this.setState((prevState) => {
				room.name = name
				room.devices = devices
				prevState.deviceInfo.saveInfo();
				return {}
			});
		}
	}

	onLoginSuccess(googleUser) {
		console.log(googleUser)
		this.setState(() => {
			return {
				googleUser: googleUser
			}
		})
	}

	onLogoutSuccess(t) {
		this.setState(() => {
			return {
				googleUser: null
			}
		})
	}

	render() {
		return <GoogleAPI clientId="695081174378-hn8bchfp0lque5htjpkv63noa28b5iee.apps.googleusercontent.com">
		{
			(this.state.googleUser !== null) ?
				<div>
					<Navigation
						onBrandClick={() => {this.setCurrentPage.call(this, this.pages[0])}}
						pages={this.pages}
						activePage={this.state.currentPage}
						onSelectPage={this.setCurrentPage.bind(this)}
						googleUser={this.state.googleUser}
						logOut={
							<GoogleLogout
								backgroundColor="#00A6FB"
								text="Log out"
								onLogoutSuccess={this.onLogoutSuccess.bind(this)}
							/>
						}
					/>
					<div className="content">
						{
							(this.state.currentPage.name === "Overview") ?
								<Overview
									name={this.state.googleUser.w3.ig}
								/>
							: (this.state.currentPage.name === "Rooms") ?
								<SetRooms
									devices={this.state.deviceInfo.info.data.devices}
									rooms={this.state.deviceInfo.info.data.rooms}
									onAddRoom={this.addRoom.bind(this)}
									onDeleteRoom={this.deleteRoom.bind(this)}
									onSaveEdits={this.saveRoomEdits.bind(this)}
								/>
							: (this.state.currentPage.name === "Devices") ?
								<SetDevices
									devices={this.state.deviceInfo.info.data.devices}
									rooms={this.state.deviceInfo.info.data.rooms}
									onDeleteDevice={this.deleteDevice.bind(this)}
									onSaveEdits={this.saveDeviceEdits.bind(this)}
								/>
							: (this.state.currentPage.name === "Help") ?
								<Help/>
							: null
						}
					</div>
				</div>
			:
				<div className="content">
					<Navigation pages={[]}/>
					<PageHeader>Log in</PageHeader>
					<div className="col-center">
						<GoogleLogin
							backgroundColor="#00A6FB"
							text="Google Login"
							width="auto"
							onLoginSuccess={this.onLoginSuccess.bind(this)}
						/>
					</div>
				</div>
		}
		</GoogleAPI>
	}
}

export default App;
