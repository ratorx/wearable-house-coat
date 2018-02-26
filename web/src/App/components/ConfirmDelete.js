import React from 'react';
import { Modal, Button } from 'react-bootstrap';

const ConfirmDelete = (props) =>
	<Modal show>
		<Modal.Header>
			<Modal.Title>Confirm Delete</Modal.Title>
		</Modal.Header>

		<Modal.Body>Are you sure you want to delete {props.type} {props.name}</Modal.Body>

		<Modal.Footer>
			<Button onClick={props.onCancel}>Cancel</Button>
			<Button bsStyle="danger" onClick={props.onDelete}>Delete</Button>
		</Modal.Footer>
	</Modal>

export default ConfirmDelete