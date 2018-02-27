import React from 'react';
import { PageHeader, ListGroup, ListGroupItem, Row, Col, FormControl, Button } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js'
import './Settings.css';
import editIcon from '../res/edit.png';
import deleteIcon from '../res/delete.png';

class SetDevices extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			deleteDialog: {
				shown: false,
				device: null
			},
			editDevice: {
				device: null,
				room: null,
				name: null
			}
		};
	}

	showDeleteDialog(device) {
		this.setState((prevState) => {
			if(!prevState.deleteDialog.shown) {
				return {
					deleteDialog: {
						shown: true,
						device: device
					}
				}
			}
		});
	}

	cancelDelete() {
		this.setState((prevState) => {
			return {
				deleteDialog: {
					shown: false,
					device: null
				}
			}
		});
	}

	deleteDevice() {
		this.setState((prevState) => {
			this.props.onDeleteDevice(prevState.deleteDialog.device);
			return {
				deleteDialog: {
					shown: false,
					device: null
				}
			}
		});
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
		let uid = e.target.value;
		let newRoom = this.props.rooms.find((room) => room.uid === uid);
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

	startEditing(device) {
		this.setState((prevState) => {
			this.props.onSaveEdits(prevState.editDevice);
			let editRoom = this.props.rooms.find(room => room.devices.includes(device.uid));
			return {
				editDevice: {
					device: device,
					room: editRoom,
					name: device.name
				}
			}
		});
	}

	cancelEditing() {
		this.setState((prevState) => {
			return {
				editDevice: {
					device: null,
					room: null,
					name: null
				}
			}
		})
	}

	finishEditing() {
		this.setState((prevState) => {
			this.props.onSaveEdits(prevState.editDevice);
			return {
				editDevice: {
					device: null,
					room: null,
					name: null
				}
			}
		})
	}

	componentWillUnmount() {
		this.props.onSaveEdits(this.state.editDevice);
	}

	render() {
		return <div>
			<PageHeader>Configure Devices</PageHeader>
			{
				this.state.deleteDialog.shown ?
					<ConfirmDelete
						type="device"
						name={this.state.deleteDialog.device.name}
						onCancel={this.cancelDelete.bind(this)}
						onDelete={this.deleteDevice.bind(this)}
					/>
				:	null
			}
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
							this.props.devices.map((device, i) =>
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
																		this.props.rooms.map((room, i) => 
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
															<Col xs={6}>{device.name}</Col>
															<Col xs={6}>{
																(() => {
																	let room = this.props.rooms.find(room => room.devices.includes(device.uid))
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