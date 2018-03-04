import React from 'react';
import { Panel, FormControl, Row, Col, Button } from 'react-bootstrap';
import ConfirmDelete from '../ConfirmDelete.js'
import PhilipsHue from './PhilipsHue.js';
import Spotify from './Spotify.js';
import deleteIcon from '../../res/delete.png';

class Actions extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			actions: props.actions.map(action => {
				return {
					device: props.devices.find(device => device.uid === action.device),
					method: action.method,
					parameters: action.parameters,
					arguments: action.arguments
				}
			}),
			deleteDialog: {
				shown: false,
				action: null
			}
		}
	}

	saveChanges() {
		this.props.setActions(
			this.state.actions.filter(action => action.device).map(action => {
				return {
					device: action.device.uid,
					method: action.method,
					parameters: action.parameters,
					arguments: action.arguments
				}
			})
		)
	}

	showDeleteDialog(action) {
		this.setState((prevState) => {
			if(!prevState.deleteDialog.shown) {
				return {
					deleteDialog: {
						shown: true,
						action: action
					}
				}
			}
		});
	}

	cancelDelete() {
		this.setState(() => {
			return {
				deleteDialog: {
					shown: false,
					action: null
				}
			}
		});
	}

	deleteAction() {
		this.setState((prevState) => {
			let index = prevState.actions.indexOf(prevState.deleteDialog.action);
			prevState.actions.splice(index, 1);
			return {
				deleteDialog: {
					shown: false,
					action: null
				}
			}
		}, this.saveChanges);
	}

	onDeviceChange(action, e) {
		let newUid = e.target.value;
		this.setState(() => {
			action.device = this.props.devices.find(device => device.uid === newUid);
			action.method = null;
			action.parameters = null;
			action.arguments = null;
			return {}
		}, this.saveChanges)
	}

	onActionChange(i, newAction) {
		this.setState((prevState) => {
			prevState.actions[i] = newAction;
			return {}
		}, this.saveChanges)
	}

	addAction() {
		this.setState((prevState) => {
			prevState.actions.push({
				device: null,
				method: null,
				parameters: [null],
				arguments: null
			})
			// Do not call saveChanges. It will be called if any changes are made
			return {};
		})
	}

	render() {
		return <div>
			{
				this.state.deleteDialog.shown ?
					this.state.deleteDialog.action.device ?
						<ConfirmDelete
							type="action for device"
							name={this.state.deleteDialog.action.device.name}
							onCancel={this.cancelDelete.bind(this)}
							onDelete={this.deleteAction.bind(this)}
						/>
					:
						<ConfirmDelete
							type="this action"
							name=""
							onCancel={this.cancelDelete.bind(this)}
							onDelete={this.deleteAction.bind(this)}
						/>	
				:	null
			}
			{
				this.state.actions.map((action, i) =>
					<Panel key={i}>
						<Panel.Heading>
							<Row>
								<Col xs={10}>
									<FormControl componentClass="select" value={action.device ? action.device.uid : "empty"} onChange={this.onDeviceChange.bind(this, action)}>
										<option value="empty" disabled>Select device</option>
										{
											this.props.devices.map((device, j) =>
												<option key={i + "." + j} value={device.uid}>{device.name}</option>
											)
										}
									</FormControl>
								</Col>
								<Col className="col-center" xs={2}>
									<img src={deleteIcon} alt="delete action" onClick={() => this.showDeleteDialog(action)}/>
								</Col>
							</Row>
						</Panel.Heading>
						<Panel.Body>
							{
								action.device ? (
									(action.device.type === "PhilipsHue" || action.device.type === "HueGroup" || action.device.type === "IFTTTLight") ?
										<PhilipsHue
											action={action}
											onChange={this.onActionChange.bind(this, i)}
										/>
									: (action.device.type === "Spotify") ?
										<Spotify
											action={action}
											onChange={this.onActionChange.bind(this, i)}
										/>
									: null
								) : null
							}	
						</Panel.Body>
					</Panel>
				)
			}
			<Button onClick={this.addAction.bind(this)}>
				New Action
			</Button>
		</div>
	}
}

export default Actions;