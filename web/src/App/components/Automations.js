import React from 'react';
import { PageHeader, Row, Col, Well, Panel } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js';
import Conditions from './automations/Conditions.js';
import Actions from './automations/Actions.js';
import './Settings.css';

class Automations extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			deleteDialog: {
				shown: false,
				automation: null
			}
		}
	}

	cancelDelete() {
		this.setState(() => {
			return {
				deleteDialog: {
					shown: false,
					automation: null
				}
			}
		})
	}

	deleteAutomation() {
		console.log("TODO TODO TODO TODO-TODO-TODO-TODO-TODOOOOOOOOO TODODODODO");
	}

	setLocations(automation, loc) {
		automation.Locations = loc;
	}

	setEnterActions(automation, actions) {
		automation.Actions = actions
	}

	setLeaveActions(automation, actions) {
		automation.LeaveActions = actions
	}

	render() {
		return <div>
			<PageHeader>Automations</PageHeader>
			{
				this.state.deleteDialog.shown ?
					<ConfirmDelete
						type="this automation"
						name=""
						onCancel={this.cancelDelete.bind(this)}
						onDelete={this.deleteAutomation.bind(this)}
					/>
				:	null
			}
			<Row>
				<Col xs={0} sm={2} lg={3}/>
				<Col xs={12} sm={8} lg={6}>
					{
						this.props.automations.map((automation, i) =>
							<Well bsSize="large" key={i} className="settings-entry">
								<Panel>
									<Panel.Heading><strong>If</strong></Panel.Heading>
									<Panel.Body>
										<Conditions
											conditions={automation.Locations}
											users={this.props.users}
											rooms={this.props.rooms}
											setLocations={this.setLocations.bind(this, automation)}
										/>
									</Panel.Body>
								</Panel>
								<Panel>
									<Panel.Heading><strong>On entry</strong></Panel.Heading>
									<Panel.Body>
										<Actions
											devices={this.props.devices}
											actions={automation.Actions}
											setActions={this.setEnterActions.bind(this, automation)}
										/>
									</Panel.Body>
								</Panel>
								<Panel>
									<Panel.Heading><strong>On leave</strong></Panel.Heading>
									<Panel.Body>
										<Actions
											devices={this.props.devices}
											actions={automation.LeaveActions}
											setActions={this.setLeaveActions.bind(this, automation)}
										/>
									</Panel.Body>
								</Panel>
							</Well>
						)
					}
				</Col>
				<Col xs={0} sm={2} lg={3}/>
			</Row>
		</div>
	}
}

export default Automations