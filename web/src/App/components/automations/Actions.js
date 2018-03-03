import React from 'react';
import { Panel, FormControl } from 'react-bootstrap';
import PhilipsHue from './PhilipsHue.js';
import Spotify from './Spotify.js';
import IFTTTLight from './IFTTTLight.js';

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
			})
		}
	}

	saveChanges() {
		this.props.setActions(this.state.actions.map(action => {
			return {
				device: action.device.uid,
				method: action.method,
				parameters: action.parameters,
				arguments: action.arguments
			}
		}))
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

	render() {
		return <div>
			{
				this.state.actions.map((action, i) =>
					<Panel key={i}>
						<Panel.Heading>
							<FormControl componentClass="select" value={action.device ? action.device.uid : "empty"} onChange={this.onDeviceChange.bind(this, action)}>
								<option value="empty" disabled>Select device</option>
								{
									this.props.devices.map((device, j) =>
										<option key={i + "." + j} value={device.uid}>{device.name}</option>
									)
								}
							</FormControl>
						</Panel.Heading>
						<Panel.Body>
							{
								action.device ? (
									(action.device.type === "PhilipsHue" || action.device.type === "HueGroup") ?
										<PhilipsHue
											action={action}
											onChange={this.onActionChange.bind(this, i)}
										/>
									: (action.device.type === "Spotify") ?
										<Spotify/>
									: (action.device.type === "IFTTTLight") ?
										<IFTTTLight/>
									: null
								) : null
							}	
						</Panel.Body>
					</Panel>
				)
			}
		</div>
	}
}

export default Actions;