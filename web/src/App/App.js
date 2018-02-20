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
					component: null,
					onPageLoad: function() { // Do NOT use () => syntax as it breaks binding. (WTF?)
						this.component = <Overview/>
					}
				},
				{
					name: "Setup",
					dropdown: [
						{
							name: "Rooms",
							component: null,
							onPageLoad: function() {
								this.component = <SetRooms/>
							}
						},
						{
							name: "Devices",
							component: null,
							onPageLoad: function() {
								this.component = <SetDevices/>
							}
						},
						{
							name: "Groups",
							component: null,
							onPageLoad: function() {
								this.component = <SetGroups/>
							}
						}
					]
				},
				{
					name: "Help",
					component: null,
					onPageLoad: function() {
						this.component = <Help/>
					}
				}
			],
			currentPage: null
		};
		this.state.currentPage = this.state.pages[0];
		this.state.currentPage.onPageLoad.bind(this.state.currentPage).call();
	}

	setCurrentPage(page) {
		this.setState(() => {return {currentPage: page}});
		page.onPageLoad.bind(page)();
	}

	render() {
		return <div>
			<Navbar fixedTop inverse collapseOnSelect className="navbar-left-no-margin">
				<Navbar.Header>
					<Navbar.Brand>
						<a onClick={() => {this.setCurrentPage(this.state.pages[0])}}>Wearable House Coat</a>
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
