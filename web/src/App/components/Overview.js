import React from 'react';
import { PageHeader } from 'react-bootstrap';
import Lorem from './Lorem.js';

const Overview = (props) => {
	return <div>
		<PageHeader>Overview</PageHeader>
		<p>Hello, {props.name}</p>
	</div>
}

export default Overview
