import React from 'react';
import { Row, Col, FormControl, Button } from 'react-bootstrap';

class Conditions extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			conditions: Object.entries(props.automation.Locations).map(([user_uid, room_lowercase_name]) => {
				let user = props.users.find(usr => usr.uid === user_uid);
				let room = props.rooms.find(rm => rm.name.toLowerCase() === room_lowercase_name);
				return [user, room];
			})
		}
	}

	addCondition() {
		this.setState((prevState) => {
			prevState.conditions.push([undefined, undefined]);
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
			return {};
		})
	}

	render() {
		let automated_users = this.props.users.filter(user => this.state.conditions.map(([cond_user, cond_room]) => cond_user).filter(u => u).indexOf(user) === -1);
		return <div>
			{
				this.state.conditions.map(([user, room], i) =>
					<div key={i}>
						<Row>
							<Col xs={5}>
								<FormControl componentClass="select" value={user ? user.uid : "empty"} onChange={this.onUserChange.bind(this, i)}>
									<option value="empty" disabled>Select person</option>
									{
										(() => {
											let usrs = automated_users.slice();
											if(user) {
												usrs.unshift(user);
											}
											return usrs.map((usr, j) => {
												return <option value={usr.uid} key={j}>{usr.name}</option>
											})
										}).call()
									}
								</FormControl>
							</Col>
							<Col xs={1}>at</Col>
							<Col xs={6}>
								<FormControl componentClass="select" value={room ? room.uid : "empty"} onChange={this.onRoomChange.bind(this, i)}>
									<option value="empty" disabled>Select room</option>
									{
										this.props.rooms.map((rm, j) =>
											<option value={rm.uid} key={j}>{rm.name}</option>
										)
									}
								</FormControl>
							</Col>
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
						Add
					</Button>
				</Col>
			</Row>
		</div>
	}
}

export default Conditions