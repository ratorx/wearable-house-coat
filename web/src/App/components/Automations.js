import React from 'react';
import { PageHeader, ListGroup, ListGroupItem, Row, Col } from 'react-bootstrap';
import ConfirmDelete from './ConfirmDelete.js';
import Conditions from './automations/Conditions.js';

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
							<ListGroup key={i}>
								<ListGroupItem>
									<Row>
										<Col xs={1}><strong>IF</strong></Col>
										<Col xs={11}>
											<Conditions
												automation={automation}
												users={this.props.users}
												rooms={this.props.rooms}
											/>
										</Col>
									</Row>
								</ListGroupItem>
								<ListGroupItem>
									<Row>
										<Col xs={1}><strong>THEN</strong></Col>
										<Col xs={11}>
											Placeholder
										</Col>
									</Row>
								</ListGroupItem>
							</ListGroup>
						)
					}
				</Col>
				<Col xs={0} sm={2} lg={3}/>
			</Row>
		</div>
	}
}

export default Automations