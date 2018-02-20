"use strict"

import React from 'react';
import './App.css';
import Lorem from './Lorem.js';
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';

class App extends React.Component {
	constructor(){
		super();
		this.state = {
			Pages: [
				{name: "Overview"},
				{
					name: "Setup",
					dropdown: [
						{name: "Rooms"},
						{name: "Devices"},
						{name: "Groups"}
					]
				},
				{name: "Help"}
			],
			currentPage: "Overview"
		}
	}

	setCurrentPage(page) {
		this.setState(() => {return {currentPage: page.name}})
	}

	render() {
		return <div>
			<Navbar fixedTop inverse>
				<Navbar.Header>
					<Navbar.Brand>
						<a href="#home">Wearable House Coat</a>
					</Navbar.Brand>
					<Navbar.Toggle/>
				</Navbar.Header>
				<Navbar.Collapse>
					<Nav>
						{
							this.state.Pages.map((page, i) => {
								if(typeof page.dropdown === "undefined") {
									return <NavItem key={i} active={page.name === this.state.currentPage} onClick={() => {this.setCurrentPage(page)}}>{page.name}</NavItem>
								} else {
									return <NavDropdown key={i} title={page.name} id={"main-navbar-dropdown-" + page.name}>
										{
											page.dropdown.map((subpage, j) =>
												<MenuItem key={i + "." + j} active={subpage.name === this.state.currentPage} onClick={() => {this.setCurrentPage(subpage)}}>{subpage.name}</MenuItem>
											)
										}
									</NavDropdown>
								}
							})
						}
					</Nav>
				</Navbar.Collapse>
			</Navbar>
			<Lorem/>
		</div>
	}
}

export default App;
