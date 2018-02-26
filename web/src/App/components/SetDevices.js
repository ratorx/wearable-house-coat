// Can mutate the references to each of the devices but requires a separate
// onDeleteDevice event passed in props to remove the device from
// a parent component's list of devices

import React from 'react';
import './Settings.css';
import { PageHeader, ListGroup, ListGroupItem, Row, Col, FormControl, Button } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js'
import editIcon from '../res/edit.png';
import deleteIcon from '../res/delete.png';

class SetDevices extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			devices: this.props.devices,
			rooms: this.props.rooms,
			deleteDialog: {
				shown: false,
				device: null
			},
			editDevice: {
				device: null, // Reference to original device, do not change unless confirmed
				room: null, // Room during editing. Change the reference but not its value
				name: null // Name during editing
			}
		}
	}

	showDeleteDialog(device) {
		this.setState((prevState) => {
			if(prevState.deleteDialog.shown)
				return {};
			return {
				deleteDialog: {
					shown: true,
					device: device
				}
			}
		});
	}

	hideDeleteDialog() {
		this.setState((prevState) => {
			return {
				deleteDialog: {
					shown: false,
					device: null
				}
			}
		})
	}

	deleteDevice() {
		this.setState((prevState, props) => {
			props.onDeleteDevice(prevState.deleteDialog.device);
			prevState.deleteDialog = {
				shown: false,
				device: null
			}
			return prevState;
		})
	}

	getDeviceRoom(device) {
		for(let room of this.state.rooms)
			for(let deviceUid of room.devices)
				if(device.uid === deviceUid)
					return room;
		return undefined;
	}

	preFinishEditing(prevState) {
		if(prevState.editDevice.device == null)
			return;
		prevState.editDevice.device.config.name = prevState.editDevice.name;

		if(prevState.editDevice.room != null) {
			let oldRoomIndex;
			for (oldRoomIndex = 0; oldRoomIndex < prevState.rooms.length; oldRoomIndex++) {
				if(prevState.rooms[oldRoomIndex].devices.includes(prevState.editDevice.device.uid)) {
					let deviceIndexOldRoom = prevState.rooms[oldRoomIndex].devices.indexOf(prevState.editDevice.device.uid);
					prevState.rooms[oldRoomIndex].devices.splice(deviceIndexOldRoom, 1);
					break;
				}
			}
		}

		let newRoomIndex;
		for(newRoomIndex = 0; newRoomIndex < prevState.rooms.length; newRoomIndex++) {
			if(prevState.rooms[newRoomIndex] === prevState.editDevice.room) {
				prevState.rooms[newRoomIndex].devices.push(prevState.editDevice.device.uid);
				break;
			}
		}
	}

	finishEditing() {
		this.setState((prevState) => {
			this.preFinishEditing(prevState);
			prevState.editDevice = {
				device: null,
				rooms: null,
				name: null
			}
			return prevState;
		});
	}

	startEditing(device) {
		this.setState((prevState) => {
			this.preFinishEditing(prevState);
			let editRoom = null;
			for(let room of prevState.rooms) {
				if(room.devices.includes(device.uid)) {
					editRoom = room;
					break;
				}
			}
			prevState.editDevice = {
				device: device,
				room: editRoom,
				name: device.config.name
			};
			return prevState;
		});
	}

	cancelEditing() {
		this.setState((prevState) => {
			prevState.editDevice = {
				device: null,
				room: null,
				name: null
			}
			return prevState;
		})
	}

	editNameChange(e) {
		let newName = e.target.value;
		this.setState((prevState) => {
			return {
				editDevice: {
					device: prevState.editDevice.device,
					room: prevState.editDevice.room,
					name: newName
				}
			}
		})
	}

	editRoomChange(e) {
		let uid = Number(e.target.value);
		let newRoom = this.state.rooms.find((room) => room.uid === uid);
		if(typeof newRoom === "undefined")
			newRoom = null;
		this.setState((prevState) => {
			return {
				editDevice: {
					device: prevState.editDevice.device,
					room: newRoom,
					name: prevState.editDevice.name
				}
			}
		})
	}

	componentWillReceiveProps(newProps) {
		this.setState(() => {
			return {
				devices: newProps.devices,
				rooms: newProps.rooms
			}
		})
	}

	componentWillUnmount() {
		// setState does not execute inside componentWillUnmount
		// simply update the references stored in the state
		this.preFinishEditing(this.state);
	}

	render() {
		return <div>
			<PageHeader>Configure Devices</PageHeader>
			{this.state.deleteDialog.shown ? <ConfirmDelete type="device" name={this.state.deleteDialog.device.config.name} onCancel={this.hideDeleteDialog.bind(this)} onDelete={this.deleteDevice.bind(this)}/> : <div/>}
			<Row>
				<Col xs={0} sm={2} lg={3}/>
				<Col xs={12} sm={8} lg={6}>
					<ListGroup>
						<ListGroupItem>
							<Row>
								<Col xs={9}>
									<Row>
										<Col xs={6}><strong>Device</strong></Col>
										<Col xs={6}><strong>Room</strong></Col>
									</Row>
								</Col>
								<Col xs={3}/>
							</Row>
						</ListGroupItem>
						{
							this.state.devices.map((device, i) =>
								<ListGroupItem key={i} className="settings-entry">
									<Row>
										{
											(this.state.editDevice.device === device) ?
												<div>
													<Col xs={9}>
														<Row>
															<Col xs={6}>
																<FormControl
																	type="text"
																	value={this.state.editDevice.name}
																	onChange={this.editNameChange.bind(this)}
																/>
															</Col>
															<Col xs={6}>
																<FormControl componentClass="select" value={this.state.editDevice.room ? this.state.editDevice.room.uid : "empty"} onChange={this.editRoomChange.bind(this)}>
																	<option value="empty" disabled>Select room</option>
																	{
																		this.state.rooms.map((room, i) => 
																			<option value={room.uid} key={i}>{room.name}</option>
																		)
																	}
																</FormControl>
															</Col>
														</Row>
													</Col>
													<Col xs={3}>
														<Row>
															<Col xs={6} onClick={this.finishEditing.bind(this)} className="col-center"><Button bsSize="small" bsStyle="success">Done</Button></Col>
															<Col xs={6} onClick={this.cancelEditing.bind(this)} className="col-center"><Button bsSize="small" bsStyle="warning">Cancel</Button></Col>
														</Row>
													</Col>
												</div>
											:
												<div>
													<Col xs={9}>
														<Row>
															<Col xs={6}>{device.config.name}</Col>
															<Col xs={6}>{
																(() => {
																	let room = this.getDeviceRoom(device);
																	return room ? room.name : <em>No room</em>
																}).call()
															}</Col>
														</Row>
													</Col>
													<Col xs={3}>
														<Row>
															<Col xs={6} onClick={() => this.startEditing(device)} className="col-center"><img src={editIcon} alt="edit"/></Col>
															<Col xs={6} onClick={() => this.showDeleteDialog(device)} className="col-center"><img src={deleteIcon} alt="delete"/></Col>
														</Row>
													</Col>
												</div>
										}
									</Row>
								</ListGroupItem>
							)
						}
					</ListGroup>
				</Col>
				<Col xs={0} sm={2} lg={3}/>
			</Row>
		</div>
	}
}

export default SetDevices