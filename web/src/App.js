import React, { Component } from 'react';
import './App.css';
import 'bootstrap-without-jquery';

class App extends Component {
	render() {
		return (
			<nav className="navbar navbar-default">
				<div className="container-fluid">
					<div className="navbar-header">
						<a className="navbar-brand" href="#">Wearable House Coat</a>
					</div>
					<ul className="nav navbar-nav">
						<li><a href="#">Page 1</a></li>
						<li className="active"><a href="#">Page 2</a></li>
						<li><a href="#">Page 3</a></li>
					</ul>
					<ul className="nav navbar-nav navbar-right">
						<li><a href="#">Link</a></li>
					</ul>
				</div>
			</nav>
		);
	}
}

export default App;
