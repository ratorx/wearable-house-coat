import React from 'react';
import { Row, Col, FormControl, Button } from 'react-bootstrap';
import ConfirmDelete from '../ConfirmDelete.js';
import deleteIcon from '../../res/delete.png';

class Conditions extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			conditions: Object.entries(props.conditions).map(([user_uid, room_lowercase_name]) => {
				let user = props.users.find(usr => usr.uid === user_uid);
				let room = props.rooms.find(rm => rm.name.toLowerCase() === room_lowercase_name);
				return [user, room];
			}),
			deleteDialog: {
				shown: false,
				condition: null
			}
		}
	}

	saveChanges(state) {
		let locations = {};
		state.conditions.forEach(([user, room]) => {
			if(user && room)
				locations[user.uid] = room.name.toLowerCase()
		});
		this.props.setLocations(locations);
	}

	addCondition() {
		this.setState((prevState) => {
			prevState.conditions.push([undefined, undefined]);
			this.saveChanges(prevState);
			return {};
		})
	}

	onUserChange(i, e) {
		let newUid = e.target.value;
		let user = this.props.users.find(usr => usr.uid === newUid);
		if(!user)
			return;
		this.setState((prevState) => {
			prevState.conditions[i][0] = user;
			this.saveChanges(prevState);
			return {};
		})
	}

	onRoomChange(i, e) {
		let newUid = e.target.value;
		let room = this.props.rooms.find(rm => rm.uid === newUid);
		if(!room)
			return;
		this.setState((prevState) => {
			prevState.conditions[i][1] = room;
			this.saveChanges(prevState);
			return {};
		})
	}

	showDeleteDialog(condition) {
		this.setState(() => {
			return {
				deleteDialog: {
					shown: true,
					condition: condition
				}
			}
		})
	}

	cancelDelete() {
		this.setState(() => {
			return {
				deleteDialog: {
					shown: false,
					condition: null
				}
			}
		})
	}

	deleteCondition() {
		this.setState((prevState) => {
			let conditionIndex = prevState.conditions.indexOf(prevState.deleteDialog.condition);
			prevState.conditions.splice(conditionIndex, 1);
			this.saveChanges(prevState);
			return {
				deleteDialog: {
					shown: false,
					condition: null
				}
			}
		})
	}

	render() {
		let automated_users = this.props.users.filter(user => this.state.conditions.map(([cond_user, cond_room]) => cond_user).filter(u => u).indexOf(user) === -1);
		return <div>
			{
				this.state.conditions.map((userroom, i) =>
					<div key={i}>
						{
							this.state.deleteDialog.shown ?
								<ConfirmDelete
									type="condition"
									name=""
									onCancel={this.cancelDelete.bind(this)}
									onDelete={this.deleteCondition.bind(this)}
								/>
							:	null
						}
						<Row>
							<Col xs={5}>
								<FormControl componentClass="select" value={userroom[0] ? userroom[0].uid : "empty"} onChange={this.onUserChange.bind(this, i)}>
									<option value="empty" disabled>Select person</option>
									{
										(() => {
											let usrs = automated_users.slice();
											if(userroom[0]) {
												usrs.unshift(userroom[0]);
											}
											return usrs.map((usr, j) => {
												return <option value={usr.uid} key={j}>{usr.name}</option>
											})
										}).call()
									}
								</FormControl>
							</Col>
							<Col xs={1}>in</Col>
							<Col xs={5}>
								<FormControl componentClass="select" value={userroom[1] ? userroom[1].uid : "empty"} onChange={this.onRoomChange.bind(this, i)}>
									<option value="empty" disabled>Select room</option>
									{
										this.props.rooms.map((rm, j) =>
											<option value={rm.uid} key={j}>{rm.name}</option>
										)
									}
								</FormControl>
							</Col>
							<Col xs={1} onClick={() => this.showDeleteDialog(userroom)} className="col-center"><img src={deleteIcon} alt="delete"/></Col>
						</Row>
						{
							(i !== this.state.conditions.length - 1) ?
								<Row><Col xs={2} xsOffset={1}>and</Col></Row>
							: null
						}
					</div>
				)
			}	
			<Row>
				<Col xs={12}>
					<Button onClick={this.addCondition.bind(this)}>
						New Condition
					</Button>
				</Col>
			</Row>
		</div>
	}
}

export default Conditions