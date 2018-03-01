import React from 'react';
import { PageHeader } from 'react-bootstrap';
import Lorem from './Lorem.js';

const Overview = (props) => {
	return <div>
		<PageHeader>Overview</PageHeader>
		<h3>Hello, {props.name}</h3>
		<p>You currently have {props.devices.length} devices setup among {props.rooms.length} rooms.</p>
		<p>If you are stuck on what to do, feel free to check out the help page.</p>
	</div>
}

export default Overview
