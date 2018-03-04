import React from 'react';
import { PageHeader, Row, Col, Well, Panel, Button } from 'react-bootstrap';
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
		this.setState((prevState) => {
			this.props.onDeleteAutomation(prevState.deleteDialog.automation)
			return {
				deleteDialog: {
					shown: false,
					automation: null
				}
			}
		})
	}

	showDeleteDialog(automation) {
		this.setState(() => {
			return {
				deleteDialog: {
					shown: true,
					automation: automation
				}
			}
		})
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
				<Col xs={12} smOffset={2} sm={8} lgOffset={3} lg={6}>
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
								<Row>
									<Col xs={12} className="col-right">
										<Button bsStyle="danger" onClick={this.showDeleteDialog.bind(this, automation)}>
											Delete Automation
										</Button>
									</Col>
								</Row>
							</Well>
						)
					}
				</Col>
			</Row>
			<Row>
				<Col xs={12} smOffset={2} sm={8} lgOffset={3} lg={6}>
					<Button onClick={this.props.onAddAutomation}>
						New Automation
					</Button>
				</Col>
			</Row>
		</div>
	}
}

export default Automations