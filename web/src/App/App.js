import React from 'react';
import './App.css';
import { PageHeader, Alert, Row, Col } from 'react-bootstrap';
import { GoogleAPI, GoogleLogin, GoogleLogout } from 'react-google-oauth';
import Navigation from './components/Navigation.js';
import Overview from './components/Overview.js';
import SetRooms from './components/SetRooms.js';
import SetDevices from './components/SetDevices.js';
import Automations from './components/Automations.js';
import Help from './components/Help.js';
import DeviceInfo from './util/DeviceInfo.js';
import AutomationsInfo from './util/AutomationsInfo.js';

class App extends React.Component {
	pages = [
		{name: "Overview"},
		{name: "Setup",
			dropdown: [
				{name: "Rooms"},
				{name: "Devices"},
				{name: "Automations"}
			]
		},
		{name: "Help"}
	]

	constructor(){
		super();

		this.state = {
			currentPage: this.pages[0],
			deviceInfo: new DeviceInfo(),
			autoInfo: new AutomationsInfo(),
			curUserID: "",
			googleUser: null
		};
	}

	loadConfigAuto() {
		this.state.deviceInfo.updateInfo(() => {
			let user = this.state.deviceInfo.info.data.people.find(usr => usr.email === this.state.googleUser.w3.U3)
			this.state.autoInfo.updateInfo(user.uid, () => {
				this.setState(() => {
					return {
						curUserID: user.uid
					}
				})
			})
		})
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

	saveAutomations() {
		this.state.autoInfo.saveInfo(this.state.curUserID);
	}

	deleteAutomation(automation) {
		this.setState((prevState) => {
			let automationIndex = prevState.autoInfo.info.indexOf(automation);
			prevState.autoInfo.info.splice(automationIndex, 1);
			return {}
		}, this.saveAutomations)
	}

	addAutomation() {
		this.setState((prevState) => {
			prevState.autoInfo.info.push({
				Me: this.state.curUserID,
				Locations: {},
				Actions: [],
				LeaveActions: []
			})
			return {}
		}, this.saveAutomations)
	}

	onLoginSuccess(googleUser) {
		console.log(googleUser)
		this.setState(() => {
			this.loadConfigAuto();
			return {
				googleUser: googleUser
			}
		})
	}

	onLogoutSuccess(t) {
		this.setState(() => {
			return {
				googleUser: null,
				deviceInfo: new DeviceInfo(),
				autoInfo: new AutomationsInfo(),
				currentPage: this.pages[0]
			}
		})
	}

	render() {
		return <GoogleAPI clientId="695081174378-hn8bchfp0lque5htjpkv63noa28b5iee.apps.googleusercontent.com">
		{
			(this.state.googleUser !== null) ?
				(this.state.deviceInfo.loaded && this.state.autoInfo.loaded) ?
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
										devices={this.state.deviceInfo.info.data.devices}
										rooms={this.state.deviceInfo.info.data.rooms}
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
								: (this.state.currentPage.name === "Automations") ?
									<Automations
										automations={this.state.autoInfo.info}
										users={this.state.deviceInfo.info.data.people}
										rooms={this.state.deviceInfo.info.data.rooms}
										devices={this.state.deviceInfo.info.data.devices}
										onDeleteAutomation={this.deleteAutomation.bind(this)}
										onAddAutomation={this.addAutomation.bind(this)}
										onUpdateAutomation={this.saveAutomations.bind(this)}
									/>
								: (this.state.currentPage.name === "Help") ?
									<Help
										name={this.state.googleUser.w3.ig}
									/>
								: null
							}
						</div>
					</div>
				:
					<div className="content">
						<Navigation pages={[]}/>
						<Row>
							<Col xs={12} smOffset={2} sm={8} lgOffset={3} lg={6}>
								<Alert bsStyle="danger">
									<h4>Unable to load configuration</h4>
									Either the server is unreachable or the user is not found.
								</Alert>
							</Col>
						</Row>
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
