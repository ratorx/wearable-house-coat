import React from 'react';
import { PageHeader, Row, Col, ListGroup, ListGroupItem, FormControl, Button } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js'
import SortableDeviceList from "./SortableDevices.js"
import { arrayMove } from 'react-sortable-hoc';
import './Settings.css';
import editIcon from '../res/edit.png';
import deleteIcon from '../res/delete.png';

class SetRooms extends React.Component{
	constructor(props){
		super(props);
		this.state = {
			deleteDialog: {
				shown: false,
				room: null
			},
			editRoom: {
				room: null,
				name: null,
				devices: null,
				newly_added: false
			}
		}
	}

	showDeleteDialog(room) {
		this.setState((prevState) => {
			if(!prevState.deleteDialog.shown) {
				return {
					deleteDialog: {
						shown: true,
						room: room
					}
				}
			}
		})
	}

	cancelDelete() {
		this.setState(() => {
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
			this.props.onDeleteRoom(prevState.deleteDialog.room);
			return {
				deleteDialog: {
					shown: false,
					room: null
				}
			}
		})
	}

	addRoom() {
		this.setState(() => {
			this.props.onSaveEdits(this.state.editRoom);
			let newRoom = this.props.onAddRoom();
			return {
				editRoom: {
					room: newRoom,
					name: newRoom.name,
					devices: newRoom.devices,
					newly_added: true
				}
			}
		})
	}

	startEditing(room) {
		this.setState(() => {
			this.props.onSaveEdits(this.state.editRoom);
			return {
				editRoom: {
					room: room,
					name: room.name,
					devices: room.devices.slice(), // Shallow copy
					newly_added: false
				}
			}
		})
	}

	cancelEditing() {
		this.setState((prevState) => {
			if(prevState.editRoom.newly_added) {
				let roomIndex = this.props.rooms.indexOf(prevState.editRoom.room);
				this.props.rooms.splice(roomIndex, 1);
			}
			return {
				editRoom: {
					room: null,
					name: null,
					devices: null,
					newly_added: false
				}
			}
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
			return {
				editRoom: {
					room: prevState.editRoom.room,
					name: prevState.editRoom.name,
					devices: arrayMove(prevState.editRoom.devices, oldIndex, newIndex),
					newly_added: prevState.editRoom.newly_added
				}
			}
		})
	}

	finishEditing() {
		this.setState((prevState) => {
			this.props.onSaveEdits(prevState.editRoom);
			return {
				editRoom: {
					room: null,
					name: null,
					devices: null,
					newly_added: false
				}
			}
		})
	}

	componentWillUnmount() {
		this.props.onSaveEdits(this.state.editRoom);
	}

	render(){
		return <div>
			<PageHeader>Configure Rooms</PageHeader>
			{this.state.deleteDialog.shown ? <ConfirmDelete type="room" name={this.state.deleteDialog.room.name} onCancel={this.cancelDelete.bind(this)} onDelete={this.deleteRoom.bind(this)}/> : <div/>}
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
						this.props.rooms.map((room, i) =>
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
															<SortableDeviceList devices={this.state.editRoom.devices.map(uid => this.props.devices.find(device => device.uid === uid))} onSortEnd={this.editDeviceOrder.bind(this)} lockAxis="y"/>
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