import React from 'react';
import './App.css';
import Overview from './components/Overview.js'
import SetRooms from './components/SetRooms.js'
import SetDevices from './components/SetDevices.js'
import SetGroups from './components/SetGroups.js'
import Help from './components/Help.js'
import { Navbar, Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';

class App extends React.Component {
	constructor(){
		super();
		this.state = {
			pages: [
				{
					name: "Overview",
					component: <Overview/>
				},
				{
					name: "Setup",
					dropdown: [
						{
							name: "Rooms",
							component: <SetRooms/>
						},
						{
							name: "Devices",
							component: <SetDevices/>
						},
						{
							name: "Groups",
							component: <SetGroups/>
						}
					]
				},
				{
					name: "Help",
					component: <Help/>
				}
			]
		}
		this.state.currentPage = this.state.pages[0]
	}

	setCurrentPage(page) {
		this.setState(() => {return {currentPage: page}})
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
							this.state.pages.map((page, i) => {
								if(typeof page.dropdown === "undefined") {
									return <NavItem key={i} active={page.name === this.state.currentPage.name} onClick={() => {this.setCurrentPage(page)}}>{page.name}</NavItem>
								} else {
									return <NavDropdown key={i} title={page.name} id={"main-navbar-dropdown-" + page.name}>
										{
											page.dropdown.map((subpage, j) =>
												<MenuItem key={i + "." + j} active={subpage.name === this.state.currentPage.name} onClick={() => {this.setCurrentPage(subpage)}}>{subpage.name}</MenuItem>
											)
										}
									</NavDropdown>
								}
							})
						}
					</Nav>
				</Navbar.Collapse>
			</Navbar>
			{
				this.state.currentPage.component
			}
		</div>
	}
}

export default App;
