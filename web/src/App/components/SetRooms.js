import React from 'react';
import './Settings.css';
import { PageHeader, Row, Col, ListGroup, ListGroupItem, FormControl, Button } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js'
import editIcon from '../res/edit.png';
import deleteIcon from '../res/delete.png';
import { arrayMove } from 'react-sortable-hoc';
import SortableDeviceList from "./SortableDevices.js"

class SetRooms extends React.Component{
	constructor(props){
		super(props);
		this.state = {
			devices: props.devices,
			rooms: props.rooms,
			deleteDialog: {
				shown: false,
				room: null
			},
			editRoom: {
				room: null, // Reference to original room, do not change unless confirmed
				name: null, // Name during editinfg
				devices: null, // List of devices during editing
				newly_added: false // If the room is newly added to be removed when its editing is cancelled
			}
		}
	}

	showDeleteDialog(room) {
		this.setState((prevState) => {
			if(prevState.deleteDialog.shown)
				return {};
			return {
				deleteDialog: {
					shown: true,
					room: room,
				}
			}
		});
	}

	hideDeleteDialog() {
		this.setState((prevState) => {
			return {
				deleteDialog: {
					shown: false,
					room: null
				}
			}
		})
	}

	deleteRoom() {
		this.setState((prevState) => {
			let roomIndex = prevState.rooms.indexOf(prevState.deleteDialog.room);
			prevState.rooms.splice(roomIndex, 1);
			prevState.deleteDialog = {
				shown: false,
				room: null
			};
			return prevState;
		})
	}

	preFinishEditing(prevState) {
		if(prevState.editRoom.room == null)
			return;
		prevState.editRoom.room.name = prevState.editRoom.name;
		prevState.editRoom.room.devices = prevState.editRoom.devices;
	}

	finishEditing() {
		this.setState((prevState) => {
			this.preFinishEditing(prevState);
			prevState.editRoom = {
				room: null,
				name: null,
				devices: null,
				newly_added: false
			}
			return prevState;
		})
	}

	startEditing(room) {
		this.setState((prevState) => {
			this.preFinishEditing(prevState);
			prevState.editRoom = {
				room: room,
				name: room.name,
				devices: room.devices.slice(), // Shallow copy
				newly_added: false
			}
			return prevState;
		})
	}

	cancelEditing() {
		this.setState((prevState) => {
			if(prevState.editRoom.newly_added) {
				let roomIndex = prevState.rooms.indexOf(prevState.editRoom.room);
				prevState.rooms.splice(roomIndex, 1);
			}
			prevState.editRoom = {
				room: null,
				name: null,
				devices: null,
				newly_added: false
			}
			return prevState;
		})
	}

	editNameChange(e) {
		let newName = e.target.value;
		this.setState((prevState) => {
			return {
				editRoom: {
					room: prevState.editRoom.room,
					name: newName,
					devices: prevState.editRoom.devices,
					newly_added: prevState.editRoom.newly_added
				}
			}
		})
	}

	editDeviceOrder({oldIndex, newIndex}) {
		this.setState((prevState) => {
			prevState.editRoom.devices = arrayMove(prevState.editRoom.devices, oldIndex, newIndex)
			return prevState;
		})
	}

	addRoom() {
		this.setState((prevState, props) => {
			this.preFinishEditing(prevState);
			let newRoom = {
				name: "",
				uid: /*TODO*/Math.round(Math.random() * (1 << 62)),
				devices: []
			};
			console.log(newRoom.uid);
			prevState.rooms.push(newRoom);
			prevState.editRoom = {
				room: newRoom,
				name: newRoom.name,
				devices: newRoom.devices,
				newly_added: true
			}
			return prevState;
		})
	}

	componentWillUnmount() {
		// setState does not execute inside componentWillUnmount
		// simply update the references stored in the state
		this.preFinishEditing(this.state);
	}

	render(){
		return <div>
			<PageHeader>Configure Rooms</PageHeader>
			{this.state.deleteDialog.shown ? <ConfirmDelete type="room" name={this.state.deleteDialog.room.name} onCancel={this.hideDeleteDialog.bind(this)} onDelete={this.deleteRoom.bind(this)}/> : <div/>}
			<Row>
				<Col xs={0} sm={2} lg={3}/>
				<Col xs={12} sm={8} lg={6}>
					<ListGroup>
						<ListGroupItem>
							<Row>
								<Col xs={9}><strong>Room</strong></Col>
								<Col xs={3} className="col-center"><Button onClick={this.addRoom.bind(this)}>New</Button></Col>
							</Row>
						</ListGroupItem>
						{
							this.state.rooms.map((room, i) =>
								<ListGroupItem key={i} className="settings-entry">
									<Row>
										{
											(this.state.editRoom.room === room) ?
												<div>
													<Row>
														<Col xs={9}>
															<FormControl
																type="text"
																value={this.state.editRoom.name}
																onChange={this.editNameChange.bind(this)}
															/>
														</Col>
														<Col xs={3}>
															<Row>
																<Col xs={6} onClick={this.finishEditing.bind(this)} className="col-center"><Button bsSize="small" bsStyle="success">Done</Button></Col>
																<Col xs={6} onClick={this.cancelEditing.bind(this)} className="col-center"><Button bsSize="small" bsStyle="warning">Cancel</Button></Col>
															</Row>
														</Col>
													</Row>
													<Row>
														<Col xsOffset={1} xs={10}>
															<SortableDeviceList devices={this.state.editRoom.devices.map(uid => this.state.devices.find(device => device.uid === uid)).filter(d => d)} onSortEnd={this.editDeviceOrder.bind(this)} lockAxis="y"/>
														</Col>
													</Row>
												</div>
											:
												<div>
													<Col xs={9}>{room.name}</Col>
													<Col xs={3}>
														<Row>
															<Col xs={6} onClick={() => this.startEditing(room)} className="col-center"><img src={editIcon} alt="edit"/></Col>
															<Col xs={6} onClick={() => this.showDeleteDialog(room)} className="col-center"><img src={deleteIcon} alt="delete"/></Col>
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

export default SetRooms